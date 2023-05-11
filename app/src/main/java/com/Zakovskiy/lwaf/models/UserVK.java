package com.Zakovskiy.lwaf.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVK {
    @JsonProperty("vk_secret")
    public String vkSecret = "";
    @JsonProperty("vk_token")
    public String vkToken = "";
    @JsonProperty("vk_id")
    public Integer vkId = 0;
}
