package com.hsbc.transaction.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hsbc.transaction.common.exception.IdempotencyException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * Aspect for handling idempotency in API requests.
 * Prevents duplicate processing of the same request using a cache-based approach.
 */
@Slf4j
@Aspect
@Component
public class IdempotentAspect {

    /**
     * Cache for storing idempotency keys.
     * Keys expire after 30 minutes and the cache has a maximum size of 1000 entries.
     */
    private final Cache<String, Object> idempotencyCache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    /**
     * Around advice for handling idempotency.
     * Checks if a request with the same idempotency key has been processed recently.
     *
     * @param joinPoint The proceeding join point
     * @param idempotent The idempotent annotation
     * @return The result of the method execution
     * @throws Throwable if an error occurs during execution
     */
    @Around("@annotation(idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        // 1. Get current request
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            throw new RuntimeException("Unable to get HTTP request");
        }

        // 2. Extract idempotency key
        String idempotencyKey = extractIdempotencyKey(request, idempotent);

        // 3. Validate idempotency key
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            throw new IllegalArgumentException("Idempotency key cannot be empty");
        }

        // 4. Check if request has been processed
        if (idempotencyCache.getIfPresent(idempotencyKey) != null) {
            throw new IdempotencyException("Duplicate request detected");
        }

        try {
            // 5. Execute business method
            Object result = joinPoint.proceed();

            // 6. Store result in cache
            idempotencyCache.put(idempotencyKey, result);

            return result;
        } catch (Exception e) {
            // 7. Remove idempotency key on failure (optional)
            idempotencyCache.invalidate(idempotencyKey);
            throw e;
        }
    }

    /**
     * Gets the current HTTP request from the request context.
     *
     * @return The current HTTP request
     */
    private HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    /**
     * Extracts the idempotency key from the request based on the specified location.
     *
     * @param request The HTTP request
     * @param idempotent The idempotent annotation
     * @return The extracted idempotency key
     */
    private String extractIdempotencyKey(HttpServletRequest request, Idempotent idempotent) {
        switch (idempotent.location()) {
            case HEADER:
                return request.getHeader(idempotent.key());
            case PARAMETER:
                return request.getParameter(idempotent.key());
            case ATTRIBUTE:
                Object attribute = request.getAttribute(idempotent.key());
                return attribute != null ? attribute.toString() : null;
            default:
                return null;
        }
    }
}
