/*
 * @Author : Linn Myat Maung
 * @Date   : 4/9/2025
 * @Time   : 6:19 PM
 */

package com.lucus.lms_java_backend.config.exception;


public class EntityCreationException extends RuntimeException {
    public EntityCreationException(String message) {
        super(message);
    }
}