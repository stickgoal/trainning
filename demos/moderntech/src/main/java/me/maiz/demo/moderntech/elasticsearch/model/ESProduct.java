package me.maiz.demo.moderntech.elasticsearch.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "emall",type = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ESProduct {

    @Id
    private String productId;

    @NonNull
    private String name;

    @NonNull
    private String shortDesc;

    @NonNull
    private String desc;


}
