package com.hsbc.transaction.common.util;

import com.hsbc.transaction.common.exception.BusinessException;
import com.hsbc.transaction.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdempotencyUtil {

    private final CacheManager cacheManager;

    public void checkIdempotency(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            throw new BusinessException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
        }

        Cache cache = cacheManager.getCache("idempotencyKeys");
        if (cache != null && cache.get(idempotencyKey) != null) {
            throw new BusinessException(ErrorCode.REPEATED_REQUEST);
        }

        // 标记该key已使用
        cache.put(idempotencyKey, "PROCESSED");
    }
}
