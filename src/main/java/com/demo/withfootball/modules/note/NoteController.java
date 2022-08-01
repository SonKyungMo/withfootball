package com.demo.withfootball.modules.note;

import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.account.AccountRepository;
import com.demo.withfootball.modules.account.CurrentAccount;
import com.demo.withfootball.modules.note.form.NoteForm;
import com.demo.withfootball.modules.note.validator.NoteValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class NoteController {

    private final NoteValidator noteValidator;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final NoteService noteService;
    private final NoteRepository noteRepository;


    @InitBinder("noteForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(noteValidator);
    }

    @GetMapping("/send-note")
    public String newCommunityForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new NoteForm());
        return "/send-note";
    }

    @PostMapping("/send-note")
    public String sendNote(@CurrentAccount Account sender, @Valid NoteForm noteForm, Errors errors, Model model) {
        Account rec = accountRepository.findByNickname(noteForm.getReceiver());
        if (errors.hasErrors()) {
            model.addAttribute(sender);
            return "/send-note";
        }
        noteService.createNote(modelMapper.map(noteForm, Note.class), sender, rec);
        return "index-after-login";
    }

    @GetMapping("/note/{id}")
    public String getNote(@CurrentAccount Account account, @PathVariable Long id, Model model) {
        Note note = noteService.getNoteToReceiver(account, id);
        model.addAttribute(account);
        model.addAttribute(note);
        return "note/view";
    }

}

