package com.kotak.mb2.admin.administration.domain.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(schema = "public", name = "\"admin_dept_menu_maker_checker\"")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(builderClassName = "Builder", setterPrefix = "with")
public class AdminDeptMenuMakerChecker extends AbstractBaseEntity {

    @Column(name = "dept")
    private String dept;

    @Column(name = "menu_code")
    private String menuCode;

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

    @Column(name = "is_deleted")
    private String isDeleted;

    @Column(name = "rec_id")
    private String recId;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "description")
    private String description;

    @Column(name = "action")
    private String action;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AdminDeptMenuMakerChecker that = (AdminDeptMenuMakerChecker) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static AdminDeptMenuMakerChecker updateAdminDeptMenuMakerChecker(AdminDeptMenuMakerChecker
                                                                                    adminDeptMenuMakerChecker,
                                                                          String status,
                                                                          String checker,
                                                                          String checkerComments) {
        adminDeptMenuMakerChecker.setStatus(status);
        adminDeptMenuMakerChecker.setChecker(checker);
        adminDeptMenuMakerChecker.setCheckerComments(checkerComments);
        adminDeptMenuMakerChecker.setCheckerDate(OffsetDateTime.now());
        return adminDeptMenuMakerChecker;
    }
}
