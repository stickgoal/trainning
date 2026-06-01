package me.maiz.middleware.elasticdemo.repository;

import me.maiz.middleware.elasticdemo.entity.MedicalRecordDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface MedicalRecordRepository extends ElasticsearchRepository<MedicalRecordDoc,String> {
    // 根据科室精准查询
    List<MedicalRecordDoc> findByDeptName(String deptName);

    // 诊断模糊匹配
    List<MedicalRecordDoc> findByDiagnosisContaining(String keyword);

    // 分页
    Page<MedicalRecordDoc> findAll(Pageable pageable);
}