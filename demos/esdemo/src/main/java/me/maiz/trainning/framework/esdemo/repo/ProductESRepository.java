package me.maiz.trainning.framework.esdemo.repo;

import me.maiz.trainning.framework.esdemo.model.ESProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductESRepository extends ElasticsearchRepository<ESProduct,String> {

    Page<ESProduct> findByNameOrDescOrShortDesc(String name, String desc, String shortDesc, Pageable pageable);

    @Query("{ \"bool\" : { \"should\" : [{ \"match\" : { \"desc\" : \"?0\" }},{ \"match\" : { \"name\" : \"?0\" }},{\"match\":{\"shortDesc\":\"国\"}}]}}")
    Page<ESProduct> search(String keyword,Pageable pageable);

}
