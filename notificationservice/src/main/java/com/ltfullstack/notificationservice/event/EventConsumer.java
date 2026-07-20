package com.ltfullstack.notificationservice.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltfullstack.commonservice.model.BorrowingNotificationMessage;
import com.ltfullstack.commonservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EventConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, maxDelay = 60000, multiplier = 2),
            autoCreateTopics = "true",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            include = {RetriableException.class, RuntimeException.class},
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_DELAY_VALUE,
            dltTopicSuffix = "notification-service-dlt",
            retryTopicSuffix = "-retry"
    )
    @KafkaListener(topics = "test", containerFactory = "kafkaListenerContainerFactory")
    public void listenGroupFoo(String message) {
        log.info("Received Message in group foo: {}", message);
        throw new RuntimeException("Error test");
    }

    @DltHandler
    public void hendleDlt(@Payload String message) {
        log.info("Received DLT message: {}", message);
    }

    /** Đăng ký / test đơn giản — payload = địa chỉ email người nhận. */
    @KafkaListener(topics = "testEmail", containerFactory = "kafkaListenerContainerFactory")
    public void testMail(String message) {
        log.info("Received testEmail: {}", message);

        String body = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">
                <h1 style="color: #2563eb;">Xin chào!</h1>
                <p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>LibraStack</strong>.</p>
                <p><strong>Email:</strong> %s</p>
            </div>
            """.formatted(message);

        emailService.sendMail(message, "Welcome to LibraStack", body, true, null);
    }

    /**
     * Borrow / return confirmation từ BorrowingSaga.
     * Chỉ 1 listener trên topic này — tránh 2 consumer cùng group tranh partition.
     */
    @KafkaListener(topics = "testMailTemplate", containerFactory = "kafkaListenerContainerFactory")
    public void onBorrowingNotification(String payload) {
        log.info("Received testMailTemplate payload (len={})", payload != null ? payload.length() : 0);
        try {
            String trimmed = payload == null ? "" : payload.trim();

            // Payload cũ: chỉ là 1 email string (không phải JSON)
            if (!trimmed.startsWith("{")) {
                Map<String, Object> placehold = new HashMap<>();
                placehold.put("name", "LibraStack Reader");
                emailService.sendlEmailWithTempalte(trimmed, "LibraStack", "emailTemplate.ftl", placehold, null);
                return;
            }

            BorrowingNotificationMessage msg =
                    objectMapper.readValue(trimmed, BorrowingNotificationMessage.class);

            if (msg.getEmployeeEmail() == null || msg.getEmployeeEmail().isBlank()) {
                log.warn("Skip mail: missing email, borrowingId={}", msg.getBorrowingId());
                return;
            }

            String template;
            String subject;
            switch (msg.getType() != null ? msg.getType() : "") {
                case "BORROWED" -> {
                    template = "borrow-success.ftl";
                    subject = "LibraStack — Borrow confirmed";
                }
                case "RETURNED" -> {
                    template = "return-success.ftl";
                    subject = "LibraStack — Book returned";
                }
                default -> {
                    log.warn("Unknown type: {}", msg.getType());
                    return;
                }
            }

            Map<String, Object> model = new HashMap<>();
            model.put("name", msg.getEmployeeName() != null ? msg.getEmployeeName() : "Reader");
            model.put("bookName", msg.getBookName() != null ? msg.getBookName() : msg.getBookId());
            model.put("borrowingId", msg.getBorrowingId());
            model.put("date", msg.getOccurredAt());

            log.info("Sending {} mail to {}", msg.getType(), msg.getEmployeeEmail());
            emailService.sendlEmailWithTempalte(
                    msg.getEmployeeEmail(),
                    subject,
                    template,
                    model,
                    null
            );
        } catch (Exception e) {
            log.error("Failed to process borrowing notification: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
