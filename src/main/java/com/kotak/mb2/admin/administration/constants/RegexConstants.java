package com.kotak.mb2.admin.administration.constants;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class RegexConstants {
    public static final String ALPHA_NUMERIC_REGEX="^[a-zA-Z0-9._-]+([\s][a-zA-Z0-9._-]+)*$";
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9.]*$";
    public static final String SESSION_ROLE = "^(?:admin|maker|checker)$";
}
