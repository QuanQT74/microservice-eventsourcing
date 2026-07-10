package com.ltfullstack.commonservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookUpadateStatusEvent {

    private String bookId;

    private Boolean isReady;

    private String employeeId;

    private  String borrwingId;

}
