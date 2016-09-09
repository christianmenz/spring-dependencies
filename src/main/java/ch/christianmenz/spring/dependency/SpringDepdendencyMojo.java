package ch.christianmenz.spring.dependency;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.common.reflect.ClassPath;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            
            List<URL> urlList = project.getDependencyArtifacts().parallelStream().map(artifact -> toURL(artifact)).collect(Collectors.toList());
            urlList.add(0, toURL(project.getArtifact()));
                     
            URLClassLoader classLoader = new URLClassLoader(urlList.toArray(new URL[0]));
            URL findResource = classLoader.findResource("application.xml");
            System.out.println(findResource);
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

}
