package com.ltfullstack.borrowingservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingReturnedEvent {
    ///
    /// TẠO MỚI - Event khi trả sách

    private String id;

    private String bookId;

    private Date returnedDate;

}
