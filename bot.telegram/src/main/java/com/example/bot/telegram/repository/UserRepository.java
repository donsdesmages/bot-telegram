package com.example.bot.telegram.repository;

import com.example.bot.telegram.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findTextByEmail(String email);

  /*  @Modifying
    @Query("select from  usEn where usEn.email = ?1")
    void findUserByEmail();*/

}
