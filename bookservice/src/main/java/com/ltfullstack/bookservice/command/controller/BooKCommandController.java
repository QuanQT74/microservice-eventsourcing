package com.ltfullstack.bookservice.command.controller;

import com.ltfullstack.bookservice.command.command.CreateCommandBook;
import com.ltfullstack.bookservice.command.command.DeleteCommandBook;
import com.ltfullstack.bookservice.command.command.UpdateCommandBook;
import com.ltfullstack.bookservice.command.data.Book;
import com.ltfullstack.bookservice.command.data.BookRepository;
import com.ltfullstack.bookservice.command.model.BookRequestModel;
import com.ltfullstack.commonservice.command.UpdateStatusBookCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/books")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Tag(name = "Book Command")
public class BooKCommandController {
    @Autowired
    CommandGateway commandGateway;
    
    @Autowired
    BookRepository bookRepository;
    
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
        String bookId = UUID.randomUUID().toString();
        
        // Save to DB directly
        Book book = Book.builder()
            .id(bookId)
            .name(model.getName())
            .author(model.getAuthor())
            .isReady(true)
            .imageUrl(model.getImageUrl())
            .build();
        bookRepository.save(book);
        
        // Send command for event sourcing
        CreateCommandBook command = new CreateCommandBook(bookId, model.getName(), model.getAuthor(), true, model.getImageUrl());
        commandGateway.send(command);
        
        return bookId;
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
        // Update DB directly
        bookRepository.findById(bookId).ifPresent(book -> {
            book.setName(model.getName());
            book.setAuthor(model.getAuthor());
            book.setIsReady(model.getIsReady());
            book.setImageUrl(model.getImageUrl());
            bookRepository.save(book);
        });
        
        // Send command for event sourcing
        UpdateCommandBook updateCommandBook = new UpdateCommandBook(bookId, model.getName(), model.getAuthor(), model.getIsReady(), model.getImageUrl());
        commandGateway.send(updateCommandBook);
        
        return bookId;
    }
    
    @PostMapping("/update-status/{bookId}")
    public String updateBookStatus(@PathVariable String bookId, @RequestParam boolean isReady) {
        // Update DB directly
        bookRepository.findById(bookId).ifPresent(book -> {
            book.setIsReady(isReady);
            bookRepository.save(book);
        });
        
        // Send command for event sourcing
        UpdateStatusBookCommand command = new UpdateStatusBookCommand(bookId, isReady, null, null);
        commandGateway.send(command);
        
        return bookId;
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
        // Delete from DB directly
        bookRepository.deleteById(bookId);
        
        // Send command for event sourcing
        DeleteCommandBook deleteCommandBook = new DeleteCommandBook(bookId);
        commandGateway.send(deleteCommandBook);
        
        return bookId;
    }

}