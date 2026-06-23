package me.maiz.middleware.elasticdemo.controller;

import lombok.RequiredArgsConstructor;
import me.maiz.middleware.elasticdemo.entity.MedicalRecordDoc;
import me.maiz.middleware.elasticdemo.repository.MedicalRecordRepository;
import me.maiz.middleware.elasticdemo.service.EsMedicalService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/es")
@RequiredArgsConstructor
public class EsMedicalController {

    private final MedicalRecordRepository medicalRecordRepository;

    @Qualifier("springDataService")
    private final EsMedicalService springDataService;

    @Qualifier("restClientService")
    private final EsMedicalService restClientService;

    // ==================== Spring Data 方式 ====================

    @PostMapping("/springdata/save")
    public ResponseEntity<Map<String, Object>> saveBySpringData(@RequestBody MedicalRecordDoc doc) {
        Map<String, Object> result = new HashMap<>();
        try {
            springDataService.save(doc);
            result.put("success", true);
            result.put("message", "保存成功（Spring Data）");
            result.put("data", doc);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("保存失败（Spring Data）", e);
            result.put("success", false);
            result.put("message", "保存失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/springdata/get/{id}")
    public ResponseEntity<Map<String, Object>> getByIdSpringData(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Optional<MedicalRecordDoc> doc = springDataService.getById(id);
            result.put("success", true);
            result.put("method", "Spring Data");
            result.put("data", doc.orElse(null));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询失败（Spring Data），ID: {}", id, e);
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/springdata/page")
    public ResponseEntity<Map<String, Object>> pageBySpringData(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            Page<MedicalRecordDoc> pageResult = springDataService.page(page, size);
            result.put("success", true);
            result.put("method", "Spring Data");
            result.put("totalElements", pageResult.getTotalElements());
            result.put("totalPages", pageResult.getTotalPages());
            result.put("currentPage", pageResult.getNumber() + 1);
            result.put("pageSize", pageResult.getSize());
            result.put("data", pageResult.getContent());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("分页查询失败（Spring Data），page: {}, size: {}", page, size, e);
            result.put("success", false);
            result.put("message", "分页查询失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/springdata/search")
    public ResponseEntity<Map<String, Object>> searchBySpringData(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<MedicalRecordDoc> docs = springDataService.searchByCondition(keyword);
            result.put("success", true);
            result.put("method", "Spring Data");
            result.put("count", docs.size());
            result.put("data", docs);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("条件查询失败（Spring Data）", e);
            result.put("success", false);
            result.put("message", "条件查询失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @DeleteMapping("/springdata/clear")
    public ResponseEntity<Map<String, Object>> clearBySpringData() {
        Map<String, Object> result = new HashMap<>();
        try {
            springDataService.clearIndex();
            result.put("success", true);
            result.put("message", "清空索引成功（Spring Data）");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("清空索引失败（Spring Data）", e);
            result.put("success", false);
            result.put("message", "清空索引失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @DeleteMapping("/springdata/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteBySpringData(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            springDataService.delete(id);
            result.put("success", true);
            result.put("message", "删除成功（Spring Data）");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("删除失败（Spring Data），ID: {}", id, e);
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @PostMapping("/springdata/batch-insert")
    public ResponseEntity<Map<String, Object>> batchInsertSpringData(@RequestParam(defaultValue = "100") int count) {
        Map<String, Object> result = new HashMap<>();
        try {
            long startTime = System.currentTimeMillis();
            List<String> patientNames = Arrays.asList("张三", "李四", "王五", "赵六", "孙七", "周八", "吴九", "郑十");
            List<String> departments = Arrays.asList("心内科", "呼吸科", "消化科", "神经内科", "骨科");
            List<String> diagnoses = Arrays.asList("高血压", "糖尿病", "冠心病", "慢性支气管炎", "胃炎", "脑梗塞", "关节炎");
            List<String> genders = Arrays.asList("男", "女");

            for (int i = 0; i < count; i++) {
                MedicalRecordDoc doc = new MedicalRecordDoc();
                doc.setRecordId(UUID.randomUUID().toString());
                doc.setPatientName(patientNames.get(i % patientNames.size()) + i);
                doc.setDeptName(departments.get(i % departments.size()));
                doc.setDiagnosis(diagnoses.get(i % diagnoses.size()));
                doc.setAge(20 + (i % 60));
                doc.setGender(genders.get(i % 2));
                doc.setInDate(LocalDate.now().minusDays(i));
                springDataService.save(doc);
            }

            long endTime = System.currentTimeMillis();
            result.put("success", true);
            result.put("message", "批量插入成功（Spring Data）");
            result.put("count", count);
            result.put("timeMs", endTime - startTime);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("批量插入失败（Spring Data）", e);
            result.put("success", false);
            result.put("message", "批量插入失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    // ==================== RestClient 方式 ====================

    @PostMapping("/restclient/save")
    public ResponseEntity<Map<String, Object>> saveByRestClient(@RequestBody MedicalRecordDoc doc) {
        Map<String, Object> result = new HashMap<>();
        try {
            restClientService.save(doc);
            result.put("success", true);
            result.put("message", "保存成功（RestClient）");
            result.put("data", doc);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("保存失败（RestClient）", e);
            result.put("success", false);
            result.put("message", "保存失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/restclient/get/{id}")
    public ResponseEntity<Map<String, Object>> getByIdRestClient(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Optional<MedicalRecordDoc> doc = restClientService.getById(id);
            result.put("success", true);
            result.put("method", "RestClient");
            result.put("data", doc.orElse(null));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询失败（RestClient），ID: {}", id, e);
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/restclient/page")
    public ResponseEntity<Map<String, Object>> pageByRestClient(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            Page<MedicalRecordDoc> pageResult = restClientService.page(page, size);
            result.put("success", true);
            result.put("method", "RestClient");
            result.put("totalElements", pageResult.getTotalElements());
            result.put("totalPages", pageResult.getTotalPages());
            result.put("currentPage", pageResult.getNumber() + 1);
            result.put("pageSize", pageResult.getSize());
            result.put("data", pageResult.getContent());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("分页查询失败（RestClient），page: {}, size: {}", page, size, e);
            result.put("success", false);
            result.put("message", "分页查询失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/restclient/search")
    public ResponseEntity<Map<String, Object>> searchByRestClient(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<MedicalRecordDoc> docs = restClientService.searchByCondition(keyword);
            result.put("success", true);
            result.put("method", "RestClient");
            result.put("count", docs.size());
            result.put("data", docs);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("条件查询失败（RestClient）", e);
            result.put("success", false);
            result.put("message", "条件查询失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @DeleteMapping("/restclient/clear")
    public ResponseEntity<Map<String, Object>> clearByRestClient() {
        Map<String, Object> result = new HashMap<>();
        try {
            restClientService.clearIndex();
            result.put("success", true);
            result.put("message", "清空索引成功（RestClient）");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("清空索引失败（RestClient）", e);
            result.put("success", false);
            result.put("message", "清空索引失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @DeleteMapping("/restclient/delete-index")
    public ResponseEntity<Map<String, Object>> deleteIndexByRestClient() {
        Map<String, Object> result = new HashMap<>();
        try {
            restClientService.deleteIndex();
            result.put("success", true);
            result.put("message", "删除索引成功（RestClient）");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("删除索引失败（RestClient）", e);
            result.put("success", false);
            result.put("message", "删除索引失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @PostMapping("/restclient/batch-insert")
    public ResponseEntity<Map<String, Object>> batchInsertRestClient(@RequestParam(defaultValue = "100") int count) {
        Map<String, Object> result = new HashMap<>();
        try {
            long startTime = System.currentTimeMillis();
            List<String> patientNames = Arrays.asList("张三", "李四", "王五", "赵六", "孙七", "周八", "吴九", "郑十");
            List<String> departments = Arrays.asList("心内科", "呼吸科", "消化科", "神经内科", "骨科");
            List<String> diagnoses = Arrays.asList("高血压", "糖尿病", "冠心病", "慢性支气管炎", "胃炎", "脑梗塞", "关节炎");
            List<String> genders = Arrays.asList("男", "女");

            for (int i = 0; i < count; i++) {
                MedicalRecordDoc doc = new MedicalRecordDoc();
                doc.setRecordId(UUID.randomUUID().toString());
                doc.setPatientName(patientNames.get(i % patientNames.size()) + i);
                doc.setDeptName(departments.get(i % departments.size()));
                doc.setDiagnosis(diagnoses.get(i % diagnoses.size()));
                doc.setAge(20 + (i % 60));
                doc.setGender(genders.get(i % 2));
                restClientService.save(doc);
            }

            long endTime = System.currentTimeMillis();
            result.put("success", true);
            result.put("message", "批量插入成功（RestClient）");
            result.put("count", count);
            result.put("timeMs", endTime - startTime);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("批量插入失败（RestClient）", e);
            result.put("success", false);
            result.put("message", "批量插入失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    // ==================== 对比测试 ====================

    @PostMapping("/compare/save")
    public ResponseEntity<Map<String, Object>> compareSave(@RequestBody MedicalRecordDoc doc) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> springDataResult = new HashMap<>();
        Map<String, Object> restClientResult = new HashMap<>();

        try {
            // Spring Data 方式
            long start1 = System.currentTimeMillis();
            springDataService.save(doc);
            long end1 = System.currentTimeMillis();
            springDataResult.put("success", true);
            springDataResult.put("timeMs", end1 - start1);
        } catch (Exception e) {
            log.error("对比保存 - Spring Data 方式失败", e);
            springDataResult.put("success", false);
            springDataResult.put("error", e.getMessage());
        }

        try {
            // RestClient 方式
            long start2 = System.currentTimeMillis();
            restClientService.save(doc);
            long end2 = System.currentTimeMillis();
            restClientResult.put("success", true);
            restClientResult.put("timeMs", end2 - start2);
        } catch (Exception e) {
            log.error("对比保存 - RestClient 方式失败", e);
            restClientResult.put("success", false);
            restClientResult.put("error", e.getMessage());
        }

        result.put("springData", springDataResult);
        result.put("restClient", restClientResult);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/compare/search")
    public ResponseEntity<Map<String, Object>> compareSearch(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> springDataResult = new HashMap<>();
        Map<String, Object> restClientResult = new HashMap<>();

        try {
            // Spring Data 方式
            long start1 = System.currentTimeMillis();
            List<MedicalRecordDoc> docs1 = springDataService.searchByCondition(keyword);
            long end1 = System.currentTimeMillis();
            springDataResult.put("success", true);
            springDataResult.put("count", docs1.size());
            springDataResult.put("timeMs", end1 - start1);
        } catch (Exception e) {
            log.error("对比搜索 - Spring Data 方式失败", e);
            springDataResult.put("success", false);
            springDataResult.put("error", e.getMessage());
        }

        try {
            // RestClient 方式
            long start2 = System.currentTimeMillis();
            List<MedicalRecordDoc> docs2 = restClientService.searchByCondition(keyword);
            long end2 = System.currentTimeMillis();
            restClientResult.put("success", true);
            restClientResult.put("count", docs2.size());
            restClientResult.put("timeMs", end2 - start2);
        } catch (Exception e) {
            log.error("对比搜索 - RestClient 方式失败", e);
            restClientResult.put("success", false);
            restClientResult.put("error", e.getMessage());
        }

        result.put("springData", springDataResult);
        result.put("restClient", restClientResult);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/compare/batch-insert")
    public ResponseEntity<Map<String, Object>> compareBatchInsert(@RequestParam(defaultValue = "100") int count) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> springDataResult = new HashMap<>();
        Map<String, Object> restClientResult = new HashMap<>();

        try {
            // Spring Data 方式
            long start1 = System.currentTimeMillis();
            List<String> patientNames = Arrays.asList("张三", "李四", "王五", "赵六", "孙七", "周八", "吴九", "郑十");
            List<String> departments = Arrays.asList("心内科", "呼吸科", "消化科", "神经内科", "骨科");
            List<String> diagnoses = Arrays.asList("高血压", "糖尿病", "冠心病", "慢性支气管炎", "胃炎", "脑梗塞", "关节炎");
            List<String> genders = Arrays.asList("男", "女");

            for (int i = 0; i < count; i++) {
                MedicalRecordDoc doc = new MedicalRecordDoc();
                doc.setRecordId(UUID.randomUUID().toString());
                doc.setPatientName(patientNames.get(i % patientNames.size()) + i);
                doc.setDeptName(departments.get(i % departments.size()));
                doc.setDiagnosis(diagnoses.get(i % diagnoses.size()));
                doc.setAge(20 + (i % 60));
                doc.setGender(genders.get(i % 2));
                springDataService.save(doc);
            }
            long end1 = System.currentTimeMillis();
            springDataResult.put("success", true);
            springDataResult.put("count", count);
            springDataResult.put("timeMs", end1 - start1);
        } catch (Exception e) {
            log.error("对比批量插入 - Spring Data 方式失败", e);
            springDataResult.put("success", false);
            springDataResult.put("error", e.getMessage());
        }

        try {
            // RestClient 方式
            long start2 = System.currentTimeMillis();
            List<String> patientNames = Arrays.asList("张三", "李四", "王五", "赵六", "孙七", "周八", "吴九", "郑十");
            List<String> departments = Arrays.asList("心内科", "呼吸科", "消化科", "神经内科", "骨科");
            List<String> diagnoses = Arrays.asList("高血压", "糖尿病", "冠心病", "慢性支气管炎", "胃炎", "脑梗塞", "关节炎");
            List<String> genders = Arrays.asList("男", "女");

            for (int i = 0; i < count; i++) {
                MedicalRecordDoc doc = new MedicalRecordDoc();
                doc.setRecordId(UUID.randomUUID().toString());
                doc.setPatientName(patientNames.get(i % patientNames.size()) + i);
                doc.setDeptName(departments.get(i % departments.size()));
                doc.setDiagnosis(diagnoses.get(i % diagnoses.size()));
                doc.setAge(20 + (i % 60));
                doc.setGender(genders.get(i % 2));
                restClientService.save(doc);
            }
            long end2 = System.currentTimeMillis();
            restClientResult.put("success", true);
            restClientResult.put("count", count);
            restClientResult.put("timeMs", end2 - start2);
        } catch (Exception e) {
            log.error("对比批量插入 - RestClient 方式失败", e);
            restClientResult.put("success", false);
            restClientResult.put("error", e.getMessage());
        }

        result.put("springData", springDataResult);
        result.put("restClient", restClientResult);
        return ResponseEntity.ok(result);
    }

    // ==================== @Query DSL 查询测试 ====================

    /**
     * 测试1: 根据患者姓名模糊查询
     */
    @GetMapping("/query/search-by-patient")
    public ResponseEntity<Map<String, Object>> searchByPatientName(@RequestParam String patientName) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<MedicalRecordDoc> docs = medicalRecordRepository.searchByPatientName(patientName);
            result.put("success", true);
            result.put("method", "@Query - match查询");
            result.put("queryParam", patientName);
            result.put("count", docs.size());
            result.put("data", docs);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("患者姓名查询失败", e);
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 测试2: 根据科室和诊断结果组合查询
     */
    @GetMapping("/query/search-by-dept-diagnosis")
    public ResponseEntity<Map<String, Object>> findByDeptAndDiagnosis(
            @RequestParam String deptName,
            @RequestParam String diagnosis) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<MedicalRecordDoc> docs = medicalRecordRepository.findByDeptAndDiagnosis(deptName, diagnosis);
            result.put("success", true);
            result.put("method", "@Query - bool多字段查询");
            result.put("deptName", deptName);
            result.put("diagnosis", diagnosis);
            result.put("count", docs.size());
            result.put("data", docs);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("科室诊断组合查询失败", e);
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 测试3: 年龄范围查询
     */
    @GetMapping("/query/search-by-age-range")
    public ResponseEntity<Map<String, Object>> findByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<MedicalRecordDoc> docs = medicalRecordRepository.findByAgeRange(minAge, maxAge);
            result.put("success", true);
            result.put("method", "@Query - range范围查询");
            result.put("minAge", minAge);
            result.put("maxAge", maxAge);
            result.put("count", docs.size());
            result.put("data", docs);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("年龄范围查询失败", e);
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 测试4: 复杂布尔查询
     */
    @GetMapping("/query/complex-search")
    public ResponseEntity<Map<String, Object>> complexSearch(
            @RequestParam String gender,
            @RequestParam String keyword,
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<MedicalRecordDoc> docs = medicalRecordRepository.complexSearch(gender, keyword, minAge, maxAge);
            result.put("success", true);
            result.put("method", "@Query - 复杂bool查询");
            result.put("gender", gender);
            result.put("keyword", keyword);
            result.put("minAge", minAge);
            result.put("maxAge", maxAge);
            result.put("count", docs.size());
            result.put("data", docs);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("复杂查询失败", e);
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}
