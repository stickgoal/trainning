package com.woniuxy.esquickstart.dao;

import com.woniuxy.esquickstart.entity.Product;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 *
 */
public interface ProductESRepo extends ElasticsearchRepository<Product,Integer> {

    List<Product> findByNameOrInfoOrDescription(String name,String info,String description);

    @Query("{\"bool\":{\"should\": [{\"match\":{\"name\":\"?0\"}},{\"match\":{\"info\":\"?0\"}},{\"match\":{\"description\":\"?0\"}}]}}")
    List<Product> search(String kw);

}
