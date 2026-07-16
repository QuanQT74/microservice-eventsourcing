package com.ltfullstack.borrowingservice.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class BorrowingResponseModel {
    private String id;

    private String bookId;

    private String employeeId;

    private Date borrwingDate;

    private Date returnData;

    private String status;
}
