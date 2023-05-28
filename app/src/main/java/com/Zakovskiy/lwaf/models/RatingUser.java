package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.models.enums.RatingsType;
import com.Zakovskiy.lwaf.models.enums.Sex;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatingUser extends ShortUser {
    @JsonProperty("v")
    public Integer value = 0;
    @JsonProperty("rt")
    public RatingsType ratingsType = RatingsType.SUPER_LIKES;
}
