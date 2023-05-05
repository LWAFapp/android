package com.Zakovskiy.lwaf.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerConfig {
    @JsonProperty("services")
    public Services services = new Services();
    @JsonProperty("loto_counts")
    public Integer lotoCounts = 0;
    @JsonProperty("loto_guessed_numbers")
    public Integer lotoGuessedNumbers = 6;
    @JsonProperty("loto_timeout")
    public Integer lotoTimeout = 0;
    @JsonProperty("online_time")
    public Integer onlineTime = 0;
    @JsonProperty("colors")
    public Colors colors = new Colors();
    @JsonProperty("imgs_path")
    public String imgsPath = "";
    @JsonProperty("news_user_id")
    public String newsUserId = "";
    @JsonProperty("root_user_id")
    public String rootUserId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Services {
        @JsonProperty("tracks")
        public Boolean tracks = true;
        @JsonProperty("rooms")
        public Boolean rooms = true;
        @JsonProperty("global_conversation")
        public Boolean globalConversation = true;
        @JsonProperty("authorization")
        public Boolean authorization = true;
        @JsonProperty("friends")
        public Boolean friends = true;
        @JsonProperty("posts")
        public  Boolean posts = true;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Colors {
        @JsonProperty("left_chat")
        public String leftChat = "";
        @JsonProperty("set_like_on_track")
        public String setLikeOnTrack = "";
        @JsonProperty("set_superlike_on_track")
        public String setSuperLikeOnTrack = "";
        @JsonProperty("remove_message_btn")
        public String remove_message_btn = "";
    }
}
