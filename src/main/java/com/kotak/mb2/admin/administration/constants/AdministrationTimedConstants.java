package com.kotak.mb2.admin.administration.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AdministrationTimedConstants {

    //Department Controller
    public static final String GET_DEPARTMENTS = "v1.getDepartments";
    public static final String ADD_DEPARTMENT = "v1.addDepartment";
    public static final String EDIT_DEPARTMENT = "v1.editDepartment";
    public static final String DELETE_DEPARTMENT = "v1.deleteDepartment";
    public static final String ACTIVATE_DEL_DEPARTMENT = "v1.activateDelDepartment";
    public static final String LIST_DEPT_ACCESS = "v1.listDeptAccess";
    public static final String UPDATE_DEPT_ACCESS = "v1.updateDeptAccess";
    public static final String GET_DEPT_USERS = "v1.getDepartmentUsers";
    public static final String APPROVE_DEPARTMENT = "v1.approveDepartment";
    public static final String REJECT_DEPARTMENT = "v1.rejectDepartment";

    //User Controller
    public static final String CHECK_VALID_USER = "v1.checkValidUser";
    public static final String ADD_DEPARTMENT_USER = "v1.addDepartmentUser";
    public static final String LIST_ALL_USERS = "v1.listAllUsers";
    public static final String GET_PENDING_ACTIONS = "v1.getPendingActions";
    public static final String APPROVE_DEPT_USER = "v1.approveDepartmentUser";
    public static final String REJECT_DEPT_USER = "v1.rejectDepartmentUser";
    public static final String DELETE_DEPT_USER = "v1.deleteDepartmentUser";
    public static final String EDIT_DEPT_USER = "v1.editDepartmentUser";
    public static final String COMPLETED_ACTIVITIES = "v1.getCompletedActivities";

    //PendingActionCount Controller
    public static final String PENDING_ACTIONS_COUNT = "v1.getPendingActionsCount";
    public static final String ACTIVATE_DEL_USER = "v1.activateDelUser";
    public static final String SHOW_DETAILS = "v1.showDetails";
    public static final String SHOW_MENU_DETAILS = "v1.showMenuDetails";
}
