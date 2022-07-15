package com.demo.withfootball.modules.notice;

import com.demo.withfootball.modules.community.Community;
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
public class Notice {

    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String contents;

    private LocalDateTime createdDateTime;

    @ManyToOne
    private Community community;


}
