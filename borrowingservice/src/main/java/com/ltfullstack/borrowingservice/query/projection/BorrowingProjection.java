package com.ltfullstack.borrowingservice.query.projection;

import com.ltfullstack.borrowingservice.command.data.Borrowing;
import com.ltfullstack.borrowingservice.command.data.BorrowingRepository;
import com.ltfullstack.borrowingservice.query.model.BorrowingResponseModel;
import com.ltfullstack.borrowingservice.query.queries.GetAllBorrowingQuery;
import com.ltfullstack.borrowingservice.query.queries.GetBorrowingByIdQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ltfullstack.borrowingservice.query.queries.*;


import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class BorrowingProjection {

    @Autowired
    private BorrowingRepository repository;

    @QueryHandler
    public List<BorrowingResponseModel> handle(GetAllBorrowingQuery query) {
        return repository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @QueryHandler
    public BorrowingResponseModel handle(GetBorrowingByIdQuery query){
        return repository.findById(query.getId())
                .map(this::toModel)
                .orElse(null);
    }
    @QueryHandler
    public  List<BorrowingResponseModel> handle(GetActiveBorrowingQuery query){
        return repository.findByStatus("BORROWED").stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
    @QueryHandler
    public List<BorrowingResponseModel> handle(GetBorrowingByUserQuery query) {
        return repository.findByEmployeeId(query.getUserId()).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
    private BorrowingResponseModel toModel(Borrowing borrowing){
        return new BorrowingResponseModel(
                borrowing.getId(),
                borrowing.getBookId(),
                borrowing.getEmployeeId(),
                borrowing.getBorrwingDate(),
                borrowing.getReturnData(),
                borrowing.getStatus()
        );
    }
}
