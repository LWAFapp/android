package com.Zakovskiy.lwaf.models.post;

import com.Zakovskiy.lwaf.models.ShortUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostComment {
    @JsonProperty("cid")
    public String commentId = "";
    @JsonProperty("ts")
    public Long timestamp = 0L;
    @JsonProperty("pid")
    public String postId = "";
    @JsonProperty("c")
    public String content = "";
    @JsonProperty("u")
    public ShortUser user = new ShortUser();
    @JsonProperty("rcid")
    public String replyCommentId = "";
    @JsonProperty("cr")
    public List<PostComment> replyComments = new ArrayList<>();
}
