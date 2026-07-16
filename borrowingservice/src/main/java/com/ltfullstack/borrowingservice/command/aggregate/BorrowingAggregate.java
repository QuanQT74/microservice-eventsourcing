package com.ltfullstack.borrowingservice.command.aggregate;

import com.ltfullstack.borrowingservice.command.command.CreateBorrowingCommand;
import com.ltfullstack.borrowingservice.command.command.DeleteBorrowingCommand;
import com.ltfullstack.borrowingservice.command.event.BorrowingDeleteEvent;
import com.ltfullstack.borrowingservice.command.event.BorrowingCreatedEvent;
import com.ltfullstack.borrowingservice.command.command.ReturnBookCommand;
import com.ltfullstack.borrowingservice.command.event.BorrowingReturnedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.util.Date;
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
    public BorrowingAggregate(CreateBorrowingCommand command){
        BorrowingCreatedEvent commadEvent = new BorrowingCreatedEvent();
        BeanUtils.copyProperties(command,commadEvent);
        AggregateLifecycle.apply(commadEvent);
    }
    @CommandHandler
    public void handle (DeleteBorrowingCommand command){
        BorrowingDeleteEvent event = new BorrowingDeleteEvent(command.getId());
        AggregateLifecycle.apply(event);
    }
    @CommandHandler
    public void handle(ReturnBookCommand command){
        BorrowingReturnedEvent event = new BorrowingReturnedEvent(
                command.getId(),
                command.getBookId(),
                command.getReturnedDate());
        AggregateLifecycle.apply(event);
    }
    @EventSourcingHandler
    public void on(BorrowingCreatedEvent brrowingCommadEvent){

        this.id = brrowingCommadEvent.getId();

        this.bookId = brrowingCommadEvent.getBookId();

        this.employeeId = brrowingCommadEvent.getEmployeeId();

        this.borrwingDate = brrowingCommadEvent.getBorrwingDate();
    }

    @EventSourcingHandler
    public void on(BorrowingDeleteEvent borrowingDeleteEvent){
        this.id = borrowingDeleteEvent.getId();
    }

    @EventSourcingHandler
    public  void on(BorrowingReturnedEvent borrowingReturnedEvent){
        this.id = borrowingReturnedEvent.getId();
        this.returnData = borrowingReturnedEvent.getReturnedDate();
        this.status = "RETURNED";
    }

}
