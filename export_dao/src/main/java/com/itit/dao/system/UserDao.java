package com.itit.dao.system;

import com.itit.domain.system.User;

import java.util.List;


public interface UserDao {

	//根据企业id查询全部
	List<User> findAll(String companyId);

	//根据id查询
    User findById(String userId);

	//根据id删除
	void delete(String userId);

	//保存
	void save(User user);

	//更新
	void update(User user);

	//查询 用户主键 是否被引用
	Long findUserRole(String  userId);

	/**
	 * 根据邮箱查询用户
	 * @param email
	 * @return
	 */
    User findUserByEmail(String email);
}