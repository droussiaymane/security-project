package com.kotak.mb2.admin.administration.repository;

import com.kotak.mb2.admin.administration.domain.entity.AdminDeptMenuMakerChecker;
import com.kotak.mb2.admin.administration.projection.CompletedActivityProjection;
import com.kotak.mb2.admin.administration.projection.FetchPendingActionsProjection;
import com.kotak.mb2.admin.administration.projection.MenuPendingActionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AdminDeptMenuMakerCheckerRepository extends JpaRepository<AdminDeptMenuMakerChecker, String> {

    Long countByDeptAndCheckerNullAndDeletedByNull(String name);
    List<FetchPendingActionsProjection> findByCheckerDateNullAndDeletedByNullAndMakerIn(List<String> makers);
    List<FetchPendingActionsProjection> findByCheckerDateNullAndDeletedByNull();

    @Query(value = "SELECT COUNT(DISTINCT To_char(makerDate, 'DD-MM-YYYY HH24:MI:SS')) FROM AdminDeptMenuMakerChecker admmc "
            + "WHERE checker IS NULL AND deletedBy IS NULL AND maker IN ?1")
    long countByCheckerDateNullAndDeletedByNullAndMakerIn(List<String> sessionUsername);
    @Query(value = "SELECT COUNT(DISTINCT To_char(makerDate, 'DD-MM-YYYY HH24:MI:SS')) FROM AdminDeptMenuMakerChecker admmc "
            + "WHERE checker IS NULL AND deletedBy IS NULL")
    long countByCheckerDateNullAndDeletedByNull();

    @Modifying
    @Query(value = "UPDATE AdminDeptMenuMakerChecker SET deletedBy = ?1, deletedAt=CURRENT_TIMESTAMP, checkerComments = ?2 "
            + "WHERE ID = ?3 AND deletedBy IS NULL AND CHECKER IS NULL")
    void rejectDepartmentMenuRequest(String sessionUserName, String comments, String userId);
    @Modifying
    @Query(value = "UPDATE AdminDeptMenuMakerChecker SET status = ?1, checker = ?2, checkerDate = CURRENT_TIMESTAMP, "
            + "checkerComments = ?3 WHERE ID = ?4 AND deletedBy IS NULL AND CHECKER IS NULL")
    void updateAdminDepartMenumentMakerChecker(String statusRejected, String sessionUserName, String comments, String departmentId);

    /*@Query(value = "SELECT ADMC FROM AdminDeptMenuMakerChecker ADMC " +
            "WHERE (ADMC.maker = ?1 OR ADMC.checker = ?1 OR ADMC.deletedBy = ?1) " +
            "AND (ADMC.deletedBy IS NOT NULL OR ADMC.checker IS NOT NULL) ORDER BY ADMC.makerDate DESC")
    List<AdminDeptMenuMakerChecker> completedActivities(String sessionUsername);*/

   /* @Query(value = "SELECT dept,(SELECT description FROM AdminMenuProfile WHERE menuName = menuCode),maker,makerDate, "
                    + "To_char(makerDate, 'DD-MM-YYYY HH24:MI:SS') , makerComments,Nvl(checker, deletedBy), To_char(Nvl(checkerDate, deletedAt), 'DD-MM-YYYY HH24:MI:SS'), "
                    + "checkerComments,( CASE WHEN makerStatus = 'PEN' THEN 'ADD' ELSE 'DEL' END ) AS makerAction,( CASE WHEN status IS NULL THEN 'WITH' WHEN ( status = 'ACT' OR status = 'DEL' ) THEN 'ACT' ELSE 'REJ' END ) "
                    + "FROM AdminDeptMenuMakerChecker WHERE (maker = ?1 OR checker = ?1 OR deleted_by = ?1) AND ( checker IS NOT NULL OR deletedBy IS NOT NULL ) ORDER BY makerDate DESC")
    List<AdminDeptMenuMakerChecker> completedActivities(String sessionUsername);*/

    @Query(value = "SELECT ADMMC.dept AS dept,(SELECT AMP.description AS description FROM AdminMenuProfile AMP WHERE AMP.menuName = ADMMC.menuCode) AS description, ADMMC.maker AS maker, ADMMC.makerDate AS rawMakerDate, " +
                    "To_char(ADMMC.makerDate, 'DD-MM-YYYY HH24:MI:SS') AS makerDate , ADMMC.makerComments AS makerComments, COALESCE(ADMMC.checker, ADMMC.deletedBy) AS authz, To_char(COALESCE(ADMMC.checkerDate, ADMMC.deletedAt) , 'DD-MM-YYYY HH24:MI:SS') AS authzDate, " +
                    "ADMMC.checkerComments AS checkerComments,( CASE WHEN ADMMC.makerStatus = 'PEN' THEN 'ADD' ELSE 'DEL' END ) AS makerAction,( CASE WHEN ADMMC.status IS NULL THEN 'WITH' WHEN ( status = 'ACT' OR status = 'DEL' ) THEN 'ACT' ELSE 'REJ' END ) AS authzAction " +
                    "FROM AdminDeptMenuMakerChecker ADMMC WHERE (ADMMC.maker = ?1 OR ADMMC.checker = ?1 OR ADMMC.deletedBy = ?1) AND ( ADMMC.checker IS NOT NULL OR ADMMC.deletedBy IS NOT NULL ) ORDER BY ADMMC.makerDate DESC")
    List<CompletedActivityProjection> completedActivities(String sessionUsername);

    @Query(value = "SELECT admmc.dept as dept, admmc.menuCode as menuCode,admmc.description as description, admmc.action as action "
                    +"FROM AdminDeptMenuMakerChecker admmc  WHERE admmc.dept = ?1 and to_char(admmc.makerDate , 'DD-MM-YYYY HH24:MI:SS') = ?2")
    List<MenuPendingActionProjection> getPendingMenuDetails(String deptName, String makerDate);

    @Query(value = "SELECT adma.dept as dept, adma.menuCode as menuCode,amp.description as description,'non-modified' as action FROM AdminDepartmentMenuAccess adma "
            +"JOIN AdminMenuProfile amp on adma.menuCode = amp.menuName WHERE adma.dept = ?1")
    List<MenuPendingActionProjection> getExistingMenuDetails(String deptName);

    @Query(value = "select admmc from AdminDeptMenuMakerChecker admmc where to_char(makerDate, 'DD-MM-YYYY HH24:MI:SS') = ?1")
    List<AdminDeptMenuMakerChecker> findByMakerDate(String makerDate);
}
