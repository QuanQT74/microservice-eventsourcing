package com.ltfullstack.commonservice.event;

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
    private Date borrowingDate;
}
