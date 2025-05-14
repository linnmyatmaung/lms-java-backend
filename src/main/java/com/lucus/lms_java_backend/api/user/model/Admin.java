/*
 * @Author : Linn Myat Maung
 * @Date   : 4/13/2025
 * @Time   : 5:40 PM
 */

package com.lucus.lms_java_backend.api.user.model;

import com.lucus.lms_java_backend.api.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends User {

    private String department;


}