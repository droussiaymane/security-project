package com.kotak.mb2.admin.administration.repository;

import com.kotak.mb2.admin.administration.domain.entity.AdminUserDeptRoles;
import com.kotak.mb2.admin.administration.projection.AdminUserDeptRolesProjection;
import com.kotak.mb2.admin.administration.projection.AdminUserMakerCheckerProjection;
import com.kotak.mb2.admin.administration.projection.EditDepartmentUserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserDeptRolesRepository extends JpaRepository<AdminUserDeptRoles, String> {

    Long countByUsernameContainingIgnoreCase(String username);

    List<AdminUserDeptRoles> findByDept(String dept);

    @Query(value = "SELECT AUDR.username AS username, AUDR.role AS role, AUDR.status AS status, "
            + "(SELECT 1 FROM AdminUserMakerChecker AUMC WHERE AUMC.username = AUDR.username AND AUMC.checkerDate "
            + "IS NULL AND AUMC.deletedBy IS NULL) AS isUpdated FROM AdminUserDeptRoles AUDR INNER JOIN "
            + "AdminDepartments AD ON AUDR.dept = AD.name WHERE AD.id = ?1 ORDER BY username")
    List<AdminUserDeptRolesProjection> getDeptUsers(String deptId);

    Optional<AdminUserDeptRoles> findByUsername(String username);

    void deleteByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE AdminUserDeptRoles AUDR SET AUDR.status = 'ACT' WHERE AUDR.dept = ?1 AND AUDR.status='INA' "
            + "AND AUDR.lastModifiedAt >= ?2")
    void updateAdminUserDeptRolesAfterAdminDeptUpdate(String deptName, OffsetDateTime modifiedAt);

    @Modifying
    @Transactional
    @Query("UPDATE AdminUserDeptRoles AUDR SET AUDR.status = 'INA' WHERE AUDR.dept = ?1 AND AUDR.status='ACT'")
    void updateStatusAfterAdminDeptDelete(String deptName);

    @Query("SELECT AUDR.id AS id,AUDR.dept AS dept,(SELECT AD.status FROM AdminDepartments AD WHERE AD.name = dept) "
            + "AS currDeptStatus, "
            + "(SELECT AD.status FROM AdminDepartments AD WHERE AD.name = ?1) AS reqDeptStatus,AUDR.role AS role, "
            + "AUDR.empCode AS empCode, "
            + "AUDR.displayName AS displayName,AUDR.company AS company, AUDR.adDept AS adDept, "
            + "AUDR.givenName AS givenName,AUDR.sn AS sn, AUDR.mail AS mail, AUDR.mobile AS mobile, "
            + "AUDR.telephoneNo AS  telephoneNo "
            + "FROM AdminUserDeptRoles AUDR WHERE AUDR.username = ?2")
    EditDepartmentUserProjection editDepartmentUser(String dept, String username);

    @Query(value = "SELECT AUDR.id AS id, AUDR.username AS username, AUDR.role AS role, S.description AS status, "
            + "AUDR.dept AS deptName, AUDR.createdAt AS createdAt, AUDR.empCode as empCode, "
            + "(SELECT count(*) FROM AdminUserMakerChecker AUMC WHERE AUMC.username = AUDR.username AND "
            + "AUMC.checkerDate "
            + "IS NULL AND AUMC.deletedBy IS NULL) AS isUpdated FROM AdminUserDeptRoles AUDR INNER JOIN Status S ON "
            + "AUDR.status = S.code ORDER BY deptName, username")
    List<AdminUserMakerCheckerProjection> getUsers();
}
