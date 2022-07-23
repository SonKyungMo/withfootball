package com.demo.withfootball.modules.note.form;

import com.demo.withfootball.modules.account.Account;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class NoteForm {

    @NotBlank
    @Length(max = 50)
    private String title;

    private String receiver;

    private String contents;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdDateTime;

}
