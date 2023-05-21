package com.Zakovskiy.lwaf.models.post;

import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.models.enums.CommentReaction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostReaction {
    @JsonProperty("pid")
    public String postId = "";
    @JsonProperty("u")
    public ShortUser user;
    @JsonProperty("t")
    public CommentReaction type = CommentReaction.DISLIKE;
    @JsonProperty("ts")
    public Integer timestamp;

}
