package com.itit.service.cargo;


import com.itit.domain.cargo.ExportProduct;
import com.itit.domain.cargo.ExportProductExample;
import com.github.pagehelper.PageInfo;

import java.util.List;


public interface ExportProductService {

	ExportProduct findById(String id);

	void save(ExportProduct exportProduct);

	void update(ExportProduct exportProduct);

	void delete(String id);

	PageInfo<ExportProduct> findByPage(ExportProductExample exportProductExample, int pageNum, int pageSize);

	List<ExportProduct> findByExportId(ExportProductExample exportProductExample);
}
