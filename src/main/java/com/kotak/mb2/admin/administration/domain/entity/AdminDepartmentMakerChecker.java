package com.kotak.mb2.admin.administration.domain.entity;

import com.kotak.mb2.admin.administration.domain.enums.Action;
import com.kotak.mb2.admin.administration.domain.request.ActivateDelDepartmentRequest;
import com.kotak.mb2.admin.administration.domain.request.AddDepartmentRequest;
import com.kotak.mb2.admin.administration.domain.request.EditDepartmentRequest;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;
import static com.kotak.mb2.admin.administration.domain.enums.Action.ADD;
import static com.kotak.mb2.admin.administration.domain.enums.Action.EDIT;

@Entity
@Table(schema = "public", name = "\"admin_depts_maker_checker\"")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(builderClassName = "Builder", setterPrefix = "with")
public class AdminDepartmentMakerChecker extends AbstractBaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "maker")
    private String maker;

    @Column(name = "maker_status")
    private String makerStatus;

    @Column(name = "maker_comments")
    private String makerComments;

    @Column(name = "maker_date")
    private OffsetDateTime makerDate;

    @Column(name = "checker")
    private String checker;

    @Column(name = "status")
    private String status;

    @Column(name = "checker_comments")
    private String checkerComments;

    @Column(name = "checker_date")
    private OffsetDateTime checkerDate;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "rec_id")
    private Integer recId;

    @Column(name = "is_deleted")
    private String isDeleted;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private Action action;

    public static AdminDepartmentMakerChecker getAdminDepartmentMakerChecker(AddDepartmentRequest addDepartmentRequest,
                                                                             String name, String maker) {
        return AdminDepartmentMakerChecker.builder()
                .withName(name)
                .withDescription(addDepartmentRequest.description())
                .withMaker(maker)
                .withMakerStatus(STATUS_PENDING)
                .withMakerDate(OffsetDateTime.now())
                .withAction(ADD)
                .build();
    }

    public static AdminDepartmentMakerChecker setDescAndMakerForEditDept(
            EditDepartmentRequest editDepartmentRequest, String departmentName) {
        return AdminDepartmentMakerChecker.builder()
                .withName(departmentName)
                .withDescription(editDepartmentRequest.description())
                .withMaker(editDepartmentRequest.sessionUsername())
                .withMakerStatus(STATUS_PENDING)
                .withMakerDate(OffsetDateTime.now())
                .withAction(EDIT)
                .build();
    }

    public static AdminDepartmentMakerChecker setDescAndMakerForDeleteDept(String departmentName,
                                                                           String sessionUserName) {
        return AdminDepartmentMakerChecker.builder()
                .withName(departmentName)
                .withDescription(DELETED)
                .withMaker(sessionUserName)
                .withMakerStatus(STATUS_PENDING_DELETE)
                .withMakerDate(OffsetDateTime.now())
                .withAction(Action.INACTIVE)
                .build();
    }

    public static AdminDepartmentMakerChecker setDescAndMakerForActivateDept(
            ActivateDelDepartmentRequest activateDelDepartmentRequest, String departmentName) {
        return AdminDepartmentMakerChecker.builder()
                .withName(departmentName)
                .withDescription(activateDelDepartmentRequest.description())
                .withMaker(activateDelDepartmentRequest.sessionUsername())
                .withMakerStatus(STATUS_PENDING)
                .withMakerDate(OffsetDateTime.now())
                .withAction(EDIT)
                .build();
    }

    public static AdminDepartmentMakerChecker updateAdminDeptMakerChecker(AdminDepartmentMakerChecker
                                                                                  adminDepartmentMakerChecker,
                                                                          String status,
                                                                          String checker,
                                                                          String checkerComments) {
        adminDepartmentMakerChecker.setStatus(status);
        adminDepartmentMakerChecker.setChecker(checker);
        adminDepartmentMakerChecker.setCheckerComments(checkerComments);
        adminDepartmentMakerChecker.setCheckerDate(OffsetDateTime.now());
        return adminDepartmentMakerChecker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AdminDepartmentMakerChecker that = (AdminDepartmentMakerChecker) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
