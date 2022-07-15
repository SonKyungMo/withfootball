package com.demo.withfootball.modules.account;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

//JdbcTokenRepositoryImpl
// public static final String CREATE_TABLE_SQL = "create table persistent_logins 
// (username varchar(64) not null, series varchar(64) primary key, token varchar(64) not null, last_used timestamp not null)";
// 생성
@Table(name="persistent_logins")
@Entity
@Getter
@Setter
public class PersistentLogins {

    @Id
    @Column(length = 64)
    private String series;

    @Column(nullable= false, length = 64)
    private String username;

    @Column(nullable = false, length = 64)
    private String token;

    @Column(name="last_used", nullable = false, length = 64)
    private LocalDateTime lastUsed;
}
