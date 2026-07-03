package com.ltfullstack.bookservice.command.controller;

import com.ltfullstack.bookservice.command.command.CreateCommandBook;
import com.ltfullstack.bookservice.command.command.DeleteCommandBook;
import com.ltfullstack.bookservice.command.command.UpdateCommandBook;
import com.ltfullstack.bookservice.command.model.BookRequestModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Book Command")
public class BooKCommandController {
    @Autowired
    CommandGateway commandGateway;

    @Operation(
            summary = "Create a new book",
            description = "Create a new book and publish a BookCreatedEvent to Axon Server.",
            tags = {"Book Command"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book created successfully"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid book information"),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error")
            }
    )
    @PostMapping
    public String addBook(@Valid @RequestBody BookRequestModel model){
        CreateCommandBook command = new CreateCommandBook(UUID.randomUUID().toString(),model.getName(),model.getAuthor(),true);
        return commandGateway.sendAndWait(command);
    }

    @Operation(
            summary = "Update an existing book",
            description = "Update book information by book ID and publish a BookUpdatedEvent.",
            tags = {"Book Command"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book updated successfully"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Book not found"),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error")
            }
    )

    @PutMapping({"/{bookId}"})
    public String updateBook( @RequestBody BookRequestModel model , @PathVariable String bookId){
        UpdateCommandBook updateCommonandBook = new UpdateCommandBook(bookId,model.getName(),model.getAuthor(),model.getIsReady());
        return commandGateway.sendAndWait(updateCommonandBook);
    }

    @Operation(
            summary = "Delete a book",
            description = "Delete a book by book ID and publish a BookDeletedEvent.",
            tags = {"Book Command"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book deleted successfully"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Book not found"),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error")
            }
    )
    @DeleteMapping({"/{bookId}"})
    public String deleteBook(@PathVariable String bookId){
        DeleteCommandBook deleteCommandBook = new DeleteCommandBook(bookId);
        return commandGateway.sendAndWait(deleteCommandBook);
    }

}