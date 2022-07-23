package com.demo.withfootball.modules.note.event;

import com.demo.withfootball.modules.note.Note;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SendNoteEvent {
    protected final Note note;

}
