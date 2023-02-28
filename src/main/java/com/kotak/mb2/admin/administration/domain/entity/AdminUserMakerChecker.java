package com.kotak.mb2.admin.administration.domain.entity;

import com.kotak.mb2.admin.administration.domain.enums.Action;
import com.kotak.mb2.admin.administration.domain.request.ActivateDelUserRequest;
import com.kotak.mb2.admin.administration.domain.request.EditUserRequest;
import com.kotak.mb2.admin.administration.projection.EditDepartmentUserProjection;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.domain.enums.Action.ADD;

@Entity
@Table(schema = "public", name = "\"admin_user_maker_checker\"")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(builderClassName = "Builder", setterPrefix = "with")
public class AdminUserMakerChecker extends AbstractBaseEntity {

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "empcode")
    private String empCode;

    @Column(name = "dept")
    private String dept;

    @Column(name = "role")
    private String role;

    @Column(name = "maker")
    private String maker;

    @Column(name = "maker_date")
    private OffsetDateTime makerDate;

    @Column(name = "maker_comments")
    private String makerComments;

    @Column(name = "maker_status")
    private String makerStatus;

    @Column(name = "checker")
    private String checker;

    @Column(name = "checker_date")
    private OffsetDateTime checkerDate;

    @Column(name = "checker_comments")
    private String checkerComments;

    @Column(name = "status")
    private String status;

    @Column(name = "rec_id")
    private String recId;

    @Column(name = "is_deleted")
    private String isDeleted;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private Action action;

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

    public static AdminUserMakerChecker getAdminUserMakerChecker(AdminUserDeptRoles deptUser,
                                                                 String sessionUsername) {
        return AdminUserMakerChecker.builder()
                .withUsername(deptUser.getUsername())
                .withEmpCode(deptUser.getEmpCode())
                .withDept(deptUser.getDept())
                .withRole(deptUser.getRole())
                .withMaker(sessionUsername)
                .withMakerDate(OffsetDateTime.now())
                .withMakerStatus(STATUS_PENDING)
                .withAction(ADD)
                .withDisplayName(deptUser.getDisplayName())
                .withCompany(deptUser.getCompany())
                .withGivenName(deptUser.getGivenName())
                .withSn(deptUser.getSn())
                .withMail(deptUser.getMail())
                .withMobile(deptUser.getMobile())
                .withTelephoneNo(deptUser.getTelephoneNo())
                .build();
    }

    public static AdminUserMakerChecker getAdminUserMakerCheckerForOnboardUser(AdminUserDeptRoles deptUser,
                                                                 String sessionUsername) {
        return AdminUserMakerChecker.builder()
                .withUsername(deptUser.getUsername())
                .withEmpCode(deptUser.getEmpCode())
                .withDept(deptUser.getDept())
                .withRole(deptUser.getRole())
                .withMaker(sessionUsername)
                .withMakerDate(OffsetDateTime.now())
                .withMakerStatus(STATUS_PENDING)
                .withAction(ADD)
                .withDisplayName(deptUser.getDisplayName())
                .withCompany(deptUser.getCompany())
                .withGivenName(deptUser.getGivenName())
                .withSn(deptUser.getSn())
                .withMail(deptUser.getMail())
                .withMobile(deptUser.getMobile())
                .withTelephoneNo(deptUser.getTelephoneNo())
                .withStatus(STATUS_ACTIVE)
                .withCheckerComments("Auto Approved")
                .build();
    }

    public static AdminUserMakerChecker updateAdminUserMakerChecker(AdminUserMakerChecker adminUserMakerChecker,
                                                                    String status,
                                                                    String sessionUsername,
                                                                    String checkerComments) {
        adminUserMakerChecker.setStatus(status);
        adminUserMakerChecker.setChecker(sessionUsername);
        adminUserMakerChecker.setCheckerComments(checkerComments);
        adminUserMakerChecker.setCheckerDate(OffsetDateTime.now());
        if (status.equals(STATUS_DELETE)) {
            adminUserMakerChecker.setDeletedBy(sessionUsername);
            adminUserMakerChecker.setDeletedAt(OffsetDateTime.now());
        }
        return adminUserMakerChecker;
    }

    public static AdminUserMakerChecker deleteAdminUserMakerChecker(AdminUserDeptRoles deptUser,
                                                                    String sessionUsername) {
        return AdminUserMakerChecker.builder()
                .withUsername(deptUser.getUsername())
                .withEmpCode(deptUser.getEmpCode())
                .withDept(deptUser.getDept())
                .withRole(deptUser.getRole())
                .withMaker(sessionUsername)
                .withMakerDate(OffsetDateTime.now())
                .withMakerStatus(STATUS_PENDING)
                .withAction(Action.INACTIVE)
                .withDisplayName(deptUser.getDisplayName())
                .withCompany(deptUser.getCompany())
                .withGivenName(deptUser.getGivenName())
                .withSn(deptUser.getSn())
                .withMail(deptUser.getMail())
                .withMobile(deptUser.getMobile())
                .withTelephoneNo(deptUser.getTelephoneNo())
                .build();
    }

    public static AdminUserMakerChecker getAdminUserMakerCheckerForEditUser(EditDepartmentUserProjection data,
                                                                            EditUserRequest request) {
        return AdminUserMakerChecker.builder()
                .withUsername(request.username())
                .withEmpCode(data.getEmpCode())
                .withDept(StringUtils.isEmpty(request.dept()) ? data.getDept() : request.dept())
                .withRole(StringUtils.isEmpty(request.role()) ? data.getRole() : request.role())
                .withMaker(request.sessionUsername())
                .withMakerDate(OffsetDateTime.now())
                .withMakerStatus(STATUS_PENDING)
                .withAction(Action.EDIT)
                .withDisplayName(data.getDisplayName())
                .withCompany(data.getCompany())
                .withAdDept(data.getAdDept())
                .withGivenName(data.getGivenName())
                .withSn(data.getSn())
                .withMail(data.getMail())
                .withMobile(data.getMobile())
                .withTelephoneNo(data.getTelephoneNo())
                .build();
    }

    public static AdminUserMakerChecker getAdminUserMakerCheckerForActivateDelUser(AdminUserDeptRoles data,
                                                                                   ActivateDelUserRequest request) {
        return AdminUserMakerChecker.builder()
                .withUsername(data.getUsername())
                .withEmpCode(data.getEmpCode())
                .withDept(data.getDept())
                //.withRole(StringUtils.isEmpty(request.sessionRole()) ? data.getRole() : request.sessionRole())
                .withRole(data.getRole())
                .withMaker(request.sessionUsername())
                .withMakerDate(OffsetDateTime.now())
                .withMakerStatus(STATUS_PENDING)
                .withAction(Action.EDIT)
                .withDisplayName(data.getDisplayName())
                .withCompany(data.getCompany())
                .withAdDept(data.getAdDept())
                .withGivenName(data.getGivenName())
                .withSn(data.getSn())
                .withMail(data.getMail())
                .withMobile(data.getMobile())
                .withTelephoneNo(data.getTelephoneNo())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AdminUserMakerChecker that = (AdminUserMakerChecker) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
