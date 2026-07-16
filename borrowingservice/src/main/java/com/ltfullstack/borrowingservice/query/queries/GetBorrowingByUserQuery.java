package com.ltfullstack.borrowingservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetBorrowingByUserQuery {
    private String userId;
}
