package com.ltfullstack.bookservice.command.aggregate;

import com.ltfullstack.bookservice.command.command.CreateCommandBook;
import com.ltfullstack.bookservice.command.command.DeleteCommandBook;
import com.ltfullstack.bookservice.command.command.UpdateCommandBook;
import com.ltfullstack.bookservice.command.event.BookCreatedEvent;
import com.ltfullstack.bookservice.command.event.BookDeleteEvent;
import com.ltfullstack.bookservice.command.event.BookUpdateEvent;
import com.ltfullstack.commonservice.command.BookRollBackStatusCommand;
import com.ltfullstack.commonservice.command.UpdateStatusBookCommand;
import com.ltfullstack.commonservice.event.BookRollBackEvent;
import com.ltfullstack.commonservice.event.BookUpadateStatusEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;


@Aggregate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookAggregate {
    @AggregateIdentifier
    String id;

    String name;

    String author;

    Boolean isReady;

    @CommandHandler
    public BookAggregate(CreateCommandBook command){
        BookCreatedEvent bookCreatedEvent = new BookCreatedEvent();
        BeanUtils.copyProperties(command,bookCreatedEvent);

        AggregateLifecycle.apply(bookCreatedEvent);
    }

    @CommandHandler
    public void handle(UpdateCommandBook commandBook){
        BookUpdateEvent bookUpdateEvent = new BookUpdateEvent();
        BeanUtils.copyProperties(commandBook,bookUpdateEvent);

        AggregateLifecycle.apply(bookUpdateEvent);
    }

    @CommandHandler
    public void handle(DeleteCommandBook commandBook){
        BookDeleteEvent bookDeleteEvent = new BookDeleteEvent();

        BeanUtils.copyProperties(commandBook,bookDeleteEvent);

        AggregateLifecycle.apply(bookDeleteEvent);
    }

    @CommandHandler
    public void handle(UpdateStatusBookCommand updateStatusBookCommand){
        BookUpadateStatusEvent statusEvent = new BookUpadateStatusEvent();
        BeanUtils.copyProperties(updateStatusBookCommand,statusEvent);

        AggregateLifecycle.apply(statusEvent);
    }

    @CommandHandler void handle(BookRollBackStatusCommand rollBackStatusCommand){
        BookRollBackEvent rollBackEvent = new BookRollBackEvent();
        BeanUtils.copyProperties(rollBackStatusCommand,rollBackEvent);

        AggregateLifecycle.apply(rollBackEvent);

    }
    @EventSourcingHandler
    public  void on(BookCreatedEvent event){
        this.id = event.getId();
        this.name = event.getName();
        this.author = event.getAuthor();
        this.isReady = event.getIsReady();
    }

    @EventSourcingHandler
    public void on(BookUpdateEvent event){
        this.id = event.getId();
        this.name = event.getName();
        this.author = event.getAuthor();
        this.isReady = event.getIsReady();
    }
    @EventSourcingHandler
    public void on(BookDeleteEvent event){
        AggregateLifecycle.markDeleted();
    }

    @EventSourcingHandler
    public void on (BookUpadateStatusEvent event){
        this.id = event.getBookId();
        this.isReady = event.getIsReady();
    }

    @EventSourcingHandler
    public void on(BookRollBackEvent rollBackEvent){
        this.id = rollBackEvent.getBookId();
        this.isReady = rollBackEvent.getIsReady();
    }
}
