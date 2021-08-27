package com.itit.dao.system;

import com.itit.domain.system.Module;
import com.itit.domain.system.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RoleDao {

    //根据id查询
    Role findById(String id);

    //查询全部
    List<Role> findAll(String companyId);

	//根据id删除
    void delete(String id);

	//添加
    void save(Role role);

	//更新
    void update(Role role);


    void deleteModuleByRoleid(String roleid);

    void saveRoeidModule(String roleid, String moduleId);

    List<Role> findRoleByUserId(String userId);

    void deleteRoleByUser(String userid);

    void saveUserRole(@Param("userid") String userid, @Param("roleId") String roleId);
}