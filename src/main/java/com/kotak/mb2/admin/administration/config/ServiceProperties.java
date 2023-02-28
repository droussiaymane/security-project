package com.kotak.mb2.admin.administration.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
@Getter
public class ServiceProperties {

    @Value("${ldap.host}")
    private String ldapHost;

    @Value("${ldap.factories.init-ctx}")
    private String ldapFactoriesInitCtx;

    @Value("${ldap.search-base}")
    private String ldapSearchBase;

    @Value("${ldap.binding-user}")
    private String ldapBindingUser;

    @Value("${ldap.binding-password}")
    private String ldapBindingPassword;

    @Value("${msg.pending.approval}")
    private String msgPendingApproval;

    @Value("${msg.sent.approval}")
    private String msgSentApproval;
}
