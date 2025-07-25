package com.docmgr.llm.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for applying rate limiting to LLM provider methods
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimited {
    
    /**
     * The provider identifier for rate limiting
     */
    String provider();
    
    /**
     * Maximum requests allowed per minute
     */
    int requestsPerMinute();
    
    /**
     * Whether to throw exception on rate limit exceeded (default) or return null
     */
    boolean throwOnExceeded() default true;
}