package com.ltfullstack.borrowingservice.command.Saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltfullstack.borrowingservice.command.command.DeleteBorrowingCommand;
import com.ltfullstack.borrowingservice.command.data.Borrowing;
import com.ltfullstack.borrowingservice.command.data.BorrowingRepository;
import com.ltfullstack.borrowingservice.command.event.BorrowingReturnedEvent;
import com.ltfullstack.commonservice.command.BookRollBackStatusCommand;
import com.ltfullstack.commonservice.command.UpdateStatusBookCommand;
import com.ltfullstack.commonservice.event.BookRollBackEvent;
import com.ltfullstack.commonservice.event.BookUpadateStatusEvent;
import com.ltfullstack.commonservice.event.BorrowingCreatedEvent;
import com.ltfullstack.commonservice.model.BookResponseCommandModel;
import com.ltfullstack.commonservice.model.BorrowingNotificationMessage;
import com.ltfullstack.commonservice.model.EmployeeResponseCommandModel;
import com.ltfullstack.commonservice.queries.GetBookDetailQuery;
import com.ltfullstack.commonservice.queries.GetDetailEmployeeQuery;
import com.ltfullstack.commonservice.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

/**
 * Orchestration Saga điều phối luồng mượn / trả sách giữa các service (Borrowing, Book, Employee).
 *
 * <p><b>Mượn sách (happy path):</b>
 * {@code BorrowingCreated} → check employee → check book → {@code UpdateStatusBook(isReady=false)}
 * → {@code BookUpadateStatus} → end</p>
 *
 * <p><b>Trả sách (happy path):</b>
 * {@code BorrowingReturned} → {@code UpdateStatusBook(isReady=true)} → {@code BookUpadateStatus} → end</p>
 */
@Slf4j
@Saga
public class BorrowingSaga {

    private static final long serialVersionUID = 1L;

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient KafkaService kafkaService;

    @Autowired
    private transient BorrowingRepository borrowingRepository;

    @Value("${user-service.url:http://userservice:9004}")
    private transient String userServiceUrl;

    // Không giữ RestTemplate/ObjectMapper ở field transient — Axon deserialize saga sẽ set null.
    private String borrowingId;
    private String bookId;
    private String employeeId;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    private void handle(BorrowingCreatedEvent event) {
        log.info("Saga START BorrowingCreatedEvent: id={}, bookId={}, employeeId={}",
                event.getId(), event.getBookId(), event.getEmployeeId());

        this.borrowingId = event.getId();
        this.bookId = event.getBookId();
        this.employeeId = event.getEmployeeId();

        try {
            validateEmployee(event.getEmployeeId());
            validateBookAvailable(event.getBookId());

            SagaLifecycle.associateWith("bookId", event.getBookId());
            commandGateway.sendAndWait(new UpdateStatusBookCommand(
                    event.getBookId(),
                    false,
                    event.getEmployeeId(),
                    event.getId()
            ));
        } catch (Exception ex) {
            log.error("Saga FAILED on BorrowingCreatedEvent: {}", ex.getMessage());
            rollbackBorrowingRecord(event.getId());
        }
    }

