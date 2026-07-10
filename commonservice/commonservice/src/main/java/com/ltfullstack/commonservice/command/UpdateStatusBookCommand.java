package com.ltfullstack.commonservice.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusBookCommand {

    @TargetAggregateIdentifier

    private String bookId;

    private Boolean isReady;

    private String employeeId;

    private  String borrwingId;

}
