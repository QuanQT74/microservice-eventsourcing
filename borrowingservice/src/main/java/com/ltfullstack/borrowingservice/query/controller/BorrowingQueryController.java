package com.ltfullstack.borrowingservice.query.controller;

import com.ltfullstack.borrowingservice.query.model.BorrowingResponseModel;
import com.ltfullstack.borrowingservice.query.queries.GetActiveBorrowingQuery;
import com.ltfullstack.borrowingservice.query.queries.GetAllBorrowingQuery;
import com.ltfullstack.borrowingservice.query.queries.GetBorrowingByIdQuery;
import com.ltfullstack.borrowingservice.query.queries.GetBorrowingByUserQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/borrowings")
@RequiredArgsConstructor
@Tag(name = "Borrowing Query")
public class BorrowingQueryController {
    @Autowired
    private QueryGateway queryGateway;

    @Operation(summary = "Get all borrowings")
    @GetMapping
    public List<BorrowingResponseModel> getAll() {
        return queryGateway.query(
                new GetAllBorrowingQuery(),
                ResponseTypes.multipleInstancesOf(BorrowingResponseModel.class)
        ).join();
    }
    @Operation(summary = "Get borrowing by ID")
    @GetMapping("/{id}")
    public BorrowingResponseModel getById(@PathVariable String id){
        return queryGateway.query(
                new GetBorrowingByIdQuery(id),
                ResponseTypes.instanceOf(BorrowingResponseModel.class)
        ).join();
    }

    @Operation(summary = "Get borrowings by user")
    @GetMapping("/user/{userId}")
    public List<BorrowingResponseModel> GetbyUser(@PathVariable String userId){
        return queryGateway.query(
                new GetBorrowingByUserQuery(userId),
                ResponseTypes.multipleInstancesOf(BorrowingResponseModel.class)
        ).join();
    }
    @Operation(summary = "Get active borrowings")
    @GetMapping("/active")
    public List<BorrowingResponseModel> getActive() {
        return queryGateway.query(
                new GetActiveBorrowingQuery(),
                ResponseTypes.multipleInstancesOf(BorrowingResponseModel.class)
        ).join();
    }

}
