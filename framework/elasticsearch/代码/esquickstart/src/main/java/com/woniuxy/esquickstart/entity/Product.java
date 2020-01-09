package com.woniuxy.esquickstart.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "emall",type = "product")
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private int productId;

    private String name;

    private String info;

    private String description;


}
