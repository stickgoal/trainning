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
    @Id
    private String recordId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String patientName;

    @Field(type = FieldType.Keyword)
    private String deptName;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String diagnosis;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Keyword)
    private String gender;

    @Field(
            type = FieldType.Date)
    @DateTimeFormat(pattern = "yyyy-MM-dd")  // 新版 SB3 + ES8 必须用这个
    private LocalDate inDate;
}
