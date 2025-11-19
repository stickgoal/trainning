package me.maiz.shardingjdbcdemo;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class MybatisPlusGenerator {
    //修改此处的包路径，注意：是到模块上一级
    public static final String PACKAGE_NAME = "me.maiz";

    private static final Logger logger = LoggerFactory.getLogger(MybatisPlusGenerator.class);
    public static final String TABLE_PREFIX = "user";

    public static void main(String[] args) {
        //从application.yml中获取
        Map<String, String> ds = parseDataSourceFormYml();

        FastAutoGenerator.create(ds.get("url"), ds.get("username"), ds.get("password"))
                .globalConfig(builder -> builder
                        .author("Lucas")
                        .outputDir(Paths.get(System.getProperty("user.dir"))  +"/src/main/java")
                        .commentDate("yyyy-MM-dd").disableOpenDir()
                )
                .packageConfig(builder -> builder
                        .moduleName("shardingjdbcdemo")
                        .parent(PACKAGE_NAME)
                        .entity("model")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .controller("controller")
                        //设置xml到resources位置
                        .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir")  + "/src/main/resources/mapper"))
                )
                .strategyConfig((scanner, builder) -> builder
                        .addInclude(scanner.apply("请输入表名，多个表名用,隔开"))
                        .addTablePrefix(TABLE_PREFIX).serviceBuilder().convertServiceFileName(entityName -> entityName + "Service")
                        .controllerBuilder().enableRestStyle()
                        .entityBuilder()
                        .enableLombok()
                        .enableRemoveIsPrefix()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

    private static Map<String, String> parseDataSourceFormYml() {

        Yaml yaml = new Yaml();

        String path = Paths.get(System.getProperty("user.dir"))  + "/src/main/resources/application.yml";
        logger.info("获取配置文件路径:{}", path);
        InputStream inputStream = FileUtil.getInputStream(path);
        if (inputStream == null) {
            throw new IllegalArgumentException("application.yml 文件未找到！");
        }
        // 将 YAML 文件解析为 Map

        Map<String, Map<String, Map<String, String>>> yamlMap = yaml.load(inputStream);
        Map<String, String> ds = yamlMap.get("spring").get("datasource");

        logger.info("获取配置信息:{}", ds);
        return ds;

    }

}