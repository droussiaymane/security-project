package com.kotak.mb2.admin.administration.domain.entity;

import lombok.*;
import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.Hashtable;

@Entity
@Table(schema = "public", name = "\"add_user\"")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(builderClassName = "Builder", setterPrefix = "with")
public class AddUser extends AbstractBaseEntity{
    @Column(name = "username")
    private String username;

    @Column(name = "dept")
    private String dept;

    @Column(name = "role")
    private String role;

    @Column(name = "maker")
    private String maker;

    @Column(name = "checker")
    private String checker;

    @Column(name = "ip")
    private String ip;

    @Column(name = "ldap_emp_no")
    private String ldapEmployeeNo;

    @Column(name = "ldap_display_name")
    private String ldapDisplayName;

    @Column(name = "ldap_company")
    private String ldapCompany;

    @Column(name = "ldap_dept")
    private String ldapDept;

    @Column(name = "ldap_given_name")
    private String ldapGivenName;

    @Column(name = "ldap_sn")
    private String ldapSn;

    @Column(name = "ldap_mail")
    private String ldapMail;

    @Column(name = "ldap_mobile")
    private String ldapMobile;

    @Column(name = "ldap_telephone_no")
    private String ldapTelephoneNo;

    @Column(name = "code")
    private String code;

    @Column(name = "status")
    private String status;

    @Column(name = "request_id")
    private String requestId;

    public static AddUser from(AdminUserDeptRoles adminUserDeptRoles, String maker, String checker,
                               String ip, Hashtable<String, String> userData, JSONObject response) {
        return AddUser.builder()
                .withUsername(adminUserDeptRoles.getUsername())
                .withDept(adminUserDeptRoles.getDept())
                .withRole(adminUserDeptRoles.getRole())
                .withMaker(maker)
                .withChecker(checker)
                .withIp(ip)
                .withLdapEmployeeNo(userData.get("employeeNumber"))
                .withLdapDisplayName(userData.get("displayName"))
                .withLdapCompany(userData.get("company"))
                .withLdapDept(userData.get("department"))
                .withLdapGivenName(userData.get("givenName"))
                .withLdapSn(userData.get("sn"))
                .withLdapMail(userData.get("mail"))
                .withLdapMobile(userData.get("mobile"))
                .withLdapTelephoneNo(userData.get("telephoneNumber"))
                .withCode(response.getString("infoID"))
                .withStatus(response.getString("infoMsg"))
                .withRequestId(response.getString("request_id"))
                .build();
    }
}
