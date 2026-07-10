package com.ltfullstack.borrowingservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingCreatedEvent {
    private String id;

    private String bookId;

    private String employeeId;

    private Date borrwingDate;
}
