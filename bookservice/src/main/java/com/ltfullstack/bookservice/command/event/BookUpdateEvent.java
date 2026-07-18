package com.ltfullstack.bookservice.command.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookUpdateEvent {
    String id;

    String name;

    String author;

    Boolean isReady;

    String imageUrl;
}
