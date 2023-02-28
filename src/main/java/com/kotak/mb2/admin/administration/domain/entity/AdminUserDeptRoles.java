package com.kotak.mb2.admin.administration.domain.entity;

import com.kotak.mb2.admin.administration.domain.request.AddUserObject;
import com.kotak.mb2.admin.administration.domain.request.AddUserRequest;
import com.kotak.mb2.admin.administration.domain.response.CheckValidUserResponse;
import lombok.*;
import org.hibernate.Hibernate;
import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Hashtable;
import java.util.Objects;

import static com.kotak.mb2.admin.administration.constants.AppConstants.STATUS_ACTIVE;

@Entity
@Table(schema = "public", name = "\"admin_user_dept_roles\"")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(builderClassName = "Builder", setterPrefix = "with")
public class AdminUserDeptRoles extends AbstractBaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "role")
    private String role;

    @Column(name = "status")
    private String status;

    @Column(name = "dept")
    private String dept;

    @Column(name = "empcode")
    private String empCode;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "company")
    private String company;

    @Column(name = "ad_dept")
    private String adDept;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "sn")
    private String sn;

    @Column(name = "mail")
    private String mail;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "telephone_no")
    private String telephoneNo;

    public static AdminUserDeptRoles    getAdminUserDeptRoles(AddUserRequest user, CheckValidUserResponse userData) {
        return AdminUserDeptRoles.builder()
                .withUsername(user.username())
                .withRole(user.role())
                .withEmpCode(userData.empCode())
                .withDisplayName(userData.displayName())
                .withCompany(userData.company())
                .withDept(userData.department())
                .withGivenName(userData.givenName())
                .withSn(userData.sn())
                .withMail(userData.mail())
                .withMobile(userData.mobile())
                .withTelephoneNo(userData.telephoneNumber())
                .build();
    }

    public static AdminUserDeptRoles getAdminUserDeptRolesForNewUser(String username, String role,
                                                                     Hashtable<String, String> userData) {
        return AdminUserDeptRoles.builder()
                .withUsername(username)
                .withRole(role)
                .withEmpCode(userData.get("employeeNumber"))
                .withDisplayName(userData.get("displayName"))
                .withCompany(userData.get("company"))
                .withDept(userData.get("department"))
                .withGivenName(userData.get("givenName"))
                .withSn(userData.get("sn"))
                .withMail(userData.get("mail"))
                .withMobile(userData.get("mobile"))
                .withTelephoneNo(userData.get("telephoneNumber"))
                .build();
    }

    public static AdminUserDeptRoles from(AdminUserMakerChecker adminUserMakerChecker) {
        return AdminUserDeptRoles.builder()
                .withUsername(adminUserMakerChecker.getUsername())
                .withEmpCode(adminUserMakerChecker.getEmpCode())
                .withDept(adminUserMakerChecker.getDept())
                .withRole(adminUserMakerChecker.getRole())
                .withStatus(adminUserMakerChecker.getStatus())
                .withDisplayName(adminUserMakerChecker.getDisplayName())
                .withCompany(adminUserMakerChecker.getCompany())
                .withAdDept(adminUserMakerChecker.getAdDept())
                .withGivenName(adminUserMakerChecker.getGivenName())
                .withSn(adminUserMakerChecker.getSn())
                .withMail(adminUserMakerChecker.getMail())
                .withMobile(adminUserMakerChecker.getMobile())
                .withTelephoneNo(adminUserMakerChecker.getTelephoneNo())
                .build();
    }

    public static AdminUserDeptRoles updateAdminUserDeptRoles(AdminUserDeptRoles adminUserDeptRoles,
                                                              AdminUserMakerChecker adminUserMakerChecker) {
        adminUserDeptRoles.setDept(adminUserMakerChecker.getDept());
        adminUserDeptRoles.setEmpCode(adminUserMakerChecker.getEmpCode());
        adminUserDeptRoles.setRole(adminUserMakerChecker.getRole());
        adminUserDeptRoles.setStatus(STATUS_ACTIVE);
        adminUserDeptRoles.setDisplayName(adminUserMakerChecker.getDisplayName());
        adminUserDeptRoles.setCompany(adminUserMakerChecker.getCompany());
        adminUserDeptRoles.setAdDept(adminUserMakerChecker.getAdDept());
        adminUserDeptRoles.setGivenName(adminUserMakerChecker.getGivenName());
        adminUserDeptRoles.setSn(adminUserMakerChecker.getSn());
        adminUserDeptRoles.setMail(adminUserMakerChecker.getMail());
        adminUserDeptRoles.setMobile(adminUserMakerChecker.getMobile());
        adminUserDeptRoles.setTelephoneNo(adminUserMakerChecker.getTelephoneNo());
        return adminUserDeptRoles;
    }

    public static AdminUserDeptRoles updateAdminUserDeptRolesForOnboardUser(AdminUserDeptRoles adminUserDeptRoles,
                                                                            AdminUserDeptRoles addUserRequestObj) {
        adminUserDeptRoles.setDept(addUserRequestObj.getDept());
        adminUserDeptRoles.setEmpCode(addUserRequestObj.getEmpCode());
        adminUserDeptRoles.setRole(addUserRequestObj.getRole());
        adminUserDeptRoles.setStatus(STATUS_ACTIVE);
        adminUserDeptRoles.setDisplayName(addUserRequestObj.getDisplayName());
        adminUserDeptRoles.setCompany(addUserRequestObj.getCompany());
        adminUserDeptRoles.setAdDept(addUserRequestObj.getAdDept());
        adminUserDeptRoles.setGivenName(addUserRequestObj.getGivenName());
        adminUserDeptRoles.setSn(addUserRequestObj.getSn());
        adminUserDeptRoles.setMail(addUserRequestObj.getMail());
        adminUserDeptRoles.setMobile(addUserRequestObj.getMobile());
        adminUserDeptRoles.setTelephoneNo(addUserRequestObj.getTelephoneNo());
        return adminUserDeptRoles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AdminUserDeptRoles that = (AdminUserDeptRoles) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
