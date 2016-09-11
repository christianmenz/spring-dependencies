package ch.christianmenz.spring.dependency;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian
 */
@Mojo(defaultPhase = LifecyclePhase.INSTALL, name = "list", requiresDependencyResolution = ResolutionScope.COMPILE)
public class SpringDepdendencyMojo extends AbstractMojo {

    /**
     * POM
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter
    private String[] configLocations;

    @Parameter
    private boolean printUrl;
    
    @Parameter
    private boolean webApplication;

    private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private PathMatchingResourcePatternResolver resolver;
    private URLClassLoader classLoader;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            List<URL> urlList = project.getDependencyArtifacts().stream().map(artifact -> toURL(artifact)).collect(Collectors.toList());
            urlList.add(0, toURL(project.getArtifact()));                                    
            if (webApplication) {
                urlList.add(new File(project.getBuild().getOutputDirectory()).toURI().toURL());                
            }            
            classLoader = new URLClassLoader(urlList.toArray(new URL[0]));            
            ResourceLoader loader = new DefaultResourceLoader(classLoader);
            resolver = new PathMatchingResourcePatternResolver(loader);            
            String[] configResource = findConfigResources();

            for (String configLocation : configResource) {
                printDependencies(configLocation, 0, new LinkedHashSet<String>());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoExecutionException("Failed", e);
        }
    }

    private URL toURL(Artifact artifact) {
        try {
            return artifact.getFile().toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void printDependencies(final String resourcePath, int nestingDepth, Set<String> loadedResources) throws IOException, XMLStreamException {
      
        Resource[] resources = resolver.getResources(resourcePath);
        String depthIndicator = StringUtils.repeat("  ", nestingDepth);
                
        for (Resource resource : resources) {                       
            if (!loadedResources.add(resource.getURL().getPath())) {
                throw new RuntimeException("Cyclic dependency detected. Please check your configuration");
            }            
            if (printUrl) {
                System.out.println(depthIndicator + resource.getURL());
            } else {
                System.out.println(depthIndicator + resource.getFilename());
            }

            nestingDepth += 1;

            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(resource.getInputStream());
            while (xmlStreamReader.hasNext()) {
                xmlStreamReader.next();
                if (xmlStreamReader.isStartElement() && xmlStreamReader.getLocalName().equals("import")) {
                    String importResource = xmlStreamReader.getAttributeValue(null, "resource");
                    printDependencies(importResource, nestingDepth, new LinkedHashSet<>(loadedResources));
                }
            }
        }
    }

    private String[] findConfigResources() throws XMLStreamException, FileNotFoundException, SAXException, IOException, ParserConfigurationException, XPathExpressionException {
        if (webApplication) {
            WebXmlResourceReader webXmlResourceReader = new WebXmlResourceReader();
            return webXmlResourceReader.readConfigContextLocations();
        } else {
            return this.configLocations;
        }
    }
}
