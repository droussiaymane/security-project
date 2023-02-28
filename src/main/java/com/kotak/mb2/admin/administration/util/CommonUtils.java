package com.kotak.mb2.admin.administration.util;

public interface CommonUtils {

    static boolean isStringEmptyOrNull(String str) {
        return str == null || str.trim().isEmpty();
    }
}
