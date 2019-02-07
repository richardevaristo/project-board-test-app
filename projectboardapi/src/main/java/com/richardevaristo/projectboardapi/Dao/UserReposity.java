package com.richardevaristo.projectboardapi.Dao;

import com.richardevaristo.projectboardapi.Model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReposity extends CrudRepository<User, Integer> {
    User findByUsernameAndPassword(String username, String password);
}
