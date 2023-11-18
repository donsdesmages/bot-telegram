package com.example.bot.telegram.model;

import com.example.bot.telegram.StateEnum;
import com.example.bot.telegram.entity.TextEntity;
import lombok.*;

@Data
@Setter
@Getter
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class UserModel {
    private Long id;
    private String text;
    private String email;
    private Boolean isActive;
    private StateEnum stateEnum;
}
