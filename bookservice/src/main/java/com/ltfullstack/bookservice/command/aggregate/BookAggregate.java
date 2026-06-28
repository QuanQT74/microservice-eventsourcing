package com.ltfullstack.bookservice.command.aggregate;

import com.ltfullstack.bookservice.command.command.CreateCommandBook;
import com.ltfullstack.bookservice.command.command.DeleteCommandBook;
import com.ltfullstack.bookservice.command.command.UpdateCommandBook;
import com.ltfullstack.bookservice.command.event.BookCreatedEvent;
import com.ltfullstack.bookservice.command.event.BookDeleteEvent;
import com.ltfullstack.bookservice.command.event.BookUpdateEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import tools.jackson.databind.util.BeanUtil;

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
    public  BookAggregate(CreateCommandBook command){
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
}
