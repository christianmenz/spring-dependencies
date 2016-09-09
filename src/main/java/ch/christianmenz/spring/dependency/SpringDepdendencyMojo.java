package ch.christianmenz.spring.dependency;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            final URL url = project.getArtifact().getFile().toURI().toURL();
            System.out.println(url);
            
            URLClassLoader classLoader = new URLClassLoader(new URL[] {url});
            URL findResource = classLoader.findResource("application.xml");
            System.out.println(findResource);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed", e);
        }

    }

}
