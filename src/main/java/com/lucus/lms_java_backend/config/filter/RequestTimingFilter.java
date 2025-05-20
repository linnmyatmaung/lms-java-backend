/*
 * @Author : Linn Myat Maung
 * @Date   : 5/20/2025
 * @Time   : 2:59 PM
 */

package com.lucus.lms_java_backend.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestTimingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        request.setAttribute("startTime", System.currentTimeMillis());
        filterChain.doFilter(request, response);
    }
}