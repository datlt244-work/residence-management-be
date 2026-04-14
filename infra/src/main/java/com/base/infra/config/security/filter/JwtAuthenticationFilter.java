package com.base.infra.config.security.filter;

import com.base.domain.employee.domain.Employee;
import com.base.domain.employee.domain.valueobjects.EmployeeId;
import com.base.domain.employee.repository.EmployeeRepository;
import com.base.infra.config.security.service.JwtService;
import com.base.infra.employee.entity.EmployeeEntity;
import com.base.infra.employee.repository.JpaEmployeeRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;
    private final JpaEmployeeRepository employeeJpaRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/") ||
                path.equals("/error") ||
                path.equals("/favicon.ico") ||
                path.startsWith("/api/auth/") ||
                path.startsWith("/api/test/") ||
                path.startsWith("/h2-console/") ||
                path.startsWith("/swagger-ui") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/configuration/") ||
                path.equals("/swagger-config") ||
                path.startsWith("/api-docs") ||
                path.equals("/actuator/health") ||
                path.startsWith("/actuator/health/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String employeeIdStr = jwtService.extractEmployeeId(jwt);

        if (employeeIdStr != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final int employeePk;
            try {
                employeePk = Integer.parseInt(employeeIdStr);
            } catch (NumberFormatException ex) {
                filterChain.doFilter(request, response);
                return;
            }

            EmployeeEntity employeeEntity = employeeJpaRepository
                    .findById(employeePk)
                    .orElse(null);

            if (employeeEntity != null) {
                Employee domainEmployee = employeeRepository.findById(EmployeeId.of(employeeIdStr)).orElse(null);

                if (domainEmployee != null && jwtService.validateToken(jwt, domainEmployee)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(employeeEntity, null,
                                    employeeEntity.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
