package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.models.enums.Sex;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortUser {
    @JsonProperty("uid")
    public String userId = "";
    @JsonProperty("n")
    public String nickname = "";
    @JsonProperty("s")
    public Sex sex = Sex.MALE;
    @JsonProperty("r")
    public Integer role = 0;
    @JsonProperty("ls")
    public Long lastSeen = 0L;
    @JsonProperty("ava")
    public String avatar = "";
}
