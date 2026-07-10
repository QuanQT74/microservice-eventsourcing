package com.ltfullstack.borrowingservice.command.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BorrowingRequestModel {

    @NotBlank(message = "Book ID must not be blank")
    private String bookId;

    @NotBlank(message = "Employee ID must not be blank")
    private String employeeId;
}
