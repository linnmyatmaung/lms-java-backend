/*
 * @Author : Linn Myat Maung
 * @Date   : 4/10/2025
 * @Time   : 1:14 PM
 */

package com.lucus.lms_java_backend.config.response.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedResponse<T> {
    private List<T> items;
    private long totalItems;
    private int lastPage;
}
