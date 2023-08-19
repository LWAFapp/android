package com.Zakovskiy.lwaf.models.post;

import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostInList {
    @JsonProperty(PacketDataKeys.POST_ID)
    public String id = "";
    @JsonProperty(PacketDataKeys.PREVIEW_ID)
    public String previewId = "";
    @JsonProperty(PacketDataKeys.POST_TITLE)
    public String title = "";
    @JsonProperty(PacketDataKeys.POST_TIME_CREATE)
    public Long time = 0L;
    @JsonProperty(PacketDataKeys.POST_CONTENT)
    public String content = "";
    @JsonProperty(PacketDataKeys.POST_AUTHOR)
    public ShortUser author = new ShortUser();
    @JsonProperty(PacketDataKeys.POST_LIKES)
    public Integer likes = 0;
    @JsonProperty(PacketDataKeys.POST_DISLIKES)
    public Integer dislikes = 0;
    @JsonProperty(PacketDataKeys.POST_COMMENTS)
    public Integer comments = 0;
}
