package com.demo.withfootball.modules.community.validator;

import com.demo.withfootball.modules.community.form.CommunityForm;
import com.demo.withfootball.modules.community.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CommunityFormValidator implements Validator {

    private final CommunityRepository communityRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return CommunityForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CommunityForm communityForm = (CommunityForm) target;
        if(communityRepository.existsByPath(communityForm.getPath())){
            errors.rejectValue("path","wrong.path", "해당 그룹 경로를 사용할 수 없습니다.");
        }

    }
}
