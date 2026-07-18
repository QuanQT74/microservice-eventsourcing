package com.ltfullstack.bookservice.command.event;

import com.ltfullstack.bookservice.command.data.Book;
import com.ltfullstack.bookservice.command.data.BookRepository;
import com.ltfullstack.commonservice.event.BookRollBackEvent;
import com.ltfullstack.commonservice.event.BookUpadateStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookEventHandle {
    @Autowired
    private BookRepository bookRepository;

    @EventHandler
    public void on(BookCreatedEvent event){
        Book book = new Book();
        BeanUtils.copyProperties(event, book);
        bookRepository.save(book);
    }

    @EventHandler
    public void on(BookUpdateEvent event){
        bookRepository.findById(event.getId()).ifPresent(book -> {
            book.setName(event.getName());
            book.setAuthor(event.getAuthor());
            book.setIsReady(event.getIsReady());
            book.setImageUrl(event.getImageUrl());
            bookRepository.save(book);
        });
    }

    @EventHandler
    @DisallowReplay
    public void on(BookDeleteEvent event){
        try{
            if (!bookRepository.existsById(event.getId())) {
                log.warn("Book not found with id: {}", event.getId());
                return;
            }
            bookRepository.deleteById(event.getId());
            log.info("Book deleted: {}", event.getId());
        }catch (Exception ex){
            log.error("Error deleting book: {}", ex.getMessage());
        }
    }

    @EventHandler
    public void on(BookUpadateStatusEvent event){
        bookRepository.findById(event.getBookId()).ifPresent(book -> {
            book.setIsReady(event.getIsReady());
            bookRepository.save(book);
        });
    }

    @EventHandler
    public void on(BookRollBackEvent event){
        bookRepository.findById(event.getBookId()).ifPresent(book -> {
            book.setIsReady(event.getIsReady());
            bookRepository.save(book);
        });
    }
}
