package com.kotak.mb2.admin.administration.repository;

import com.kotak.mb2.admin.administration.domain.entity.CustomerMakerChecker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerMakerCheckerRepository extends JpaRepository<CustomerMakerChecker, String> {

    long countByMakerAndCheckerNullAndDeletedByNullAndTypeIn(String userName, List<String> type);

    long countByMakerInAndCheckerNullAndDeletedByNullAndTypeIn(List<String> username, List<String> type);

    long countByCheckerNullAndDeletedByNullAndTypeIn(List<String> type);

    @Query(value = "SELECT c FROM CustomerMakerChecker c WHERE (maker = ?1 OR checker = ?1 OR deletedBy = ?1) "
            + " AND ( c.deletedBy IS NOT NULL OR c.checker IS NOT NULL) ORDER BY c.makerDate DESC")
    List<CustomerMakerChecker> completedActivities(String sessionUsername);

}
