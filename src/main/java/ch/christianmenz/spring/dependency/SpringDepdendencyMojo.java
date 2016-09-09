
package ch.christianmenz.spring.dependency;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 * @author Christian
 */
@Mojo(defaultPhase = LifecyclePhase.INSTALL, name = "list")
public class SpringDepdendencyMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().error("Baaaaaaaaaaaaaaaaaaaaby");
    }
    
}
