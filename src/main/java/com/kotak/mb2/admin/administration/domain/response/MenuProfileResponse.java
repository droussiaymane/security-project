package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.domain.entity.AdminDepartments;
import com.kotak.mb2.admin.administration.projection.AdminMenuProfileProjection;
import lombok.Data;

import java.util.List;
import java.util.Vector;

@Data
public class MenuProfileResponse {
    private final List<MenuProfileResponse> children = new Vector<>();
    private String menuName;
    private String parentMenuName;
    private String title;
    private String description;
    private String deptId;
    private String dept;
    private String menuId;
    private Boolean status;

    public static MenuProfileResponse setParentMenuNameOfMenuProfile(AdminMenuProfileProjection
                                                                             adminMenuProfileProjection) {
        final MenuProfileResponse menuProfileResponse = new MenuProfileResponse();
        menuProfileResponse.setParentMenuName(adminMenuProfileProjection.getParentMenuName());
        menuProfileResponse.setMenuName(adminMenuProfileProjection.getParentMenuName());
        return menuProfileResponse;
    }

    public static MenuProfileResponse setMenuNameOfMenuProfile(AdminMenuProfileProjection
                                                                       adminMenuProfileProjection) {
        final MenuProfileResponse menuProfileResponse = new MenuProfileResponse();
        menuProfileResponse.setMenuName(adminMenuProfileProjection.getMenuName());
        return menuProfileResponse;
    }

    public static void setChildDetails(MenuProfileResponse childMenu, String deptId,
                                       AdminMenuProfileProjection menu, AdminDepartments adminDepartments) {
        childMenu.setDeptId(deptId);
        childMenu.setMenuId(menu.getId());
        childMenu.setDescription(menu.getDescription());
        childMenu.setStatus(adminDepartments.getName().equalsIgnoreCase(menu.getDept()));
    }

    public boolean hasChildren() {
        return this.children.size() > 0;
    }

    public boolean hasChild(String menuCode) {
        for (MenuProfileResponse menu : this.children)
            if (menu.getMenuName().equals(menuCode)) {
                return true;
            }
        return false;
    }

    public void addChild(MenuProfileResponse menu) {
        this.children.add(menu);
    }
}
