package com.ltfullstack.bookservice.command.command;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCommandBook {
    @TargetAggregateIdentifier
    String id;

    String name;

    String author;

    Boolean isReady;

}
