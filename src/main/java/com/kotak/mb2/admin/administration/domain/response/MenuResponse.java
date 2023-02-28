package com.kotak.mb2.admin.administration.domain.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class MenuResponse {
    private String menuName;
    private String menuAction;
}
