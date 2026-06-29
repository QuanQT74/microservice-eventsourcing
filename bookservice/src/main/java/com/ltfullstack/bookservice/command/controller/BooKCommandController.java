package com.ltfullstack.bookservice.command.controller;

import com.ltfullstack.bookservice.command.command.CreateCommandBook;
import com.ltfullstack.bookservice.command.command.DeleteCommandBook;
import com.ltfullstack.bookservice.command.command.UpdateCommandBook;
import com.ltfullstack.bookservice.command.model.BookRequestModel;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/books")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BooKCommandController {
    @Autowired
    CommandGateway commandGateway;

    @PostMapping
    public String addBook(@Valid @RequestBody BookRequestModel model){
        CreateCommandBook command = new CreateCommandBook(UUID.randomUUID().toString(),model.getName(),model.getAuthor(),true);
        return commandGateway.sendAndWait(command);
    }

    @PutMapping({"/{bookId}"})
    public String updateBook( @RequestBody BookRequestModel model , @PathVariable String bookId){
        UpdateCommandBook updateCommonandBook = new UpdateCommandBook(bookId,model.getName(),model.getAuthor(),model.getIsReady());
        return commandGateway.sendAndWait(updateCommonandBook);
    }
    @DeleteMapping({"/{bookId}"})
    public String deleteBook(@PathVariable String bookId){
        DeleteCommandBook deleteCommandBook = new DeleteCommandBook(bookId);
        return commandGateway.sendAndWait(deleteCommandBook);
    }

}