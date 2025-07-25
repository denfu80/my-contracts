package com.docmgr.llm.providers.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for Ollama API calls
 */
public class OllamaRequest {
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("prompt")
    private String prompt;
    
    @JsonProperty("stream")
    private Boolean stream = false;
    
    @JsonProperty("options")
    private Options options;
    
    @JsonProperty("system")
    private String system;
    
    @JsonProperty("template")
    private String template;
    
    @JsonProperty("context")
    private int[] context;
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public Boolean getStream() {
        return stream;
    }
    
    public void setStream(Boolean stream) {
        this.stream = stream;
    }
    
    public Options getOptions() {
        return options;
    }
    
    public void setOptions(Options options) {
        this.options = options;
    }
    
    public String getSystem() {
        return system;
    }
    
    public void setSystem(String system) {
        this.system = system;
    }
    
    public String getTemplate() {
        return template;
    }
    
    public void setTemplate(String template) {
        this.template = template;
    }
    
    public int[] getContext() {
        return context;
    }
    
    public void setContext(int[] context) {
        this.context = context;
    }
    
    public static class Options {
        @JsonProperty("temperature")
        private Double temperature;
        
        @JsonProperty("top_p")
        private Double topP;
        
        @JsonProperty("top_k")
        private Integer topK;
        
        @JsonProperty("num_predict")
        private Integer numPredict;
        
        @JsonProperty("stop")
        private String[] stop;
        
        @JsonProperty("repeat_penalty")
        private Double repeatPenalty;
        
        @JsonProperty("seed")
        private Integer seed;
        
        public Double getTemperature() {
            return temperature;
        }
        
        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
        
        public Double getTopP() {
            return topP;
        }
        
        public void setTopP(Double topP) {
            this.topP = topP;
        }
        
        public Integer getTopK() {
            return topK;
        }
        
        public void setTopK(Integer topK) {
            this.topK = topK;
        }
        
        public Integer getNumPredict() {
            return numPredict;
        }
        
        public void setNumPredict(Integer numPredict) {
            this.numPredict = numPredict;
        }
        
        public String[] getStop() {
            return stop;
        }
        
        public void setStop(String[] stop) {
            this.stop = stop;
        }
        
        public Double getRepeatPenalty() {
            return repeatPenalty;
        }
        
        public void setRepeatPenalty(Double repeatPenalty) {
            this.repeatPenalty = repeatPenalty;
        }
        
        public Integer getSeed() {
            return seed;
        }
        
        public void setSeed(Integer seed) {
            this.seed = seed;
        }
    }
}