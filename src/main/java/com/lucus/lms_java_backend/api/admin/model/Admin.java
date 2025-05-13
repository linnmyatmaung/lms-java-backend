/*
 * @Author : Linn Myat Maung
 * @Date   : 5/13/2025
 * @Time   : 4:49 PM
 */

package com.lucus.lms_java_backend.api.admin.model;


import jakarta.persistence.*;
import lombok.Data;
import com.lucus.lms_java_backend.api.user.model.User;

import java.time.LocalDateTime;

@Data
@Entity
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}