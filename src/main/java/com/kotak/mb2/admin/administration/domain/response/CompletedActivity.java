package com.kotak.mb2.admin.administration.domain.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.json.JSONObject;

import java.util.List;

@SuperBuilder
@Getter
public class CompletedActivity {
    private String module;
    private String maker;
    private String makerDate;
    private String makerComments;
    private String makerAction;
    private String authorizedBy;
    private String authorizedComments;
    private String authorizedDate;
    private String authorizedAction;
    private String type;
    private String status;
    private List<MenuResponse> menuList;
    private String department;
}
