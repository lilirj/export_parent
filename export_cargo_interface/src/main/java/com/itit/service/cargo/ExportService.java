package com.itit.service.cargo;

import com.itit.domain.cargo.Export;
import com.itit.domain.cargo.ExportExample;
import com.github.pagehelper.PageInfo;
import vo.ExportResult;
import vo.ExportVo;


public interface ExportService {

    Export findById(String id);

    void save(Export export);

    void update(Export export);

    void delete(String id);

	PageInfo<Export> findByPage(ExportExample example, int pageNum, int pageSize);

	void updateExport(ExportResult exportResult);
}
