package com.woniuxy.boot.starter;

import me.maiz.tool.ExprsTrackConfig;
import me.maiz.tool.KuaiDiNiaoQueryAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(KuaiDiNiaoQueryAPI.class)//指定，当有这个类时启动该配置
@Configuration
@EnableConfigurationProperties(ExpressTrackingProperties.class)//指定外化配置的属性类
public class ExpressTrackingAutoConfiguration {

    @Autowired
    private ExpressTrackingProperties expressTrackingProperties;

    @Bean
    public KuaiDiNiaoQueryAPI kuaiDiNiaoQueryAPI(){

        ExprsTrackConfig config = new ExprsTrackConfig();

        config.setUserKey(expressTrackingProperties.getUserKey());
        config.setUserSecret(expressTrackingProperties.getUserSecret());
        config.setApiUrl(expressTrackingProperties.getApiUrl());


        KuaiDiNiaoQueryAPI kuaiDiNiaoQueryAPI = new KuaiDiNiaoQueryAPI(config);

        return kuaiDiNiaoQueryAPI;
    }




}
