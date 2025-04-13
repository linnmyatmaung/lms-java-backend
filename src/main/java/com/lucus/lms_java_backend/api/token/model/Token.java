/*
 * @Author : Linn Myat Maung
 * @Date   : 4/13/2025
 * @Time   : 6:23 PM
 */

package com.lucus.lms_java_backend.api.token.model;

import com.lucus.lms_java_backend.api.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String refreshtoken;

    @Column(nullable = false)
    private Instant expiredAt;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
