package me.maiz.middleware.elasticdemo.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import me.maiz.middleware.elasticdemo.entity.MedicalRecordDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("restClientService")
@RequiredArgsConstructor
public class EsMedicalServiceRestClientImpl implements EsMedicalService {

    private final ElasticsearchClient client;
    private static final String INDEX = "medical_records_java";

    // 1. 新增文档
    @Override
    public void save(MedicalRecordDoc record) throws Exception {

        IndexRequest<MedicalRecordDoc> request = IndexRequest.of(i -> i
                .index(INDEX)
                .id(record.getRecordId())
                .document(record)
        );
        client.index(request);
    }

    // 2. 根据 ID 查询
    @Override
    public Optional<MedicalRecordDoc> getById(String id) throws Exception {
        GetRequest request = GetRequest.of(g -> g
                .index(INDEX)
                .id(id)
        );
        GetResponse<MedicalRecordDoc> response = client.get(request, MedicalRecordDoc.class);
        return Optional.ofNullable(response.source()); // Changed from return response.source();
    }

    // 3. 分页查询
    @Override
    public Page<MedicalRecordDoc> page(int page, int size) throws Exception {
        SearchRequest request = SearchRequest.of(s -> s
                .index(INDEX)
                .from((page - 1) * size)
                .size(size)
                .sort(sort -> sort.field(f -> f.field("inDate").order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)))
        );

        SearchResponse<MedicalRecordDoc> response = client.search(request, MedicalRecordDoc.class);
        List<MedicalRecordDoc> resultList = response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
        return new PageImpl<>(resultList, org.springframework.data.domain.PageRequest.of(page, size), response.hits().total().value());
    }

    // 4. Bool 查询：心内科 + 高血压 + 年龄 >=40
    @Override
    public List<MedicalRecordDoc> searchByCondition(String keyword) throws Exception {
        BoolQuery boolQuery = BoolQuery.of(b -> b
                .must(m -> m.match(t -> t.field("diagnosis").query(keyword)))
                .filter(f -> f.term(t -> t.field("deptName").value("心内科")))
                .filter(f -> f.range(r -> r.number(n -> n.field("age").gte(40.0))))
        );

        SearchRequest request = SearchRequest.of(s -> s
                .index(INDEX)
                .query(q -> q.bool(boolQuery))
        );

        SearchResponse<MedicalRecordDoc> response = client.search(request, MedicalRecordDoc.class);
        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    // 5. 清空索引数据
    @Override
    public void clearIndex() throws Exception {
        DeleteByQueryRequest request = DeleteByQueryRequest.of(d -> d
                .index(INDEX)
                .query(q -> q.matchAll(m -> m))
        );
        client.deleteByQuery(request);
    }

    // 6. 删除索引
    @Override
    public void deleteIndex() throws Exception {
        client.indices().delete(d -> d.index(INDEX));
    }

    // 7. 根据 ID 删除
    @Override
    public void delete(String id) throws Exception {
        DeleteRequest request = DeleteRequest.of(d -> d
                .index(INDEX)
                .id(id)
        );
        client.delete(request);
    }

}
