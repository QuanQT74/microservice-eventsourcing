package com.ltfullstack.borrowingservice.command.aggregate;

import com.ltfullstack.borrowingservice.command.command.CreateBorrowingCommand;
import com.ltfullstack.borrowingservice.command.command.DeleteBorrowingCommand;
import com.ltfullstack.borrowingservice.command.command.ReturnBookCommand;
import com.ltfullstack.borrowingservice.command.event.BorrowingDeleteEvent;
import com.ltfullstack.borrowingservice.command.event.BorrowingReturnedEvent;
import com.ltfullstack.commonservice.event.BorrowingCreatedEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Date;

@Slf4j
@Data
@NoArgsConstructor
@Aggregate
public class BorrowingAggregate {

    @AggregateIdentifier
    private String id;
    private String bookId;
    private String employeeId;
    private Date borrwingDate;
    private Date returnData;
    private String status;

    @CommandHandler
    public BorrowingAggregate(CreateBorrowingCommand command) {
        log.info("BorrowingAggregate CreateBorrowingCommand: id={}, bookId={}, employeeId={}",
                command.getId(), command.getBookId(), command.getEmployeeId());

        BorrowingCreatedEvent event = new BorrowingCreatedEvent();
        event.setId(command.getId());
        event.setBookId(command.getBookId());
        event.setEmployeeId(command.getEmployeeId());
        event.setBorrowingDate(command.getBorrwingDate());
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void handle(DeleteBorrowingCommand command) {
        log.info("BorrowingAggregate DeleteBorrowingCommand: {}", command.getId());
        AggregateLifecycle.apply(new BorrowingDeleteEvent(command.getId()));
    }

    /**
     * CREATE_IF_MISSING: cho phép trả sách với borrowing cũ chỉ có trong MySQL
     * (chưa từng có trong Axon Event Store).
     */
    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void handle(ReturnBookCommand command) {
        log.info("BorrowingAggregate ReturnBookCommand: {}", command.getId());
        AggregateLifecycle.apply(new BorrowingReturnedEvent(
                command.getId(),
                command.getBookId(),
                command.getReturnedDate()
        ));
    }

    @EventSourcingHandler
    public void on(BorrowingCreatedEvent event) {
        this.id = event.getId();
        this.bookId = event.getBookId();
        this.employeeId = event.getEmployeeId();
        this.borrwingDate = event.getBorrowingDate();
        this.status = "BORROWED";
    }

    @EventSourcingHandler
    public void on(BorrowingDeleteEvent event) {
        this.id = event.getId();
    }

    @EventSourcingHandler
    public void on(BorrowingReturnedEvent event) {
        this.id = event.getId();
        this.bookId = event.getBookId();
        this.returnData = event.getReturnedDate();
        this.status = "RETURNED";
    }
}
