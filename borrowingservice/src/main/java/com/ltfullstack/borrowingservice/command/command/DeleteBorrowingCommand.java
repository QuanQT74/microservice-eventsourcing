package com.ltfullstack.borrowingservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBorrowingCommand {
    @TargetAggregateIdentifier
    private String id;
}
