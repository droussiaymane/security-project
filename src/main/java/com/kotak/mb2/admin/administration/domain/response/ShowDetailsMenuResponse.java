package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMakerChecker;
import com.kotak.mb2.admin.administration.projection.MenuPendingActionProjection;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.constants.AppConstants.STATUS_DELETE;

@SuperBuilder
@Getter
public class ShowDetailsMenuResponse {
    @JsonProperty("department_name")
    private String departmentName;

    @JsonProperty("menu_code")
    private String menuCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("action")
    private String action;

    public static ShowDetailsMenuResponse menuDetailsResponse(MenuPendingActionProjection menuPendingActionProjection, Map<String,ShowDetailsMenuResponse> menuResponseMap) {


        ShowDetailsMenuResponse showDetailsMenuResponse =  ShowDetailsMenuResponse.builder()
                .departmentName(menuPendingActionProjection.getDept())
                .menuCode(menuPendingActionProjection.getMenuCode())
                .description(menuPendingActionProjection.getDescription())
                .action(menuPendingActionProjection.getAction())
                .build();

        menuResponseMap.put(menuPendingActionProjection.getMenuCode(), showDetailsMenuResponse);
        return showDetailsMenuResponse;


    }

}
