package com.demo.withfootball.modules.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;



@Transactional(readOnly = true)
public interface NoteRepository extends JpaRepository<Note, Long> {
    Note findNoteById(Long id);
}
