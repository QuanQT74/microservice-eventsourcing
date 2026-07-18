package com.ltfullstack.borrowingservice.query.projection;

import com.ltfullstack.borrowingservice.command.data.Borrowing;
import com.ltfullstack.borrowingservice.command.data.BorrowingRepository;
import com.ltfullstack.borrowingservice.query.model.BorrowingResponseModel;
import com.ltfullstack.borrowingservice.query.queries.GetAllBorrowingQuery;
import com.ltfullstack.borrowingservice.query.queries.GetBorrowingByIdQuery;
import com.ltfullstack.commonservice.model.BookResponseCommandModel;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ltfullstack.borrowingservice.query.queries.*;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class BorrowingProjection {

    @Autowired
    private BorrowingRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();

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
        BorrowingResponseModel model = new BorrowingResponseModel(
                borrowing.getId(),
                borrowing.getBookId(),
                borrowing.getEmployeeId(),
                borrowing.getBorrwingDate(),
                borrowing.getReturnData(),
                borrowing.getStatus(),
                null,
                null
        );
        
        // Fetch book info from book service
        try {
            String bookServiceUrl = "http://bookservice:9001/api/v1/books/" + borrowing.getBookId();
            BookResponseCommandModel book = restTemplate.getForObject(bookServiceUrl, BookResponseCommandModel.class);
            if (book != null) {
                model.setBookName(book.getName());
                model.setBookAuthor(book.getAuthor());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch book info for {}: {}", borrowing.getBookId(), e.getMessage());
            model.setBookName("Unknown Book");
            model.setBookAuthor("Unknown Author");
        }
        
        return model;
    }
}
