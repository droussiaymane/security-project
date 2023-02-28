package com.kotak.mb2.admin.administration.repository;

import com.kotak.mb2.admin.administration.domain.entity.AdminDepartments;
import com.kotak.mb2.admin.administration.projection.GetDepartmentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminDepartmentsRepository extends JpaRepository<AdminDepartments, String> {

    @Query(value = "SELECT AD.id AS id, AD.name as deptName, AD.description AS deptDesc, AD.createdAt AS createdAt, "
            + "S.description AS statusDesc, COUNT(AUDR.username) AS userCount, (SELECT 1 FROM "
            + "AdminDepartmentMakerChecker ADMC WHERE ADMC.name = AD.name AND ADMC.checkerDate IS NULL AND "
            + "ADMC.deletedBy IS NULL) AS isUpdated FROM AdminDepartments AD LEFT JOIN AdminUserDeptRoles AUDR "
            + "ON AD.name = AUDR.dept INNER JOIN Status S ON AD.status = S.code WHERE AD.status IS NOT NULL "
            + "GROUP BY AD.id, AD.name, AD.description, AD.createdAt, S.description ORDER BY AD.name")
    List<GetDepartmentProjection> getDepartments();

    Long countByName(String name);

    Optional<AdminDepartments> findById(String deptId);

    Optional<AdminDepartments> findByName(String name);
}
