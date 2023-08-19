package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.models.enums.OperationType;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Operation implements Serializable {
    @JsonProperty(PacketDataKeys.USER_ID)
    public String userId;
    @JsonProperty(PacketDataKeys.OPERATION_TYPE)
    public OperationType operationType;
    @JsonProperty(PacketDataKeys.VALUE)
    public Integer value;
    @JsonProperty(PacketDataKeys.TIMESTAMP)
    public Long timeStamp;
    @JsonProperty(PacketDataKeys.EXTENSION)
    public JsonNode extension;
}
