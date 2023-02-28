/*
package com.kotak.mb2.admin.administration.helper;

import com.kotak.mb2.authentication.KmbIdentity;
import com.kotak.mb2.authentication.KmbPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class KmbPrincipalHelper {

    private final KmbPrincipal kmbPrincipal;

    public KmbIdentity getIdentity() {
        Optional<KmbIdentity> kmbIdentityOptional = kmbPrincipal.getKmbIdentity();
        return kmbIdentityOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "identity is null"));
    }
}
*/
