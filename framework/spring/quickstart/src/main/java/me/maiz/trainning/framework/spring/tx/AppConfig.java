package me.maiz.trainning.framework.spring.tx;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TxConfig.class)
@ComponentScan("me.maiz.trainning.framework.spring.tx")
public class AppConfig {





}
