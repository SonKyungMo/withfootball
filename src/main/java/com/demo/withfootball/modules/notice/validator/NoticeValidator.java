package com.demo.withfootball.modules.notice.validator;

import com.demo.withfootball.modules.event.form.EventForm;
import com.demo.withfootball.modules.notice.Form.NoticeForm;
import com.demo.withfootball.modules.notice.Notice;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class NoticeValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return NoticeForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NoticeForm noticeForm = (NoticeForm) target;


    }

}
