package com.demo.withfootball.modules.community.event;

import com.demo.withfootball.infra.config.AppProperties;
import com.demo.withfootball.infra.mail.EmailMessage;
import com.demo.withfootball.infra.mail.EmailService;
import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.account.AccountPredicates;
import com.demo.withfootball.modules.account.AccountRepository;
import com.demo.withfootball.modules.account.AccountRepository;
import com.demo.withfootball.modules.community.Community;
import com.demo.withfootball.modules.community.CommunityRepository;
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
import java.util.HashSet;
import java.util.Set;

@Async
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class CommunityEventListener {

    private final CommunityRepository communityRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleCommunityCreatedEvent(CommunityCreatedEvent communityCreatedEvent) {
        Community community = communityRepository.findCommunityWithTagsAndZonesById(communityCreatedEvent.getCommunity().getId());
        //QueryDSl
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(community.getTags(), community.getZones()));
        accounts.forEach(account -> {
            if (account.isCommunityCreatedByEmail()) {

                sendCommunityCreatedEmail(community, account, "새로운 커뮤니티가 생겼습니다. ",
                        "withfootball '" + community.getTitle() + "' 커뮤니티가 생겼습니다.");
            }

            if (account.isCommunityCreatedByWeb()) {
                createNotification(community, account, community.getShortDescription(), NotificationType.COMMUNITY_CREATED);
            }
        });
    }

    @EventListener
    public void handleCommunityUpdateEvent(CommunityUpdateEvent communityUpdateEvent) {
        Community community = communityRepository.findCommunityWithManagersAndMemebersById(communityUpdateEvent.getCommunity().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(community.getManagers());
        accounts.addAll(community.getMembers());

        accounts.forEach(account -> {
            if (account.isCommunityUpdatedByEmail()) {
                sendCommunityCreatedEmail(community, account, communityUpdateEvent.getMessage(),
                        "withfootball '" + community.getTitle() + "' 커뮤니티에 새로운 소식이 생겼습니다.");
            }

            if (account.isCommunityUpdatedByWeb()) {
                createNotification(community, account, communityUpdateEvent.getMessage(), NotificationType.COMMUNITY_UPDATED);
            }
        });
    }

    @EventListener
    public void handleCommunityNoticeUpdateEvent(CommunityNoticeUpdateEvent communityNoticeUpdateEvent) {
        Community community = communityRepository.findCommunityWithManagersAndMemebersById(communityNoticeUpdateEvent.getCommunity().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(community.getManagers());
        accounts.addAll(community.getMembers());

        accounts.forEach(account -> {
            if (account.isCommunityUpdatedByEmail()) {
                sendCommunityCreatedEmail(community, account, communityNoticeUpdateEvent.getMessage(),
                        "withfootball '" + community.getTitle() + "' 커뮤니티에 공지가 생성되었습니다.");
            }

            if (account.isCommunityUpdatedByWeb()) {
                createNotification(community, account, communityNoticeUpdateEvent.getMessage(), NotificationType.NOTICE_UPDATE);
            }
        });
    }


    private void createNotification(Community community, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(community.getTitle());
        notification.setLink("/community/" + community.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    private void sendCommunityCreatedEmail(Community community, Account account, String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/community/" + community.getEncodedPath());
        context.setVariable("linkName", community.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }


}
