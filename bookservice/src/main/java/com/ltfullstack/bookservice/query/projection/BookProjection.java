package com.ltfullstack.bookservice.query.projection;

import com.ltfullstack.bookservice.command.data.Book;
import com.ltfullstack.bookservice.command.data.BookRepository;
import com.ltfullstack.bookservice.query.model.BookResponseModel;
import com.ltfullstack.bookservice.query.queris.GetAllBookQuery;
import com.ltfullstack.bookservice.query.queris.GetBookDetailQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookProjection {
    @Autowired
    private BookRepository bookRepository;

    @QueryHandler
    public List<BookResponseModel> handle(GetAllBookQuery query){
        List<Book> list = bookRepository.findAll();
        List<BookResponseModel> listBook = new ArrayList<>();
        list.forEach(book -> {
            BookResponseModel model = new BookResponseModel();
            BeanUtils.copyProperties(book,model);
            listBook.add(model);
        });
        return  listBook;
    }
    @QueryHandler
    public BookResponseModel hanlde(GetBookDetailQuery query)throws Exception{
        BookResponseModel bookResponseModel = new BookResponseModel();
        Book book = bookRepository.findById(query.getId()).orElseThrow(() -> new Exception("Not found Book with BookId:" + query.getId()));
        BeanUtils.copyProperties(book,bookResponseModel);
        return  bookResponseModel;
    }
}
