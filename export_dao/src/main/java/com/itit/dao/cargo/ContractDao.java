package com.itit.dao.cargo;

import com.itit.domain.cargo.Contract;
import com.itit.domain.cargo.ContractExample;
import vo.ContractProductVo;

import java.util.List;

public interface ContractDao {

	//删除
    int deleteByPrimaryKey(String id);

	//保存
    int insertSelective(Contract record);

	//条件查询
    List<Contract> selectByExample(ContractExample example);

	//id查询
    Contract selectByPrimaryKey(String id);

	//更新
    int updateByPrimaryKeySelective(Contract record);

    //出货表导出
    List<ContractProductVo> findByShipTime(String inputDate,String companyId);
}