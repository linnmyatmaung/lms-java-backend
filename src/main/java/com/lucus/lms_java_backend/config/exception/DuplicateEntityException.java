/*
 * @Author : Linn Myat Maung
 * @Date   : 4/9/2025
 * @Time   : 6:17 PM
 */

package com.lucus.lms_java_backend.config.exception;


public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}