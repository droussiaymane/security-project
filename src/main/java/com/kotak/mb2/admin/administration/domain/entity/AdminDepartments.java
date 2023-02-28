package com.kotak.mb2.admin.administration.domain.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

import static com.kotak.mb2.admin.administration.constants.AppConstants.STATUS_ACTIVE;

@Entity
@Table(schema = "public", name = "\"admin_departments\"")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(builderClassName = "Builder", setterPrefix = "with")
public class AdminDepartments extends AbstractBaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    public static AdminDepartments from(AdminDepartmentMakerChecker updatedDeptMakerChecker) {
        return AdminDepartments.builder()
                .withName(updatedDeptMakerChecker.getName())
                .withDescription(updatedDeptMakerChecker.getDescription())
                .withStatus(STATUS_ACTIVE)
                .build();
    }

    public static AdminDepartments updateFrom(AdminDepartments adminDepartments, String status, String description) {
        adminDepartments.setDescription(description);
        adminDepartments.setStatus(status);
        return adminDepartments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AdminDepartments that = (AdminDepartments) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
