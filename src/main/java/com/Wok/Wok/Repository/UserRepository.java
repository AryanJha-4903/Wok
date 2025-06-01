package com.Wok.Wok.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.Wok.Wok.Model.user;

public interface UserRepository extends MongoRepository<user,String> {
    Optional<user> findByUsername(String username);
    user findByEmail(String email);
    Optional findById(String id);
    user findByUsernameAndPassword(String username, String password);
    user findByEmailAndPassword(String email, String password);

}
