package com.docmgr.api.llm;

import com.docmgr.llm.model.ProviderHealth;
import com.docmgr.llm.model.ProviderType;

/**
 * Response DTO for provider information
 */
public class ProviderInfo {
    
    private String name;
    private ProviderType type;
    private boolean available;
    private ProviderHealth health;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ProviderType getType() {
        return type;
    }
    
    public void setType(ProviderType type) {
        this.type = type;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public ProviderHealth getHealth() {
        return health;
    }
    
    public void setHealth(ProviderHealth health) {
        this.health = health;
    }
    
    public static class Builder {
        private final ProviderInfo info = new ProviderInfo();
        
        public Builder name(String name) {
            info.setName(name);
            return this;
        }
        
        public Builder type(ProviderType type) {
            info.setType(type);
            return this;
        }
        
        public Builder available(boolean available) {
            info.setAvailable(available);
            return this;
        }
        
        public Builder health(ProviderHealth health) {
            info.setHealth(health);
            return this;
        }
        
        public ProviderInfo build() {
            return info;
        }
    }
}