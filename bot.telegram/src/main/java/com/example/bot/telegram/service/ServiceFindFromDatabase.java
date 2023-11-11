package com.example.bot.telegram.service;

import com.example.bot.telegram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class ServiceFindFromDatabase {
    private final UserRepository userRepository;
    protected final JdbcTemplate jdbcTemplate;

   /* protected Optional<UserEntity> findEmailUser(Long id) {
        UserEntity userEntity = new UserEntity();
        DataAccessUtils.singleResult(jdbcTemplate.query("select from  usEn where usEn.email = ?1", id))

*//*
        Optional<UserEntity> email = userRepository.findEmailById(userEntity.getId());*//*
        return email;
    }*/
}
