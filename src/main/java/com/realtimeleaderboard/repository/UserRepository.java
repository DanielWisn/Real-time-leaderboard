package com.realtimeleaderboard.repository;

import com.realtimeleaderboard.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    User save(User user);

    @Override
    List<User> findAll();
}
