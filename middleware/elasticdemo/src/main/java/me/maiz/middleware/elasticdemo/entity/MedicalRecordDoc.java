package me.maiz.middleware.elasticdemo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Document(indexName = "medical_records_java")
public class MedicalRecordDoc {
    /**
     * 病历记录ID，主键
     */
    @Id
    private String recordId;

    /**
     * 患者姓名，使用 ik_max_word 分器进行全文检索
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String patientName;

    /**
     * 科室名称，精确匹配
     */
    @Field(type = FieldType.Keyword)
    private String deptName;

    /**
     * 诊断结果，使用 ik_max_word 分器进行全文检索
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String diagnosis;

    /**
     * 年龄
     */
    @Field(type = FieldType.Integer)
    private Integer age;

    /**
     * 性别，精确匹配
     */
    @Field(type = FieldType.Keyword)
    private String gender;

    /**
     * 入院日期
     * 注意：新版 Spring Boot 3 + Elasticsearch 8 必须使用 @DateTimeFormat 指定格式
     */
    @Field(type = FieldType.Date)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate inDate;
}
