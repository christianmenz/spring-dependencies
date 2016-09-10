/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.christianmenz.spring.dependency;

import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

/**
 *
 * @author Christian
 */
public class SpringDependencyApplicationContext extends ClassPathXmlApplicationContext {

    private final boolean printUrl;

    SpringDependencyApplicationContext(boolean printUrl) {
        this.printUrl = printUrl;
        
    }

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        // Configure the bean definition reader with this context's
        // resource loading environment.
        beanDefinitionReader.setEnvironment(this.getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
        beanDefinitionReader.setDocumentReaderClass(SpringDependencyDocumentReader.class);
        
        // Allow a subclass to provide custom initialization of the reader,
        // then proceed with actually loading the bean definitions.
        initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);                        
    }   
   

    @Override
    public Resource getResource(String location) {       
        
        return super.getResource(location); //To change body of generated methods, choose Tools | Templates.
    }
    
                      
}
