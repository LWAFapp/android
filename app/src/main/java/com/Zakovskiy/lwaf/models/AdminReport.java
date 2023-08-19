package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.models.enums.ReportStatus;
import com.Zakovskiy.lwaf.models.enums.ReportType;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminReport {
    @JsonProperty(PacketDataKeys.REPORT_ID)
    public String reportId = "";
    @JsonProperty(PacketDataKeys.CONTENT)
    public String content = "";
    @JsonProperty(PacketDataKeys.STATUS)
    public ReportStatus status = ReportStatus.OPEN;
    @JsonProperty(PacketDataKeys.TO_USER)
    public ShortUser toUser = new ShortUser();
    @JsonProperty(PacketDataKeys.FROM_USER)
    public ShortUser fromUser = new ShortUser();
    @JsonProperty(PacketDataKeys.TYPE)
    public ReportType type = ReportType.USER;
    @JsonProperty(PacketDataKeys.TIME_CREATE)
    public Long timeCreate = 0L;
    @JsonProperty(PacketDataKeys.TIME_CLOSE)
    public Long timeClose = 0L;
}
