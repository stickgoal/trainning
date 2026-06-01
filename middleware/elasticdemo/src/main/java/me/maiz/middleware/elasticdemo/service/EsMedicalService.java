package me.maiz.middleware.elasticdemo.service;

import me.maiz.middleware.elasticdemo.entity.MedicalRecordDoc;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface EsMedicalService {
    // 1. 新增文档
    void save(MedicalRecordDoc record) throws Exception;

    // 2. 根据 ID 查询
    Optional<MedicalRecordDoc> getById(String id) throws Exception;

    // 3. 分页查询
    Page<MedicalRecordDoc> page(int page, int size) throws Exception;

    // 4. Bool 查询：心内科 + 高血压 + 年龄 >=40
    List<MedicalRecordDoc> searchByCondition() throws Exception;

    // 5. 清空索引数据
    void clearIndex() throws Exception;

    // 6. 删除索引
    void deleteIndex() throws Exception;

    // 7. 根据 ID 删除
    void delete(String id) throws Exception;
}
