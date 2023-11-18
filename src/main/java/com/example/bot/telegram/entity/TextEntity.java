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
@Table(name = "text_msg", schema = "public")
public class TextEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "text")
    private String text;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @NotNull
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private StateEnum stateEnum;
}