    @SagaEventHandler(associationProperty = "borrwingId", keyName = "id")
    private void handle(BookUpadateStatusEvent event) {
        try {
            String type = Boolean.FALSE.equals(event.getIsReady()) ? "BORROWED" : "RETURNED";
            String bookName = event.getBookId();
            try {
                BookResponseCommandModel book = queryGateway
                        .query(new GetBookDetailQuery(event.getBookId()),
                                ResponseTypes.instanceOf(BookResponseCommandModel.class))
                        .join();
                if (book != null && book.getName() != null) {
                    bookName = book.getName();
                }
            } catch (Exception ignored) {
            }

            if (kafkaService == null) {
                throw new IllegalStateException("KafkaService not injected into Saga");
            }

            String empId = event.getEmployeeId() != null ? event.getEmployeeId() : this.employeeId;
            if (empId == null || empId.isBlank()) {
                empId = resolveEmployeeIdFromBorrowing(event.getBorrwingId());
            }

            String email = null;
            String name = "Reader";
            Map<String, Object> user = resolveUserByEmployeeId(empId);
            if (user != null) {
                if (user.get("email") != null) {
                    email = String.valueOf(user.get("email"));
                }
                if (user.get("fullName") != null) {
                    name = String.valueOf(user.get("fullName"));
                }
            }

            if (email == null || email.isBlank()) {
                log.warn("Skip mail notify: no user email for employeeId={}, borrowingId={}",
                        empId, event.getBorrwingId());
                SagaLifecycle.end();
                return;
            }

            BorrowingNotificationMessage msg = BorrowingNotificationMessage.builder()
                    .type(type)
                    .borrowingId(event.getBorrwingId())
                    .bookId(event.getBookId())
                    .bookName(bookName)
                    .employeeId(empId)
                    .employeeEmail(email)
                    .employeeName(name)
                    .occurredAt(Instant.now().toString())
                    .build();
            String payload = new ObjectMapper().writeValueAsString(msg);
            log.info("Publishing mail notify type={} to={} topic=testMailTemplate", type, email);
            kafkaService.sendMessage("testMailTemplate", payload);
        } catch (Exception notifyEx) {
            log.error("Notify failed (ignore): {}", notifyEx.getMessage(), notifyEx);
        }
        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "borrwingId", keyName = "id")
    @EndSaga
    private void handle(BookRollBackEvent event) {
        log.info("Saga BookRollBackEvent: bookId={}, borrowingId={}",
                event.getBookId(), event.getBorrwingId());
        commandGateway.sendAndWait(new DeleteBorrowingCommand(event.getBorrwingId()));
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    private void handle(BorrowingReturnedEvent event) {
        log.info("Saga START BorrowingReturnedEvent: id={}, bookId={}", event.getId(), event.getBookId());

        this.borrowingId = event.getId();
        this.bookId = event.getBookId();
        this.employeeId = resolveEmployeeIdFromBorrowing(event.getId());

        try {
            SagaLifecycle.associateWith("bookId", event.getBookId());
            commandGateway.sendAndWait(new UpdateStatusBookCommand(
                    event.getBookId(),
                    true,
                    this.employeeId,
                    event.getId()
            ));
        } catch (Exception ex) {
            log.error("Saga FAILED on BorrowingReturnedEvent: {}", ex.getMessage());
            SagaLifecycle.end();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> resolveUserByEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            return null;
        }
        try {
            String base = (userServiceUrl != null && !userServiceUrl.isBlank())
                    ? userServiceUrl
                    : "http://userservice:9004";
            String url = base + "/api/v1/users/by-employee/" + employeeId;
            // Tạo mới mỗi lần — field transient của Saga bị null sau khi load từ saga store
            Map<String, Object> user = new RestTemplate().getForObject(url, Map.class);
            log.info("Resolved user email for employeeId={}: {}", employeeId,
                    user != null ? user.get("email") : null);
            return user;
        } catch (Exception ex) {
            log.warn("Could not resolve user by employeeId={}: {}", employeeId, ex.getMessage());
            return null;
        }
    }

    private String resolveEmployeeIdFromBorrowing(String borrowingId) {
        if (borrowingId == null || borrowingRepository == null) {
            return null;
        }
        try {
            return borrowingRepository.findById(borrowingId)
                    .map(Borrowing::getEmployeeId)
                    .orElse(null);
        } catch (Exception ex) {
            log.warn("Could not load borrowing {}: {}", borrowingId, ex.getMessage());
            return null;
        }
    }

    private void validateEmployee(String employeeId) {
        EmployeeResponseCommandModel employee = queryGateway
                .query(new GetDetailEmployeeQuery(employeeId),
                        ResponseTypes.instanceOf(EmployeeResponseCommandModel.class))
                .join();

        if (employee == null) {
            throw new IllegalStateException("Employee not found: " + employeeId);
        }
        if (Boolean.TRUE.equals(employee.getIsDisciplined())) {
            throw new IllegalStateException("Employee is disciplined and cannot borrow books");
        }
    }

    private void validateBookAvailable(String bookId) {
        BookResponseCommandModel book = queryGateway
                .query(new GetBookDetailQuery(bookId),
                        ResponseTypes.instanceOf(BookResponseCommandModel.class))
                .join();

        if (book == null) {
            throw new IllegalStateException("Book not found: " + bookId);
        }
        if (!Boolean.TRUE.equals(book.getIsReady())) {
            log.warn("Book {} already unavailable (likely locked by API) — saga continues", bookId);
        }
    }

    private void rollbackBorrowingRecord(String id) {
        commandGateway.sendAndWait(new DeleteBorrowingCommand(id));
        SagaLifecycle.end();
    }

    private void rollbackBookStatus(String bookId, String employeeId, String borrowingId) {
        commandGateway.sendAndWait(new BookRollBackStatusCommand(bookId, true, employeeId, borrowingId));
    }
}
