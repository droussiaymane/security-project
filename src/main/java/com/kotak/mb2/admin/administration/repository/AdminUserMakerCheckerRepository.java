package com.kotak.mb2.admin.administration.repository;

import com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMakerChecker;
import com.kotak.mb2.admin.administration.domain.entity.AdminUserMakerChecker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserMakerCheckerRepository extends JpaRepository<AdminUserMakerChecker, String> {

    Long countByUsernameAndCheckerNullAndDeletedByNull(String username);

    Long countByDeptAndCheckerNullAndDeletedByNull(String name);

    List<AdminUserMakerChecker> findByCheckerDateNullAndDeletedByNullAndMakerIn(List<String> makers);

    List<AdminUserMakerChecker> findByCheckerDateNullAndDeletedByNull();

    Long countByCheckerDateNullAndDeletedByNullAndMakerIn(List<String> makers);

    Long countByCheckerDateNullAndDeletedByNull();

    @Query(value = "SELECT AUMC FROM AdminUserMakerChecker AUMC " +
            "WHERE (AUMC.maker = ?1 OR AUMC.checker = ?1 OR AUMC.deletedBy = ?1) " +
            "AND (AUMC.deletedBy IS NOT NULL OR AUMC.checker IS NOT NULL) ORDER BY AUMC.makerDate DESC")
    List<AdminUserMakerChecker> completedActivities(String username);

    @Modifying
    @Query(value = "UPDATE AdminUserMakerChecker SET deletedBy = ?1, deletedAt = CURRENT_TIMESTAMP, checkerComments = ?2 "
            + "WHERE ID = ?3 AND deletedBy IS NULL AND CHECKER IS NULL")
    void rejectUserRequest(String sessionUserName, String comments, String userId);

    @Modifying
    @Query(value = "UPDATE AdminUserMakerChecker SET status = ?1, checker = ?2, checkerDate = CURRENT_TIMESTAMP, "
            + "checkerComments = ?3 WHERE ID = ?4 AND deletedBy IS NULL AND CHECKER IS NULL")
    void updateAdminUserMakerChecker(String status, String sessionUserName, String checkerComments, String userId);

    @Query(value = "SELECT count(1) from AdminUserMakerChecker where username=?1 and status='ACT' and " +
            "is_deleted is null and role = ?2 and dept = ?3 ")
    long getAdminUserMakerCheckerRoleDeptDetails(String username, String role, String dept);


    @Transactional
    @Modifying
    @Query(value = "UPDATE AdminUserMakerChecker SET status = 'ACT', action = 'EDIT', empCode = ?1,displayName = ?2,"
            + "company = ?3, adDept = ?4, givenName = ?5, sn = ?6, mail = ?7, mobile = ?8, telephoneNo = ?9 " +
            "WHERE username = ?10 AND status='ACT' AND isDeleted IS NULL")
    void updateModifiedOnboardUser(String empCode, String displayName, String company, String adDept, String givenName,
                                   String sn, String mail, String mobile, String telephoneNo, String username);
    @Transactional
    @Modifying
    @Query(value = "UPDATE AdminUserMakerChecker SET status = 'ACT', action = 'EDIT', role = ?1, dept = ?2, " +
            "checkerDate=CURRENT_TIMESTAMP WHERE username = ?3  AND status='ACT' AND  isDeleted IS NULL")
    void updateModifiedOnboardUserForRoleAndDept(String role, String dept, String username);

    @Transactional
    @Modifying
    @Query(value = "UPDATE AdminUserMakerChecker set isDeleted = '1', action= 'INACTIVE', checkerDate = CURRENT_TIMESTAMP" +
            " WHERE username = ?1 AND status = 'ACT' AND isDeleted IS NULL")
    void revokeOnboardUser(String username);

    @Transactional
    @Modifying
    @Query(value = "update AdminUserMakerChecker set status='INA',lastModifiedAt=CURRENT_TIMESTAMP WHERE username = ?1")
    void revokeOnboardRoleDept(String username);

    @Query(value = "SELECT count(1) FROM AdminUserMakerChecker where username = ?1 and " +
            " status='ACT' and isDeleted is null")
    long getUserCountByUsername(String username);


    Optional<AdminUserMakerChecker> findFirstByUsernameAndCheckerNotNullOrderByCreatedAtDesc(String userId);
}
