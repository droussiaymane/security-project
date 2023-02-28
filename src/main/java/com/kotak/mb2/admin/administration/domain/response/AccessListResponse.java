package com.kotak.mb2.admin.administration.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.kotak.mb2.admin.administration.constants.AppConstants.STATUS_ACTIVE;

@Data
public class AccessListResponse {
    @JsonProperty("dept_id")
    private String deptId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("menu_code")
    private String menuCode;

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("children")
    private List<AccessListResponse> children = new Vector<>();

    @JsonProperty("menu_id")
    private String menuId;

    public static void fillMenus(MenuProfileResponse menu, List<AccessListResponse> responseList) {
        final AccessListResponse departmentAccessResponse = new AccessListResponse();

        departmentAccessResponse.setDeptId(menu.getDeptId());
        departmentAccessResponse.setDescription(menu.getDescription());
        departmentAccessResponse.setMenuCode(menu.getMenuName());
        departmentAccessResponse.setMenuId(menu.getMenuId());
        departmentAccessResponse.setStatus(menu.getStatus());

        if (menu.hasChildren()) {
            final List<AccessListResponse> childrenArray = new ArrayList<>();
            departmentAccessResponse.setChildren(childrenArray);
            for (MenuProfileResponse menuProfileResponse : menu.getChildren()) {
                fillMenus(menuProfileResponse, childrenArray);
            }
        }
        responseList.add(departmentAccessResponse);
    }
}
