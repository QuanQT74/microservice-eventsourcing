package com.ltfullstack.bookservice.command.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookCreatedEvent {
    String id;

    String name;

    String author;

    Boolean isReady;

    String imageUrl;
}
