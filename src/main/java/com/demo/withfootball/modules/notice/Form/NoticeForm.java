package com.demo.withfootball.modules.notice.Form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class NoticeForm {

    @NotBlank
    @Length(max=50)
    private String title;

    private String contents;
}
