package com.ltfullstack.borrowingservice.command.event;

import com.ltfullstack.borrowingservice.command.data.Borrowing;
import com.ltfullstack.borrowingservice.command.data.BorrowingRepository;
import com.ltfullstack.commonservice.event.BorrowingCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BorrowingEventsHandler {

    @Autowired
    private BorrowingRepository repository;

    @EventHandler
    public void on(BorrowingCreatedEvent event) {
        log.info("Persisting BorrowingCreatedEvent: id={}, bookId={}", event.getId(), event.getBookId());
        Borrowing borrowing = new Borrowing();
        borrowing.setId(event.getId());
        borrowing.setBookId(event.getBookId());
        borrowing.setEmployeeId(event.getEmployeeId());
        borrowing.setBorrwingDate(event.getBorrowingDate());
        borrowing.setStatus("BORROWED");
        repository.save(borrowing);
    }

    @EventHandler
    public void on(BorrowingDeleteEvent deleteEvent) {
        try {
            if (repository.existsById(deleteEvent.getId())) {
                repository.deleteById(deleteEvent.getId());
                log.info("Borrowing deleted: {}", deleteEvent.getId());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @EventHandler
    public void on(BorrowingReturnedEvent returnedEvent) {
        repository.findById(returnedEvent.getId()).ifPresent(borrowing -> {
            borrowing.setStatus("RETURNED");
            borrowing.setReturnData(returnedEvent.getReturnedDate());
            repository.save(borrowing);
            log.info("Borrowing returned: {}", returnedEvent.getId());
        });
    }
}
