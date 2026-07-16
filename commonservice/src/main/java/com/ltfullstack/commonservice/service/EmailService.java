package com.ltfullstack.commonservice.service;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.Map;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private   JavaMailSender javaMailSender;
    @Autowired
    private MailSender mailSender;


    /**
     * Gửi email kèm theo tệp đính kèm.
     *
     * <p>Phương thức này hỗ trợ gửi cả email dạng văn bản thuần túy (plain text)
     * và định dạng HTML, đồng thời cho phép đính kèm một tệp tin duy nhất.</p>
     *
     * @param to          Địa chỉ email của người nhận (ví dụ: "example@mail.com").
     * @param subject     Tiêu đề của email.
     * @param text        Nội dung email (văn bản thuần túy hoặc mã HTML).
     * @param isHtml      Đặt là {@code true} nếu nội dung thuộc định dạng HTML;
     *                    đặt là {@code false} nếu là văn bản thuần túy.
     * @param attachment  Đối tượng {@link File} đại diện cho tệp muốn đính kèm.
     *                    Truyền {@code null} nếu không có tệp đính kèm.
     * @throws IllegalArgumentException Nếu địa chỉ email người nhận trống hoặc sai định dạng.
     * @throws MailSendException        Nếu xảy ra lỗi trong quá trình kết nối hoặc gửi mail.
     */
    public void sendMail(String to, String subject, String text, boolean isHtml, File attachment) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper =  new MimeMessageHelper(mimeMessage,true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text,isHtml);

            ////Nếu có đường dẫn file đính kèm → thêm file vào email.
            if(attachment != null){
                FileSystemResource fileSystemResource = new FileSystemResource(attachment);
                mimeMessageHelper.addAttachment(fileSystemResource.getFilename(),fileSystemResource);
            }
            javaMailSender.send(mimeMessage);
            log.info("Email sent successfully to {}", to);
        }catch (MessagingException ex){
            log.error("Failed to send email to {}",to,ex);
        }
    }

    @Autowired
    private FreeMarkerConfig  freeMarkerConfig;

    /**
     * Gửi email sử dụng mẫu (template) HTML.
     *
     * <p>Phương thức này sẽ tải template theo tên được cung cấp, thay thế
     * các biến trong template bằng dữ liệu từ {@code model} và gửi email
     * đến người nhận. Có thể đính kèm tệp nếu cần.</p>
     *
     * @param to địa chỉ email của người nhận.
     * @param subject tiêu đề của email.
     * @param templatename tên của file template email (không bao gồm phần mở rộng nếu được cấu hình sẵn).
     * @param model tập dữ liệu dùng để thay thế các biến trong template.
     * @param attachment tệp đính kèm, có thể truyền {@code null} nếu không có tệp đính kèm.
     *
     * @throws jakarta.mail.MessagingException nếu xảy ra lỗi trong quá trình tạo hoặc gửi email.
     * @throws java.io.IOException nếu xảy ra lỗi khi đọc template hoặc tệp đính kèm.
     */

    public void sendlEmailWithTempalte(String to , String subject , String templatename , Map<String,Object> model , File attachment){
        try{
            Template template = freeMarkerConfig.getConfiguration().getTemplate(templatename);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template,model);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html,true);

            //Nếu có đường dẫn file đính kèm → thêm file vào email.
            if(attachment != null){
                FileSystemResource fileSystemResource = new FileSystemResource(attachment);
                helper.addAttachment(fileSystemResource.getFilename(),fileSystemResource);
            }
            javaMailSender.send(mimeMessage);
            log.info("Email sent successfully to {}", to);

        }catch (MessagingException  |IOException | TemplateException ex){
            log.error("Failed to send email to {}",to,ex);
        }
    }

}
