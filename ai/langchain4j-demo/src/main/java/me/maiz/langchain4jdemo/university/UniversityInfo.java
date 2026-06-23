package me.maiz.langchain4jdemo.university;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
public class UniversityInfo {
    @Description("学校名字")
    private String name;
    @Description("学校所在省份")
    private String province;
    @Description("学校类型：985/211/双一流/普通")
    private String type;
    @Description("预估录取分数线")
    private Integer estimatedScore;
}
