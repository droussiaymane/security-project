package com.kotak.mb2.admin.administration.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AppConstants {

    public static final int SUCCESS = 0;
    public static final int FAILED = 1;

    public static final String SUCCESS_CODE = "0";

    public static final String SUCCESS_DESC = "SUCCESS";
    public static final String FAILED_DESC = "FAILED";

    public static final String ROLE_ADMIN = "ADMIN";

    public static final String STATUS_PENDING = "PEN";
    public static final String STATUS_ACTIVE = "ACT";
    public static final String STATUS_INACTIVE = "INA";
    public static final String STATUS_PENDING_DELETE = "PDL";
    public static final String STATUS_DELETE = "DEL";
    public static final String STATUS_REJECTED = "REJ";

    public static final String DEPT_ALREADY_EXIST = "Department already exist";
    public static final String CANNOT_EDIT_OWN_DEPT = "You cannot edit your own department";
    public static final String DEPT_ADDED = "Department is added";
    public static final String PENDING_DEPT_REQ = "Pending dependent requests";
    public static final String DEPT_STATUS_UPDATED = "Department status is updated";
    public static final String DEPT_ACCESS_UPDATED = "Department access is updated";

    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "User already exists in ";
    public static final String USER_ALREADY_PENDING_FOR_APPROVAL = "User already pending for approval";
    public static final String REQUEST_ALREADY_PENDING_FOR_APPROVAL = "Request already pending for approval";
    public static final String DEPT_NOT_EXISTS = "Department does not exist";
    public static final String USER_ADDED_AND_APPROVED = "User is added and approved";
    public static final String RECORD_ALREADY_APPROVE_OR_REJECTED = "The record is already Approved/Rejected";
    public static final String MAKER_AND_CHECKER_ARE_SAME = "Maker and Checker are same";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    public static final String SHOULD_NOT_BE_BLANK = "should not be blank";
    public static final String INVALID_DEPT = "Invalid department";
    public static final String USER_EDITED_SUCCESSFULLY = "User edited successfully";
    public static final String ADDED = "Added";
    public static final String UPDATED = "Updated";
    public static final String DELETED = "Deleted";
    public static final String ACTIVE = "Active";
    public static final String INACTIVE = "Inactive";
    public static final String CANNOT_EDIT_OWN_DETAILS = "You cannot edit your details";

    //modules
    public static final String ADMIN_USER = "adminUser";
    public static final String ADMIN_DEPT = "adminDept";
    public static final String ADMIN_DEPT_MENU = "adminDeptMenu";
    public static String COMM0N_API_AES_ENC_KEY = "8e8289881c818782";

    public static String PUBLIC_KEY = "";
    public static String PRIVATE_KEY = "";
    public static String PUBLIC_KEY_SIMBINDING = "";
    public static String PRIVATE_KEY_SIMBINDING = "";

    public static String ADD_USER_VALID_REQUEST_IP = "160.1.10.154,10.10.19.153,160.1.10.79,10.10.20.120";
    public static String ADD_USER_CHANNEL_IDS = "MSF|ADMIN|KRAMAN";

    public static final String ENABLED = "Enabled";
    public static final String DISABLED = "Disabled";
    public static final String INVALID_USR = "EGN003";

    public static final String ADD_USER_INVALID_IP = "EGN007";
    public static final String USERNAME_VALIDATION = "ERP001";
    public static final String DEPT_VALIDATION = "ERP002";
    public static final String ROLE_VALIDATION = "ERP003";
    public static final String CHANNEL_ID_VALIDATION = "ERP004";
    public static final String LDAP_EXCEPTION="ELD001";
    public static final String ADD_USER_CHANNEL_ID_INVALID="ELD002";
    public static final String INVALID_ACTION_TYPE="ELD003";
    public static final String DUPLICATE_REQ="EGN006";
    public static final String REQUEST_FAILED="EGN0015";
    public static final String USER_ALREADY_EXIST="EGN004";
    public static final String INVALID_DEPT_CODE="EGN005";
    public static final String PENDING_REQUEST_CODE="EGN009";

    @Deprecated
    public static final String P2C_TRANSACTION_LIMIT = "ELG0078";

    public static final String CLASHING_CRN_1 = "ELG0042";
    public static final String CLASHING_CRN_2 = "ELG0044";
    public static String CLASHING_CRN = CLASHING_CRN_1;

    public static final String COMMON_CRN_1 = "ELG0043";
    public static final String COMMON_CRN_2 = "ELG0045";
    public static final String SERVICE_UNAVAILABLE_CODE = "ELG0046";
    public static final String IMEI_BLOCK = "ELG0047";
    public static String COMMON_CRN = COMMON_CRN_1;

    public static final String ERROR_CODE_91_M0 = "EFT0091";

    public static final String ADD_USER_INVALID_IP_MSG = "Invalid IP";

    public static final String ADD_USER_CHANNEL_ID_INVALID_MSG="Invalid Channel ID";

    public static final String INVALID_ACTION_TYPE_MSG="Invalid action type";

    public static final String USERNAME_VALIDATION_MSG="Username should not be null or empty";

    public static final String DEPT_VALIDATION_MSG="Dept should not be null or empty";

    public static final String ROLE_VALIDATION_MSG="Role should not be null or empty";

    public static final String CHANNEL_ID_VALIDATION_MSG="Channel_Id should not be null or empty";

    public static final String INVALID_USR_MSG="Invalid User";

    public static final String USER_ADDED_MSG="User is Added";

    public static final String USER_DEACTIVATE_MSG="User is deactivated";

    public static final String DUPLICATE_REQUEST_MSG="Duplicate request";

    public static final String REQUEST_FAILED_MSG="Request Failed";

    public static final String USER_ALREADY_EXIST_MSG="User already exists";

    public static final String INVALID_DEPT_MSG="Invalid dept";

    public static final String PENDING_REQUEST_MSG = "Pending request";
    public static final String BLOCK = "BLOCK";
    public static final String UNBLOCK = "UNBLOCK";
    public static final String DEACTIVATE = "DEACTIVATE";
    public static final String PHONE_ACTIVATE = "PHONE_ACTIVATE";
    public static final String BRANCH_ACTIVATE= "BRANCH_ACTIVATE";
    public static final String MPIN_UNLOCK = "MPINUNLOCK";
    public static final String EMPTY_DATA = "Please select atlease one request";
    public static final String ADD_CRN = "ADD_CRN";
    public static final String ACTIVATE_DEVICE = "ACT_DEVICE";
    public static final String BLOCK_DEVICE = "BLK_DEVICE";
    public static final String CRN_GN_MPIN_ACT = "CRN_GN_MPIN_ACT";
    public static final String REQUESTED_DETAILS = "Requested";
    public static final String EXISTING_DETAILS = "Existing";
    public static final String CUSTOMER_MANAGEMENT = "CustomerManagement";
    public static final String SESSION_ROLE_ERR="Invalid Session Role";
    public static final String DEPT_NAME_ERR="Invalid Dept Name";
    public static final String USERNAME_ERR="Invalid UserName";
    public static final String DEPARTMENT_ID_ERR="Invalid deptID";
    public static final String USER_ID_ERR="Invalid userID";
    public static final String ID_ERR="Invalid ID";
    public static final String NAME_ERR="Invalid name";
    public static final String DESCRIPTION_ERR="Invalid description";
    public static final String ROLE_ERR="Invalid role";
    public static final String COMMENT_ERR="Invalid comment";
    public static final String SESSION_USERNAME_ERR="Invalid session user name";




}
