/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.christianmenz.spring.dependency;

import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public class SpringDependencyDocumentReader extends DefaultBeanDefinitionDocumentReader {
      
    @Override
    protected void importBeanDefinitionResource(Element ele) {
        System.out.println("importing " + ele.getAttribute("resource"));
        super.importBeanDefinitionResource(ele); //To change body of generated methods, choose Tools | Templates.
    }        
    
}
