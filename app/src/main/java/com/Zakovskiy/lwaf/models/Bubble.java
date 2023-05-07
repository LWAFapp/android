package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.models.enums.BubbleType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bubble {
    @JsonProperty("t")
    public Integer type = 0;
    @JsonProperty("ss")
    public String sourceSend = "";
    @JsonProperty("sr")
    public String sourceReceive = "";
    @JsonProperty("bt")
    public BubbleType bubbleType = BubbleType.DEFAULT;

    public String[] getSourceSend() {
        /*
         * 0 index - color of text
         * 1 index - color/url of background bubble
         * 2 (no require) index - second color for gradien background bubble
         */
        String[] resources = this.sourceSend.split(", ");
        return resources;
    }

    public String[] getSourceReceive() {
        /*
         * 0 index - color of text
         * 1 index - color/url of background bubble
         * 2 (no require) index - second color for gradien background bubble
         */
        String[] resources = this.sourceReceive.split(", ");
        return resources;
    }
}
