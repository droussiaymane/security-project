package com.kotak.mb2.admin.administration.repository;

import com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMakerChecker;
import com.kotak.mb2.admin.administration.projection.FetchPendingActionsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminDeptMakerCheckerRepository extends JpaRepository<AdminDepartmentMakerChecker, String> {

    Long countByNameAndCheckerNullAndDeletedByNull(String name);

    List<AdminDepartmentMakerChecker> findByCheckerDateNullAndDeletedByNullAndMakerIn(List<String> makers);

    List<AdminDepartmentMakerChecker> findByCheckerDateNullAndDeletedByNull();

    Long countByCheckerDateNullAndDeletedByNullAndMakerIn(List<String> makers);

    Long countByCheckerDateNullAndDeletedByNull();

    @Query(value = "SELECT ADMC FROM AdminDepartmentMakerChecker ADMC " +
            "WHERE (ADMC.maker = ?1 OR ADMC.checker = ?1 OR ADMC.deletedBy = ?1) " +
            "AND (ADMC.deletedBy IS NOT NULL OR ADMC.checker IS NOT NULL) ORDER BY ADMC.makerDate DESC")
    List<AdminDepartmentMakerChecker> completedActivities(String username);

    @Modifying
    @Query(value = "UPDATE AdminDepartmentMakerChecker SET deletedBy = ?1, deletedAt=CURRENT_TIMESTAMP, checkerComments = ?2 "
            + "WHERE ID = ?3 AND deletedBy IS NULL AND CHECKER IS NULL")
    void rejectDepartmentRequest(String sessionUserName, String comments, String userId);

    @Modifying
    @Query(value = "UPDATE AdminDepartmentMakerChecker SET status = ?1, checker = ?2, checkerDate = CURRENT_TIMESTAMP, "
            + "checkerComments = ?3 WHERE ID = ?4 AND deletedBy IS NULL AND CHECKER IS NULL")
    void updateAdminDepartmentMakerChecker(String status, String sessionUserName, String checkerComments,
                                           String userId);

    Optional<AdminDepartmentMakerChecker> findFirstByNameAndCheckerNotNullOrderByCreatedAtDesc(String userName);
}
