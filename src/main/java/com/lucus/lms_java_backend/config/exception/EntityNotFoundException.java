/*
 * @Author : Linn Myat Maung
 * @Date   : 4/9/2025
 * @Time   : 6:20 PM
 */

package com.lucus.lms_java_backend.config.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}