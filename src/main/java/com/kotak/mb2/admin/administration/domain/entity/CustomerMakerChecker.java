package com.kotak.mb2.admin.administration.domain.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.kotak.mb2.admin.administration.constants.AppConstants.*;


@Entity
@Table(schema = "public", name = "\"customer_maker_checker\"")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(builderClassName = "Builder", setterPrefix = "with")
public class CustomerMakerChecker extends AbstractBaseEntity {

    @Column(name = "action")
    private String action;

    @Column(name = "maker_status")
    private String makerStatus;

    @Column(name = "maker_date")
    private OffsetDateTime makerDate;

    @Column(name = "crn")
    private String crn;

    @Column(name = "maker")
    private String maker;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "reg_key")
    private String regKey;

    @Column(name = "crn_status")
    private String crnStatus;

    @Column(name = "maker_comments")
    private String makerComments;

    @Column(name = "checker_date")
    private OffsetDateTime checkerDate;

    @Column(name = "status")
    private String status;

    @Column(name = "checker_comments")
    private String checkerComments;

    @Column(name = "rec_id")
    private String recId;

    @Column(name = "is_deleted")
    private String isDeleted;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "checker")
    private String checker;

    @Column(name = "type")
    private String type;

    @Column(name = "block_reason")
    private String blockReason;

    @Column(name = "requester_id")
    private String requestId;

    public static CustomerMakerChecker getCustomerMakerChecker(String crn, String action, String username,
                                                               String appId, String type) {
        return CustomerMakerChecker.builder()
                .withAction(action)
                .withMakerStatus(STATUS_PENDING)
                .withMakerDate(OffsetDateTime.now())
                .withCrn(crn)
                .withMaker(username)
                .withAppId(appId)
                .withType(type)
                .build();
    }

    public static CustomerMakerChecker getCustomerMakerCheckerBlockUnblock(String crn, String action, String username,
                                                                           String makerComments, String crnStatus, String type, String blockReason) {
        return CustomerMakerChecker.builder()
                .withAction(action)
                .withMakerStatus(STATUS_PENDING)
                .withMakerDate(OffsetDateTime.now())
                .withMakerComments(makerComments)
                .withCrn(crn)
                .withCrnStatus(crnStatus)
                .withMaker(username)
                .withType(type)
                .withBlockReason(blockReason)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CustomerMakerChecker that = (CustomerMakerChecker) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
