package com.example.bot.telegram.entity;

import com.example.bot.telegram.StateEnum;
import lombok.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@EntityScan
@Setter
@Getter
@Entity
@ToString
@EqualsAndHashCode
@Configuration
@Table(name = "tg_bot", schema = "public")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "email")
    private String email;
    @NotNull
    @Column(name = "status")
    private Boolean isActive;
    @NotNull
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private StateEnum stateEnum;
    @NotNull
    @OneToOne
    private TextEntity textEntity;

}
