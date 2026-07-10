package com.ltfullstack.borrowingservice.command.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrowing")
public class Borrowing {
    @Id
    private String id;

    private String bookId;

    private String employeeId;

    private Date borrwingDate;

    private Date returnData;
}
