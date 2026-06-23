package me.maiz.middleware.elasticdemo.repository;

import me.maiz.middleware.elasticdemo.entity.MedicalRecordDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface MedicalRecordRepository extends ElasticsearchRepository<MedicalRecordDoc,String> {
    // 根据科室精准查询
    List<MedicalRecordDoc> findByDeptName(String deptName);

    // 诊断模糊匹配
    List<MedicalRecordDoc> findByDiagnosisContaining(String keyword);

    // 分页
    Page<MedicalRecordDoc> findAll(Pageable pageable);

    /**
     * 使用 @Query 注解的 DSL 查询示例
     * 示例1: 根据患者姓名模糊查询（使用 match 查询）
     */
    @Query("{\"match\": {\"patientName\": \"?0\"}}")
    List<MedicalRecordDoc> searchByPatientName(String patientName);

    /**
     * 示例2: 多字段组合查询 - 根据科室和诊断结果查询
     * 使用 bool 查询，must 条件同时满足科室和诊断关键词
     */
    @Query("{\"bool\": {\"must\": [" +
            "{\"term\": {\"deptName\": \"?0\"}}, " +
            "{\"match\": {\"diagnosis\": \"?1\"}}" +
            "]}}")
    List<MedicalRecordDoc> findByDeptAndDiagnosis(String deptName, String diagnosis);

    /**
     * 示例3: 范围查询 - 查询指定年龄范围的病历
     * 使用 range 查询年龄区间
     */
    @Query("{\"range\": {\"age\": {\"gte\": ?0, \"lte\": ?1}}}")
    List<MedicalRecordDoc> findByAgeRange(Integer minAge, Integer maxAge);

    /**
     * 示例4: 复杂布尔查询 - 多条件组合
     * must: 性别匹配
     * should: 患者姓名或诊断结果包含关键词（满足其一即可）
     * filter: 年龄范围过滤（不影响评分）
     */
    @Query("{\"bool\": {" +
            "\"must\": [{\"term\": {\"gender\": \"?0\"}}]," +
            "\"should\": [" +
            "{\"match\": {\"patientName\": \"?1\"}}," +
            "{\"match\": {\"diagnosis\": \"?1\"}}" +
            "]," +
            "\"filter\": [{\"range\": {\"age\": {\"gte\": ?2, \"lte\": ?3}}}]" +
            "}}")
    List<MedicalRecordDoc> complexSearch(String gender, String keyword, Integer minAge, Integer maxAge);
}