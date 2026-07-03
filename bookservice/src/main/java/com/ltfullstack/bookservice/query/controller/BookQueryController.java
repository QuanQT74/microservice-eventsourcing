package com.ltfullstack.bookservice.query.controller;

import com.ltfullstack.bookservice.query.model.BookResponseModel;
import com.ltfullstack.bookservice.query.queris.GetAllBookQuery;
import com.ltfullstack.bookservice.query.queris.GetBookDetailQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.hibernate.annotations.OptimisticLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@Tag(name = "Book Query")
public class BookQueryController {
    @Autowired
    private QueryGateway queryGateway;

    @Operation(
            summary = "Get all book",
            description = "Retrieve a list of all books from the query database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Books retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @GetMapping
    public List<BookResponseModel> getAllBooks(){
        GetAllBookQuery query = new GetAllBookQuery();
        return queryGateway.query(query, ResponseTypes.multipleInstancesOf(BookResponseModel.class)).join();
    }

    @Operation(
            summary = "Get book details",
            description = "Retrieve detailed information of a book by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book found"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Book not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @GetMapping("/{bookId}")
    public BookResponseModel getBookDetail(@PathVariable String bookId){
        GetBookDetailQuery query = new GetBookDetailQuery(bookId);
        return queryGateway.query(query,ResponseTypes.instanceOf(BookResponseModel.class)).join();
    }
}
