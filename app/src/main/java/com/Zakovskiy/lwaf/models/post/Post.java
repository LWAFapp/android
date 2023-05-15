package com.Zakovskiy.lwaf.models.post;

import com.Zakovskiy.lwaf.models.ShortUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {
    @JsonProperty("pid")
    public String id = "";
    @JsonProperty("pt")
    public String title = "";
    @JsonProperty("ptc")
    public Long time = 0L;
    @JsonProperty("pcn")
    public String content = "";
    @JsonProperty("pa")
    public ShortUser author = new ShortUser();
    @JsonProperty("pcm")
    public List<PostComment> comments = new ArrayList<>();
    @JsonProperty("pli")
    public List<PostReaction> postLikes = new ArrayList<>();
    @JsonProperty("pdi")
    public List<PostReaction> postDislikes = new ArrayList<>();
}
