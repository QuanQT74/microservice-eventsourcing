package com.ltfullstack.commonservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message gửi qua Kafka topic {@code borrowing-notifications}.
 * BorrowingSaga (producer) ↔ NotificationService (consumer).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowingNotificationMessage {
    /** BORROWED | RETURNED | FAILED */
    private String type;
    private String borrowingId;
    private String bookId;
    private String bookName;
    private String employeeId;
    private String employeeEmail;
    private String employeeName;
    private String occurredAt;
}
