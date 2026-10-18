package com.brainics.backend.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    @Value("${app.enquiry.rate-limit-per-hour:5}")
    private int rateLimit;

    private static class RateLimitEntry {
        int count;
        long resetTime;

        RateLimitEntry(int count, long resetTime) {
            this.count = count;
            this.resetTime = resetTime;
        }
    }

    private final ConcurrentHashMap<String, RateLimitEntry> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        String endpoint = request.getRequestURI();

        if (!isRateLimitedEndpoint(endpoint)) {
            return true;
        }

        String clientIp = getClientIpAddress(request);
        String key = clientIp + ":" + endpoint;

        long now = System.currentTimeMillis();
        RateLimitEntry entry = rateLimitMap.get(key);

        if (entry == null || now > entry.resetTime) {
            rateLimitMap.put(key, new RateLimitEntry(1, now + 3600000));
            return true;
        }

        entry.count++;

        if (entry.count > rateLimit) {
            log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, endpoint);
            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            response.setHeader("Retry-After", "3600");
            response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
            return false;
        }

        return true;
    }

    private boolean isRateLimitedEndpoint(String uri) {
        return uri.contains("/api/public/enquiry/submit");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
