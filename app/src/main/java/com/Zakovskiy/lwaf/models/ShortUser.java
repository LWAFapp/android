package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.enums.Sex;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortUser implements Serializable {
    @JsonProperty(PacketDataKeys.USER_ID)
    public String userId = "";
    @JsonProperty(PacketDataKeys.NICKNAME)
    public String nickname = "";
    @JsonProperty(PacketDataKeys.SEX)
    public Sex sex = Sex.MALE;
    @JsonProperty(PacketDataKeys.ROLE)
    public Integer role = 0;
    @JsonProperty(PacketDataKeys.LAST_SEEN)
    public Long lastSeen = 0L;
    @JsonProperty(PacketDataKeys.AVATAR)
    public String avatar = "";

    public boolean isAdmin() {
        return this.role >= 5;
    }

    public Boolean isUnallowedService() {
        return this.role >= Application.lwafServerConfig.allowed.unallowedService;
    }

    public Boolean isDb() {
        return this.role >= Application.lwafServerConfig.allowed.db;
    }

    public Boolean isReports() {
        return this.role >= Application.lwafServerConfig.allowed.reports;
    }

    public Boolean isClients() {
        return this.role >= Application.lwafServerConfig.allowed.reports;
    }

    public Boolean isBan() {
        return this.role >= Application.lwafServerConfig.allowed.ban;
    }
}
