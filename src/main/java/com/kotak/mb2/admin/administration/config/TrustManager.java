package com.kotak.mb2.admin.administration.config;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class TrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] xcs, String string) {
        // do nothing
    }

    public void checkServerTrusted(X509Certificate[] xcs, String string) {
        // do nothing
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
