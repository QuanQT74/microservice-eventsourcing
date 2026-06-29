package com.ltfullstack.bookservice.command.command;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeleteCommandBook {
    @TargetAggregateIdentifier
    String id;
}
