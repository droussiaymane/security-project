package com.kotak.mb2.admin.administration.repository;

import com.kotak.mb2.admin.administration.domain.entity.AdminMenuProfile;
import com.kotak.mb2.admin.administration.projection.AdminMenuProfileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMenuProfileRepository extends JpaRepository<AdminMenuProfile, String> {

    @Query(value = "SELECT AMP.id AS id, AMP.menuName AS menuName,AMP.status as status, AMP.parentMenuName AS parentMenuName, "
            + "AMP.description AS description, ADMA.dept AS dept FROM AdminMenuProfile AMP LEFT JOIN "
            + "AdminDepartmentMenuAccess ADMA ON ADMA.menuCode = AMP.menuName ORDER BY parentMenuName, menuName")
    List<AdminMenuProfileProjection> getDeptMenuAccessList();
}
