package com.demo.withfootball.modules.note;

import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.note.event.SendNoteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor

public class NoteService {

    private final NoteRepository noteRepository;
    private final ApplicationEventPublisher eventPublisher;


    public Note createNote(Note note, Account sender, Account rec) {
        note.setSender(sender);
        note.setReceiver(rec);
        note.setCreatedDateTime(LocalDateTime.now());
        eventPublisher.publishEvent(new SendNoteEvent(note));
        return noteRepository.save(note);
    }

}
