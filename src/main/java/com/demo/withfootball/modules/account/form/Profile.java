package com.demo.withfootball.modules.account.form;

import com.demo.withfootball.modules.account.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
public class Profile {

    @Length(max = 50)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    @Length(max = 50)
    private String location;

    private String ProfileImage;


}
