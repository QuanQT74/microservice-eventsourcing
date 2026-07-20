package com.ltfullstack.borrowingservice.command.controller;

import com.ltfullstack.borrowingservice.command.command.CreateBorrowingCommand;
import com.ltfullstack.borrowingservice.command.command.DeleteBorrowingCommand;
import com.ltfullstack.borrowingservice.command.command.ReturnBookCommand;
import com.ltfullstack.borrowingservice.command.data.Borrowing;
import com.ltfullstack.borrowingservice.command.data.BorrowingRepository;
import com.ltfullstack.borrowingservice.command.model.BorrowingRequestModel;
import com.ltfullstack.borrowingservice.command.model.ReturnBookRequestModel;
import com.ltfullstack.commonservice.command.UpdateStatusBookCommand;
import com.ltfullstack.commonservice.model.BookResponseCommandModel;
import com.ltfullstack.commonservice.queries.GetBookDetailQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;


@RestController
@RequestMapping("api/v1/borrowing")
@Slf4j
public class BorrowingCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private QueryGateway queryGateway;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("OK - Borrowing service is running");
    }

    @PostMapping
    public ResponseEntity<?> createBorrowing(@RequestBody BorrowingRequestModel model) {
        log.info("=== CREATE BORROWING (Saga) === bookId={}, employeeId={}",
                model.getBookId(), model.getEmployeeId());

        try {
            if (model.getBookId() == null || model.getEmployeeId() == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                        "success", false,
                        "error", "bookId and employeeId are required"
                ));
            }

            // Chặn mượn trùng: user đã đang giữ sách này
            if (borrowingRepository.existsByEmployeeIdAndBookIdAndStatus(
                    model.getEmployeeId(), model.getBookId(), "BORROWED")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of(
                        "success", false,
                        "error", "You already have this book on loan. Return it before borrowing again."
                ));
            }

            // Chặn mượn khi sách đang được người khác (hoặc chính mình) giữ
            if (borrowingRepository.existsByBookIdAndStatus(model.getBookId(), "BORROWED")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of(
                        "success", false,
                        "error", "This book is already borrowed."
                ));
            }

            // Kiểm tra isReady từ Book service
            try {
                BookResponseCommandModel book = queryGateway
                        .query(new GetBookDetailQuery(model.getBookId()),
                                ResponseTypes.instanceOf(BookResponseCommandModel.class))
                        .join();
                if (book == null || !Boolean.TRUE.equals(book.getIsReady())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of(
                            "success", false,
                            "error", "This book is not available."
                    ));
                }
            } catch (Exception ex) {
                log.warn("Could not query book detail: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(java.util.Map.of(
                        "success", false,
                        "error", "Unable to verify book availability. Try again."
                ));
            }

            String borrowingId = UUID.randomUUID().toString();
            Date now = new Date();

            Borrowing borrowing = new Borrowing();
            borrowing.setId(borrowingId);
            borrowing.setBookId(model.getBookId());
            borrowing.setEmployeeId(model.getEmployeeId());
            borrowing.setBorrwingDate(now);
            borrowing.setStatus("BORROWED");
            borrowingRepository.save(borrowing);

            CreateBorrowingCommand command = new CreateBorrowingCommand(
                    borrowingId,
                    model.getBookId(),
                    model.getEmployeeId(),
                    now
            );
            commandGateway.sendAndWait(command);

            // Đánh dấu sách không sẵn ngay — không đợi Saga async (tránh mượn trùng)
            try {
                commandGateway.sendAndWait(new UpdateStatusBookCommand(
                        model.getBookId(), false, model.getEmployeeId(), borrowingId
                ));
            } catch (Exception ex) {
                log.warn("Sync UpdateStatusBook failed (Saga will retry): {}", ex.getMessage());
            }

            return ResponseEntity.accepted().body(java.util.Map.of(
                    "success", true,
                    "borrowingId", borrowingId,
                    "message", "Borrowing created - Saga orchestrating"
            ));
        } catch (Exception e) {
            log.error("ERROR creating borrowing: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody ReturnBookRequestModel request) {
        log.info("=== RETURN BOOK (Saga) === borrowingId={}, bookId={}",
                request.getId(), request.getBookId());

        try {
            if (request.getId() == null || request.getBookId() == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                        "success", false,
                        "error", "id and bookId are required"
                ));
            }

            Date returnedDate = new Date();
            ReturnBookCommand command = new ReturnBookCommand(
                    request.getId(),
                    request.getBookId(),
                    returnedDate
            );
            commandGateway.sendAndWait(command);

            borrowingRepository.findById(request.getId()).ifPresentOrElse(borrowing -> {
                borrowing.setStatus("RETURNED");
                borrowing.setReturnData(returnedDate);
                borrowingRepository.save(borrowing);
                log.info("Read-model marked RETURNED: {}", request.getId());
            }, () -> log.warn("Borrowing not found in DB for return: {}", request.getId()));

            // Mở lại sách ngay để có thể mượn tiếp sau khi trả
            try {
                commandGateway.sendAndWait(new UpdateStatusBookCommand(
                        request.getBookId(), true, null, request.getId()
                ));
            } catch (Exception ex) {
                log.warn("Sync UpdateStatusBook (return) failed: {}", ex.getMessage());
            }

            return ResponseEntity.ok(java.util.Map.of(
                    "success", true,
                    "message", "Book returned successfully"
            ));
        } catch (Exception e) {
            log.error("ERROR returning book: {}", e.getMessage(), e);
            String message = e.getMessage() != null ? e.getMessage() : "Unknown error";
            if (message.contains("aggregate was not found")) {
                message = "Borrowing not found in event store. Please borrow again via Saga, then return.";
            }
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                    "success", false,
                    "error", message
            ));
        }
    }

    @DeleteMapping("/{borrowingId}")
    public ResponseEntity<?> deleteBorrowing(@PathVariable String borrowingId) {
        log.info("Deleting borrowing: {}", borrowingId);
        try {
            commandGateway.sendAndWait(new DeleteBorrowingCommand(borrowingId));
            if (borrowingRepository.existsById(borrowingId)) {
                borrowingRepository.deleteById(borrowingId);
            }
            return ResponseEntity.ok(java.util.Map.of("success", true));
        } catch (Exception e) {
            log.error("Error deleting borrowing: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}
