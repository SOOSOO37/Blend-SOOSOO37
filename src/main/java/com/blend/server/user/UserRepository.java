package com.blend.server.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickName(String nickname);

    Page<User> findAllByUserStatus(User.UserStatus userStatus, Pageable pageable);
}
