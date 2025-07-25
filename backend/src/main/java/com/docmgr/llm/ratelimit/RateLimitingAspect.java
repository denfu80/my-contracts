package com.docmgr.llm.ratelimit;

import com.docmgr.llm.exception.RateLimitExceededException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Aspect for enforcing rate limits on LLM provider operations using Redis
 */
@Aspect
@Component
public class RateLimitingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingAspect.class);
    private static final DateTimeFormatter MINUTE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public RateLimitingAspect(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Around("@annotation(rateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String provider = rateLimited.provider();
        int requestsPerMinute = rateLimited.requestsPerMinute();
        
        String currentMinute = getCurrentMinute();
        String key = "rate_limit:" + provider + ":" + currentMinute;
        
        try {
            String currentCountStr = redisTemplate.opsForValue().get(key);
            int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
            
            if (currentCount >= requestsPerMinute) {
                long secondsUntilNextMinute = getSecondsUntilNextMinute();
                logger.warn("Rate limit exceeded for provider: {} (current: {}, limit: {})", 
                           provider, currentCount, requestsPerMinute);
                
                if (rateLimited.throwOnExceeded()) {
                    throw new RateLimitExceededException(provider, requestsPerMinute, secondsUntilNextMinute);
                } else {
                    return null;
                }
            }
            
            // Increment counter and set expiration
            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, Duration.ofMinutes(1));
            
            logger.debug("Rate limit check passed for provider: {} ({}/{})", 
                        provider, currentCount + 1, requestsPerMinute);
            
            return joinPoint.proceed();
            
        } catch (Exception e) {
            if (e instanceof RateLimitExceededException) {
                throw e;
            }
            logger.error("Error checking rate limit for provider: " + provider, e);
            // If Redis is down, allow the request to proceed
            return joinPoint.proceed();
        }
    }
    
    private String getCurrentMinute() {
        return LocalDateTime.now().format(MINUTE_FORMATTER);
    }
    
    private long getSecondsUntilNextMinute() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMinute = now.plusMinutes(1).withSecond(0).withNano(0);
        return Duration.between(now, nextMinute).getSeconds();
    }
}