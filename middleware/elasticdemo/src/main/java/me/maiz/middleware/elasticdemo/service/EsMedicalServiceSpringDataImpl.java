package me.maiz.middleware.elasticdemo.service;

import lombok.RequiredArgsConstructor;
import me.maiz.middleware.elasticdemo.entity.MedicalRecordDoc;
import me.maiz.middleware.elasticdemo.repository.MedicalRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("springDataService")
@RequiredArgsConstructor
public class EsMedicalServiceSpringDataImpl implements EsMedicalService{
    private final MedicalRecordRepository repository;

    // 新增/修改
    public void save(MedicalRecordDoc doc){
        // 如果没有设置 inDate，则自动设置为当前时间
//        if (doc.getInDate() == null) {
//            doc.setInDate(LocalDateTime.now());
//        }
        repository.save(doc);
    }

    // 根据ID查询
    public Optional<MedicalRecordDoc> getById(String id){
        return repository.findById(id);
    }

    // 删除
    public void delete(String id){
        repository.deleteById(id);
    }

    // 分页查询
    public Page<MedicalRecordDoc> page(int pageNum, int pageSize){
        PageRequest pageRequest = PageRequest.of(
                pageNum - 1,
                pageSize
        );
        return repository.findAll(pageRequest);
    }

    @Override
    public List<MedicalRecordDoc> searchByCondition(String keyword) throws Exception {
        return repository.findByDiagnosisContaining(keyword);
    }

    @Override
    public void clearIndex() throws Exception {
        repository.deleteAll();
    }

    @Override
    public void deleteIndex() throws Exception {
        repository.deleteAll();
        // 注意：Spring Data Elasticsearch 不直接支持删除索引，这里只是清空数据
        // 如果需要完全删除索引，建议使用 RestClient 方式
    }

    // 条件查询
    public List<MedicalRecordDoc> findByDept(String deptName){
        return repository.findByDeptName(deptName);
    }
}
