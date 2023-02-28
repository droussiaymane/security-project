package com.kotak.mb2.admin.administration.domain.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(schema = "public", name = "\"admin_departments_menu_access\"")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(builderClassName = "Builder", setterPrefix = "with")
public class AdminDepartmentMenuAccess extends AbstractBaseEntity {

    @Column(name = "dept")
    private String dept;

    @Column(name = "menu_code")
    private String menuCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AdminDepartmentMenuAccess that = (AdminDepartmentMenuAccess) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
