package me.maiz.demo.moderntech.elasticsearch.repo;

import me.maiz.demo.moderntech.elasticsearch.model.ESProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductESRepository extends ElasticsearchRepository<ESProduct,String> {

    Page<ESProduct> findByNameContainingOrDescContainingOrShortDescContaining(String name,String desc,String shortDesc, Pageable pageable);


}
