package com.ltfullstack.borrowingservice.command.controller;

import com.ltfullstack.borrowingservice.command.command.CreateBorrowingCommand;
import com.ltfullstack.borrowingservice.command.command.ReturnBookCommand;
import com.ltfullstack.borrowingservice.command.model.BorrowingRequestModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;


@RestController
@RequestMapping("api/v1/borrowing")
public class BorrowingCommandController {
    @Autowired
    private CommandGateway commandGateway;
    @PostMapping
    public String createBorrowing(@RequestBody BorrowingRequestModel model){
        CreateBorrowingCommand command =   new CreateBorrowingCommand(UUID.randomUUID().toString(),model.getBookId(),model.getEmployeeId(),new Date());
        return commandGateway.sendAndWait(command);
    }

    @PostMapping("/return")
    public String returnBook(@RequestBody ReturnBookRequest request){
        ReturnBookCommand command = new ReturnBookCommand(
                request.borrowingId,
                request.bookId,
                new Date()
        );
        return commandGateway.sendAndWait(command);
    }

    static class ReturnBookRequest {
        private String borrowingId;
        private String bookId;
    }

}
