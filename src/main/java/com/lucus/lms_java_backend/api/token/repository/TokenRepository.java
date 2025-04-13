/*
 * @Author : Linn Myat Maung
 * @Date   : 4/13/2025
 * @Time   : 6:26 PM
 */

package com.lucus.lms_java_backend.api.token.repository;


import com.lucus.lms_java_backend.api.token.model.Token;
import com.lucus.lms_java_backend.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUser(User user);
}
