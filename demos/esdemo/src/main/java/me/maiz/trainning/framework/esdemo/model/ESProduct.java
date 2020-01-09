package me.maiz.trainning.framework.esdemo.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "emall",type = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("product")
public class ESProduct {

    @Id
    @TableId
    private String productId;

    @NonNull
    private String name;

    @NonNull
    private String shortDesc;

    @NonNull
    @TableField(value = "description")
    private String desc;


}
