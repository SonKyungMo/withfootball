package com.demo.withfootball.modules.note.event;

import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.account.AccountRepository;
import com.demo.withfootball.modules.community.Community;
import com.demo.withfootball.modules.event.Enrollment;
import com.demo.withfootball.modules.event.Event;
import com.demo.withfootball.modules.note.Note;
import com.demo.withfootball.modules.note.NoteRepository;
import com.demo.withfootball.modules.notification.Notification;
import com.demo.withfootball.modules.notification.NotificationRepository;
import com.demo.withfootball.modules.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class NoteEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleSendNote(SendNoteEvent sendNoteEvent) {
        Note note = sendNoteEvent.getNote();
        Account receiver = note.getReceiver();
        Account sender = note.getSender();

        Notification notification = new Notification();
        notification.setTitle(sender.getNickname());
        notification.setLink("/note/" + note.getId());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(note.getTitle());
        notification.setAccount(receiver);
        notification.setNotificationType(NotificationType.NOTE);
        notificationRepository.save(notification);

    }
}