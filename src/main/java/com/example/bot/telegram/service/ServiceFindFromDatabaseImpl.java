package com.example.bot.telegram.service;

import com.example.bot.telegram.service.impl.ServiceFindFromDatabase;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class ServiceFindFromDatabaseImpl implements ServiceFindFromDatabase {
    @Override
    public List<String> findEmail() {
        EntityManager entityManager = Persistence
            .createEntityManagerFactory("test").createEntityManager();

        Query typedQuery = entityManager.createQuery("SELECT u.email FROM UserEntity u WHERE u.stateEnum = 'WAIT_IN_PROCESS'", String.class);

        List<String> emails = typedQuery.getResultList();

        return emails;
    }

    @Override
    public List<String> findText() {

        EntityManager entityManager = Persistence
            .createEntityManagerFactory("test").createEntityManager();

        Query query = entityManager.createQuery("SELECT text FROM TextEntity");

        List<String> text = query.getResultList();

        return text;
    }


}
