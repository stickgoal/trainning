package me.maiz.trainning.framework.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.SQLException;
import java.util.Properties;

@Configuration
@ComponentScan(value="me.maiz.trainning.framework")
@MapperScan("me.maiz.trainning.framework.dao")
@EnableTransactionManagement
public class RootCtxConfig {

    @Bean(initMethod = "init" ,destroyMethod = "close")
    public DruidDataSource dataSource(){
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/mbtest");
        ds.setUsername("root");
        ds.setPassword("root123");
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        try {
            ds.setFilters("stat,slf4j");
            Properties properties = new Properties();
            properties.setProperty("druid.stat.slowSqlMillis","5000");
            ds.setConnectProperties(properties);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(("classpath:mappers/**/*.xml")));
        factoryBean.setConfiguration(getConfiguration());
        factoryBean.setTypeAliasesPackage("me.maiz.trainning.framework.dao.model");

        return factoryBean.getObject();
    }

    private org.apache.ibatis.session.Configuration getConfiguration() {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(Slf4jImpl.class);
        return configuration;
    }


}
