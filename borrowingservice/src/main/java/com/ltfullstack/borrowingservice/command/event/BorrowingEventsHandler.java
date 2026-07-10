package com.ltfullstack.borrowingservice.command.event;

import com.ltfullstack.borrowingservice.command.data.Borrowing;
import com.ltfullstack.borrowingservice.command.data.BorrowingRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BorrowingEventsHandler {
    @Autowired
    BorrowingRepository repository ;

    @EventHandler
    public void on(BorrowingCreatedEvent createdEvent){
        Borrowing borrowing = new Borrowing();

        borrowing.setId(createdEvent.getId());

        borrowing.setBookId(createdEvent.getBookId());
        borrowing.setEmployeeId(createdEvent.getEmployeeId());
        borrowing.setBorrwingDate(createdEvent.getBorrwingDate());

        repository.save(borrowing);
    }

    @EventHandler
    public void on(BorrowingDeleteEvent deleteEvent){
        try{
            repository.findById(deleteEvent.getId()).orElseThrow(() -> new NotFoundException("Book not found"));
            repository.deleteById(deleteEvent.getId());
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
    }

}
