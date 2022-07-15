package com.demo.withfootball.modules.account.form;

import com.demo.withfootball.modules.account.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Notifications {

    private boolean groupCreatedByEmail;

    private boolean groupCreatedByWeb;

    private boolean groupEnrollmentResultByEmail;

    private boolean groupEnrollmentResultByWeb;

    private boolean groupUpdatedByEmail;

    private boolean groupUpdatedByWeb;

}
