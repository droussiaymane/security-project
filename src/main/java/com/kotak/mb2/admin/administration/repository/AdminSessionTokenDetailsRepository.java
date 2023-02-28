package com.kotak.mb2.admin.administration.repository;

import com.kotak.mb2.admin.administration.domain.entity.AdminSessionTokenDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminSessionTokenDetailsRepository extends JpaRepository<AdminSessionTokenDetails, String> {

    @Query(value = "SELECT jwtToken FROM AdminSessionTokenDetails WHERE username =?1 ")
    String fetchJwtTokenByUsername(String username);

}
