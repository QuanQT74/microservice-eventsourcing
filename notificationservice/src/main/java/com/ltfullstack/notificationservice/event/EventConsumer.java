package com.ltfullstack.notificationservice.event;

import com.ltfullstack.commonservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.RetriableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public EventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RetryableTopic(
            attempts = "4",//3 topic + 1 topic DLQ
            backoff =@Backoff(
                    delay = 1000,//1 giây
                    maxDelay = 60000,///// Tối đa 60 giây
                    multiplier = 2 //// Tăng dần: 1s → 2s → 4s → 8s
            ),
            autoCreateTopics = "true",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            include = {RetriableException.class, RuntimeException.class},
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_DELAY_VALUE,
            dltTopicSuffix = "notification-service-dlt",
            retryTopicSuffix = "-retry"
    )

    @KafkaListener(topics = "test", containerFactory = "kafkaListenerContainerFactory")
    public void listenGroupFoo(String message) {
        log.info("Received Message in group foo: " + message);
        throw new RuntimeException("Error test");
    }

    @DltHandler
    public void hendleDlt(@Payload String message ){
        log.info("Received message : " +message);
    }
    @KafkaListener(topics = "testEmail", containerFactory = "kafkaListenerContainerFactory")
    public void testMail(String message) {
        log.info("Received Message: {}", message);

        String template = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">
                <h1 style="color: #2563eb;">👋 Xin chào, %s!</h1>

                <p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>ABC Company</strong>.</p>

                <p>Chúng tôi rất vui được đồng hành cùng bạn.</p>

                <p><strong>Email:</strong> %s</p>

                <p>Chúc bạn có những trải nghiệm tuyệt vời!</p>

                <hr>

                <p style="font-size:12px;color:#777;">
                    © 2026 ABC Company
                </p>
            </div>
            """;

        String body = String.format(
                template,
                "Đông Quân",
                message
        );

        emailService.sendMail(
                message,
                "Thank You",
                body,
                true,
                null
        );
    }
    @KafkaListener(topics = "testMailTemplate", containerFactory = "kafkaListenerContainerFactory")
    public void testSendMail(String message) {
        log.info("Received Message in group foo: " + message);

        Map<String,Object> placehold = new HashMap<>();
        placehold.put("name","LT FULLSATCK");
        emailService.sendlEmailWithTempalte(message,"","emailTemplate.ftl",placehold,null);

    }
}
