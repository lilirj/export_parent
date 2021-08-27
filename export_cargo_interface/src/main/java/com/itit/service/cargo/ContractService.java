package com.itit.service.cargo;

import com.github.pagehelper.PageInfo;
import com.itit.domain.cargo.Contract;
import com.itit.domain.cargo.ContractExample;
import vo.ContractProductVo;

import java.util.List;

/**
 * 购销合同模块
 */
public interface ContractService {

    /**
     * 分页查询
     * @param contractExample 分页查询的参数
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return
     */
    PageInfo<Contract> findByPage(ContractExample contractExample, int pageNum, int pageSize);

    /**
     * 查询所有
     */
    List<Contract> findAll(ContractExample contractExample);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    Contract findById(String id);

    /**
     * 新增
     */
    void save(Contract contract);

    /**
     * 修改
     */
    void update(Contract contract);

    /**
     * 删除部门
     */
    void delete(String id);

    /**
     * 导出 出货表
     */
    List<ContractProductVo> findByShipTime(String inputDate,String companyId);
}











