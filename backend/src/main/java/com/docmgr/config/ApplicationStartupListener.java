package com.docmgr.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Logs important application information when the application is ready
 */
@Component
public class ApplicationStartupListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);
    
    @Value("${server.port:3000}")
    private int serverPort;
    
    @Value("${server.address:http://localhost}")
    private String serverAddress;
    
    private final Environment environment;
    
    public ApplicationStartupListener(Environment environment) {
        this.environment = environment;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        boolean isDevelopment = isProfileActive("development");
        boolean isProduction = isProfileActive("production");
        String url = serverAddress + ":" + serverPort;
        // Determine base URLs
        logger.info("🚀 Document Management Service is ready!");
        logger.info("📍 Application URLs:");
        
        // Always show localhost URLs
        logger.info("   - API Health: {}/api/v1/health", url);
        logger.info("   - LLM Health: {}/api/v1/llm/health", url);
        logger.info("   - Swagger UI: {}/swagger-ui/index.html", url);
        logger.info("   - OpenAPI JSON: {}/api/v1/api-docs", url);
        logger.info("   - Health Dashboard: {}/health-dashboard", url);
        
        // Show production URLs if in production
        if (isProduction) {
            logger.info("🌐 Production URLs:");
            logger.info("   - Swagger UI: {}/swagger-ui/index.html", url);
            logger.info("   - Health Dashboard: {}/health-dashboard", url);
        }
        
        if (isDevelopment) {
            logger.info("🔧 Development Tools:");
            logger.info("   - pgAdmin: {}:8080 (admin@example.com / admin123)", serverAddress);
            logger.info("   - Redis Commander: {}:8081", serverAddress);
            logger.info("   - Debug Port: 5005 (for IDE debugging)");
        }
        
        logger.info("🎯 Ready to process documents with LLM providers!");
    }
    
    private boolean isProfileActive(String profile) {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            if (profile.equals(activeProfile)) {
                return true;
            }
        }
        return false;
    }
}