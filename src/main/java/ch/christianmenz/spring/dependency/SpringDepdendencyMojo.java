package ch.christianmenz.spring.dependency;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
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
    private String applicationContext;
    
    @Parameter
    private boolean printUrl;
    
    private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            List<URL> urlList = project.getDependencyArtifacts().parallelStream().map(artifact -> toURL(artifact)).collect(Collectors.toList());
            urlList.add(0, toURL(project.getArtifact()));
            URLClassLoader classLoader = new URLClassLoader(urlList.toArray(new URL[0]));

            printDependencies(applicationContext, classLoader, 0);
        } catch (Exception e) {
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

    private void printDependencies(final String resourcePath, final URLClassLoader classLoader, int nestingDepth) throws IOException, XMLStreamException {
        ResourceLoader loader = new DefaultResourceLoader(classLoader);
        Resource resource = loader.getResource(resourcePath);        
        
        String depthIndicator = StringUtils.repeat(" ", nestingDepth);
                
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
                printDependencies(importResource, classLoader, nestingDepth);
            }
        }
    }
}
