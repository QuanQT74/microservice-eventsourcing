package com.ltfullstack.employeeservice.command.aggregate;

import com.ltfullstack.employeeservice.command.command.CreateEmployeeCommand;
import com.ltfullstack.employeeservice.command.command.DeleteEmployeeCommand;
import com.ltfullstack.employeeservice.command.command.UpdateEmployeeCommand;
import com.ltfullstack.employeeservice.command.event.EmployeeCreatedEvent;
import com.ltfullstack.employeeservice.command.event.EmployeeDeleteEvent;
import com.ltfullstack.employeeservice.command.event.EmployeeUpdatedEvent;
import com.ltfullstack.employeeservice.command.model.CreateEmployeeModel;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.ComponentScan;

@Aggregate
@NoArgsConstructor
public class EmployeeAggregate {

    @AggregateIdentifier
    private String id;

    private String firstName;

    private String lastName;

    private String Kin;

    private Boolean isDisciplined;

    //Add
    @CommandHandler
    public EmployeeAggregate(CreateEmployeeCommand createEmployeeCommand){
        EmployeeCreatedEvent employeeCreatedEvent = new EmployeeCreatedEvent();
        BeanUtils.copyProperties(createEmployeeCommand,employeeCreatedEvent);

        AggregateLifecycle.apply(employeeCreatedEvent);
    }

    @EventSourcingHandler
    public void on(EmployeeCreatedEvent event){
        this.id = event.getId();
        this.firstName = event.getFirstName();
        this.isDisciplined = event.getIsDisciplined();
        this.lastName = event.getLastName();
        this.Kin = event.getKin();
    }

    //Update
    @CommandHandler
    public void handle(UpdateEmployeeCommand updateEmployeeCommand){
        EmployeeUpdatedEvent employeeUpdatedEvent = new EmployeeUpdatedEvent();

        BeanUtils.copyProperties(updateEmployeeCommand,employeeUpdatedEvent);

        AggregateLifecycle.apply(employeeUpdatedEvent);
    }
    @EventSourcingHandler
    public void on(EmployeeUpdatedEvent employeeUpdatedEvent){
        this.id = employeeUpdatedEvent.getId();
        this.firstName = employeeUpdatedEvent.getFirstName();
        this.lastName = employeeUpdatedEvent.getLastName();
        this.Kin = employeeUpdatedEvent.getKin();
        this.isDisciplined = employeeUpdatedEvent.getIsDisciplined();
    }

    //Delete
    @CommandHandler
    public void handle(DeleteEmployeeCommand deleteEmployeeCommand){
        EmployeeDeleteEvent event = new EmployeeDeleteEvent();
        BeanUtils.copyProperties(deleteEmployeeCommand,event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(EmployeeDeleteEvent employeeDeleteEvent){
        this.id = employeeDeleteEvent.getId();
    }
}
