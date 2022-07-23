package com.demo.withfootball.modules.note;

import com.demo.withfootball.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Note {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String contents;

    @ManyToOne
    private Account sender;

    @ManyToOne
    private Account receiver;

    private LocalDateTime createdDateTime;

    private boolean checked;


}
