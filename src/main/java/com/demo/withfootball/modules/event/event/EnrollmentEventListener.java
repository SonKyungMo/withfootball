package com.demo.withfootball.modules.event.event;

import com.demo.withfootball.infra.config.AppProperties;
import com.demo.withfootball.infra.mail.EmailMessage;
import com.demo.withfootball.infra.mail.EmailService;
import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.community.Community;
import com.demo.withfootball.modules.event.Enrollment;
import com.demo.withfootball.modules.event.Event;
import com.demo.withfootball.modules.notification.Notification;
import com.demo.withfootball.modules.notification.NotificationRepository;
import com.demo.withfootball.modules.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener {

    private final NotificationRepository notificationRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Community community = event.getCommunity();

        if (account.isCommunityEnrollmentResultByEmail()) {
            sendEmail(enrollmentEvent, account, event, community);
        }

        if (account.isCommunityEnrollmentResultByWeb()) {
            createNotification(enrollmentEvent, account, event, community);
        }
    }

    private void sendEmail(EnrollmentEvent enrollmentEvent, Account account, Event event, Community community) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/community/" + community.getEncodedPath() + "/events/" + event.getId());
        context.setVariable("linkName", community.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("위드풋볼, " + event.getTitle() + " 모임 참가 신청 결과입니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(EnrollmentEvent enrollmentEvent, Account account, Event event, Community community) {
        Notification notification = new Notification();
        notification.setTitle(community.getTitle() + " / " + event.getTitle());
        notification.setLink("/community/" + community.getEncodedPath() + "/events/" + event.getId());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }

}