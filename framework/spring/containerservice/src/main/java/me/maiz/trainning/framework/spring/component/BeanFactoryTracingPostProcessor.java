package me.maiz.trainning.framework.spring.component;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class BeanFactoryTracingPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition userDAODef = beanFactory.getBeanDefinition("userDAO");
        System.out.println("BeanFactoryTracingPostProcessor : "+userDAODef.getBeanClassName());
    }
}
