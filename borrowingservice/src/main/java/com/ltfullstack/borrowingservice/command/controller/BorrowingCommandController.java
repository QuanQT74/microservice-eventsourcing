package com.ltfullstack.borrowingservice.command.controller;

import com.ltfullstack.borrowingservice.command.command.CreateBorrowingCommand;
import com.ltfullstack.borrowingservice.command.command.DeleteBorrowingCommand;
import com.ltfullstack.borrowingservice.command.command.ReturnBookCommand;
import com.ltfullstack.borrowingservice.command.model.BorrowingRequestModel;
import com.ltfullstack.borrowingservice.command.model.ReturnBookRequestModel;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
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
    
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("OK - Borrowing service is running");
    }
    
    @PostMapping
    public ResponseEntity<?> createBorrowing(@RequestBody BorrowingRequestModel model){
        System.out.println("=== RAW BODY ===");
        System.out.println("model = " + model);
        System.out.flush();
        
        try {
            if (model == null) {
                return ResponseEntity.badRequest().body("Request body is null");
            }
            
            System.out.println("Parsed model: bookId=" + model.getBookId() + ", employeeId=" + model.getEmployeeId());
            System.out.flush();
            
            if (model.getBookId() == null || model.getEmployeeId() == null) {
                return ResponseEntity.badRequest().body("bookId and employeeId are required");
            }
            
            String borrowingId = UUID.randomUUID().toString();
            System.out.println("Creating command with id: " + borrowingId);
            System.out.flush();
            
            CreateBorrowingCommand command = new CreateBorrowingCommand(
                    borrowingId,
                    model.getBookId(),
                    model.getEmployeeId(),
                    new Date()
            );
            
            System.out.println("Command created, sending to Axon...");
            System.out.flush();
            
            String result = commandGateway.sendAndWait(command);
            System.out.println("Command result: " + result);
            System.out.flush();
            
            return ResponseEntity.ok(java.util.Map.of(
                    "success", true,
                    "borrowingId", result
            ));
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            System.out.flush();
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody ReturnBookRequestModel request){
        log.info("=== RETURN REQUEST RECEIVED ===");
        log.info("request = {}", request);
        log.info("request.getId() = {}", request != null ? request.getId() : "null");
        
        try {
            String borrowingId = request.getId();
            log.info("Returning book for borrowingId: {}, bookId: {}", borrowingId, request.getBookId());
            
            ReturnBookCommand command = new ReturnBookCommand(
                    borrowingId,
                    request.getBookId(),
                    new Date()
            );
            
            String result = commandGateway.sendAndWait(command);
            log.info("Book returned successfully: {}", result);
            
            return ResponseEntity.ok(java.util.Map.of(
                    "success", true,
                    "result", result
            ));
        } catch (Exception e) {
            log.error("Error returning book: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{borrowingId}")
    public ResponseEntity<?> deleteBorrowing(@PathVariable String borrowingId) {
        log.info("Deleting borrowing: {}", borrowingId);
        try {
            DeleteBorrowingCommand command = new DeleteBorrowingCommand(borrowingId);
            commandGateway.sendAndWait(command);
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
