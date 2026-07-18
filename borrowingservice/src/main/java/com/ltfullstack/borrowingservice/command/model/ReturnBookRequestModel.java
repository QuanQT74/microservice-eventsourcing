package com.ltfullstack.borrowingservice.command.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class ReturnBookRequestModel {
    private String id;
    private String bookId;
    private String employeeId;
    
    public String getBorrowingId() {
        log.info("ReturnBookRequestModel: id={}, bookId={}, employeeId={}", id, bookId, employeeId);
        return id;
    }
}
