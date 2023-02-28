package com.kotak.mb2.admin.administration.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import static com.kotak.mb2.admin.administration.util.CommonUtils.isStringEmptyOrNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class LdapClient {

    private final ConcurrentHashMap<String, String> environment = new ConcurrentHashMap<>();
    private final ServiceProperties serviceProperties;

    @PostConstruct
    public void setData() {
        try {
            final var ldapFactoriesInitCtx = serviceProperties.getLdapFactoriesInitCtx();
            environment.put(Context.INITIAL_CONTEXT_FACTORY, isStringEmptyOrNull(ldapFactoriesInitCtx)
                    ? "com.sun.jndi.ldap.LdapCtxFactory" : ldapFactoriesInitCtx);
            environment.put("com.sun.jndi.ldap.read.timeout", "2000");
            environment.put(Context.PROVIDER_URL, serviceProperties.getLdapHost());

            // Authenticate as S. User and password "mysecret"
            environment.put(Context.SECURITY_AUTHENTICATION, "simple");

            // For suppressing exception.
            environment.put(Context.REFERRAL, "follow");
            log.debug("Env {}", environment);
        } catch (Exception e) {
            log.error("Exception environment {} ", e);
        }
    }

    public Hashtable<String, String> getUserData(String username) {
        Hashtable<String, String> userData = new Hashtable<>();

        final SearchControls searchControls = new SearchControls();
        final String[] attrIDs = {"employeeNumber", "company", "department", "displayName", "givenName", "sn", "mail",
                "mobile", "telephoneNumber", "distinguishedName"};
//        final String[] attrIDs = {"distinguishedName"};

        searchControls.setReturningAttributes(attrIDs);
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        final Hashtable<String, String> hashtable = new Hashtable<>(environment);
        hashtable.put("java.naming.ldap.factory.socket",  NaiveSSLSocketFactory.class.getName());
        hashtable.put(Context.SECURITY_PRINCIPAL, serviceProperties.getLdapBindingUser());
        hashtable.put(Context.SECURITY_CREDENTIALS, serviceProperties.getLdapBindingPassword());
        log.info("hashtable {}", hashtable,username);

        try {
            int i = 0;
            final DirContext dirContext = new InitialDirContext(hashtable);
            final NamingEnumeration<SearchResult> answer = dirContext.search(serviceProperties.getLdapSearchBase(),
                    "(samAccountName=" + username + ")", searchControls);

            userData = new Hashtable<>();
            log.info("answer value {} ",answer.toString());
            if (answer != null) {
                while (answer.hasMoreElements()) {
                    i++;
                    final SearchResult searchResult = (SearchResult) answer.next();
                    final Attributes attrs = searchResult.getAttributes();

                    for (String s : attrIDs) {
                        final Attribute attr = attrs.get(s);
                        log.debug("ldap attributes {}" + attr);

                        if (attr != null) {
                            userData.put(s, attr.get().toString());
                            //break;
                        }
                    }
                    log.info("while increament {} ", i);
                    if (!userData.isEmpty()) {
                        log.info("inside userdata not empty loop");
                        break;
                    }
                }
             }
        } catch (Exception e) {
            log.error("LdapClient", "getUserData", "Exception:-", e);
            e.printStackTrace();
        }

        log.debug("ldap userData:" + userData);
        return userData;
    }
}
