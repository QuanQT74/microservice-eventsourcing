package com.ltfullstack.bookservice.command.event;

import com.ltfullstack.bookservice.command.data.Book;
import com.ltfullstack.bookservice.command.data.BookRepository;
import com.ltfullstack.commonservice.event.BookUpadateStatusEvent;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Optional;
@Slf4j
@Component
public class BookEventHandle {
    @Autowired
    private BookRepository bookRepository;

    @EventHandler
    public void on(BookCreatedEvent event){
        Book book = new Book();
        BeanUtils.copyProperties(event,book);
        bookRepository.save(book);
    }

    @EventHandler
    public void on(BookUpdateEvent event){
        bookRepository.findById(event.getId()).ifPresent(book -> {
            book.setName(event.getName());
            book.setAuthor(event.getAuthor());
            book.setIsReady(event.getIsReady());
            bookRepository.save(book);
        });
    }
    @EventHandler
    @DisallowReplay
    public void on(BookDeleteEvent event){
        try{
            bookRepository.findById(event.getId()).orElseThrow(() -> new NotFoundException("Book not found"));
            bookRepository.deleteById(event.getId());
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
    }

    @EventHandler
    public void on(BookUpadateStatusEvent event){
        bookRepository.findById(event.getBookId()).ifPresent(book -> {
            book.setIsReady(event.getIsReady());
            bookRepository.save(book);
        });
    }
}
