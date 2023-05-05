package com.Zakovskiy.lwaf.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelTrackResponse {
    @JsonProperty("artist")
    public String artist = "";
    @JsonProperty("id")
    public Integer id = 0;
    @JsonProperty("owner_id")
    public Integer ownerId = 0;
    @JsonProperty("title")
    public String title = "";
    @JsonProperty("duration")
    public Integer duration = 0;
    @JsonProperty("url")
    public String url = "";
    @JsonProperty("album")
    public Album album = new Album();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Album {
        @JsonProperty("thumb")
        public Thumb thumb = new Thumb();

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public class Thumb {
            @JsonProperty("width")
            public Integer width = 0;
            @JsonProperty("height")
            public Integer height = 0;
            @JsonProperty("photo_1200")
            public String photo_1200 = "";
            @JsonProperty("photo_600")
            public String photo_600 = "";
        }
    }
}