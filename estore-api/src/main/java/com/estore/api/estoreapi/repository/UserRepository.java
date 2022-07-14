package com.estore.api.estoreapi.repository;


import java.util.Optional;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.estore.api.estoreapi.models.User;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByUserName(String userName);
  Optional<User> findByPassword(String password);
  Optional<User> findByEmail(String email);
}
