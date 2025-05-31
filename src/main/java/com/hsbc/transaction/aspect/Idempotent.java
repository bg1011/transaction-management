package com.hsbc.transaction.aspect;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Annotation for implementing idempotency in API endpoints.
 * This annotation helps prevent duplicate processing of the same request by
 * using an idempotency key to track and cache request results.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 * @Idempotent(location = Location.HEADER, key = "X-Idempotency-Key")
 * public ResponseEntity<TransactionVO> createTransaction(@RequestBody CreateTransactionDTO dto) {
 *     // Method implementation
 * }
 * }
 * </pre>
 * </p>
 *
 * @author HSBC Development Team
 * @version 1.0
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * Specifies the location where the idempotency key can be found.
     * The default is the request header.
     *
     * @return the location of the idempotency key
     */
    Location location() default Location.HEADER;

    /**
     * Specifies the name of the idempotency key.
     * The default is "Idempotency-Key".
     *
     * @return the name of the idempotency key
     */
    String key() default "Idempotency-Key";

    /**
     * Specifies the expiration time for the idempotency key.
     * The default is 30 minutes.
     *
     * @return the expiration time
     */
    long expireTime() default 30;

    /**
     * Specifies the time unit for the expiration time.
     * The default is minutes.
     *
     * @return the time unit for expiration
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    /**
     * Enum defining the possible locations where an idempotency key can be found.
     */
    enum Location {
        /**
         * The idempotency key is expected in the request header.
         */
        HEADER,

        /**
         * The idempotency key is expected in the request parameters.
         */
        PARAMETER,

        /**
         * The idempotency key is expected in the request attributes.
         */
        ATTRIBUTE
    }
}