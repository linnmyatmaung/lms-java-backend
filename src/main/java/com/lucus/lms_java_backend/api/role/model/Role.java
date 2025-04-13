/*
 * @Author : Linn Myat Maung
 * @Date   : 4/10/2025
 * @Time   : 2:40 PM
 */

package com.lucus.lms_java_backend.api.role.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName name;
}
