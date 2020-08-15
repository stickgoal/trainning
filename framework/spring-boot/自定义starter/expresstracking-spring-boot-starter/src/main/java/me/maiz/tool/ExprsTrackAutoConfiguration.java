package me.maiz.tool;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//当存在此类是启动配置
@ConditionalOnClass(KuaiDiNiaoQueryAPI.class)
//启用配置类
@EnableConfigurationProperties(ExprsTrackProperties.class)
public class ExprsTrackAutoConfiguration {

    @Autowired
    private ExprsTrackProperties exprsTrackProperties;

    @Bean
    public KuaiDiNiaoQueryAPI kuaiDiNiaoQueryAPI(){

        ExprsTrackConfig config = new ExprsTrackConfig();
        BeanUtils.copyProperties(exprsTrackProperties,config);

        return new KuaiDiNiaoQueryAPI(config);
    }



}
