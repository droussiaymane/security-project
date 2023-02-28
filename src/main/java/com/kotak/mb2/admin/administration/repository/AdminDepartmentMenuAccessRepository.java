package com.kotak.mb2.admin.administration.repository;

import com.kotak.mb2.admin.administration.domain.entity.AdminDepartmentMenuAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminDepartmentMenuAccessRepository extends JpaRepository<AdminDepartmentMenuAccess, String> {

    List<AdminDepartmentMenuAccess> findByDept(String deptName);

    void deleteByDeptAndMenuCode(String deptName,String menuCode);
}
