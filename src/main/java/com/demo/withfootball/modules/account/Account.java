package com.demo.withfootball.modules.account;

import com.demo.withfootball.modules.tag.Tag;
import com.demo.withfootball.modules.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location; //varchar

    @Lob //textType Mapping(255이상의 데이터)
    @Basic(fetch = FetchType.EAGER) //user loading 같이 로딩
    private String profileImage;

    private boolean communityCreatedByEmail = false;

    private boolean communityCreatedByWeb = true;

    private boolean communityEnrollmentResultByEmail= false;

    private boolean communityEnrollmentResultByWeb = true;

    private boolean communityUpdatedByEmail= false;

    private boolean communityUpdatedByWeb = true;

    private LocalDateTime emailCheckTokenGeneratedAt;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();


    public void generateEmailCheckToken() {

        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignup() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now());
    }
}
