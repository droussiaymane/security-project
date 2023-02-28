package com.kotak.mb2.admin.administration.domain.entity;

import lombok.*;
import org.hibernate.Hibernate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(schema = "public", name = "\"admin_session_token_details\"")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder(builderClassName = "Builder", setterPrefix = "with")
public class AdminSessionTokenDetails extends  AbstractBaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "jwt_token")
    private String jwtToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AdminSessionTokenDetails that = (AdminSessionTokenDetails) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
