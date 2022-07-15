package com.demo.withfootball.modules.event;

import com.demo.withfootball.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(
        name = "Enrollment.withEventAndCommunity",
        attributeNodes = {
                @NamedAttributeNode(value = "event", subgraph = "community")
        },
        subgraphs = @NamedSubgraph(name = "community", attributeNodes = @NamedAttributeNode("community"))
)

@Entity
@Getter
@Setter
@EqualsAndHashCode(of="id")
public class Enrollment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;
}
