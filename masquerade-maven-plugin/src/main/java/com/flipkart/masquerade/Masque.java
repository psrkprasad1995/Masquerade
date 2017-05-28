/*
 * Copyright 2017 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.masquerade;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrey.garg on 12/05/17.
 */
@Mojo(
        name = "masquerade",
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class Masque extends AbstractMojo {

    @Parameter(required = true)
    private String configurationClass;

    @Parameter(required = true)
    private File targetFile;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        addProjectDependenciesToClasspath();
        getLog().info("Fetching Configuration from " + configurationClass);

        final String classInput = configurationClass;
        try {
            Class<Configuration> configurationClass = (Class<Configuration>) Class.forName(classInput, true, Thread.currentThread().getContextClassLoader());
            Configuration configuration = configurationClass.newInstance();

            getLog().info("Configuration instance created.");
            Masquerade.initialize(configuration, Thread.currentThread().getContextClassLoader(), targetFile);
        } catch (Exception e) {
            throw new MojoExecutionException("Wrong configuration class", e);
        }
        getLog().info("Masquerade is open for invites.");
    }

    private void addProjectDependenciesToClasspath() {

        try {

            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader newClassLoader = getClassLoader(project, oldClassLoader, getLog());
            Thread.currentThread().setContextClassLoader(newClassLoader);

        } catch (Exception e) {
            getLog().info("Skipping addition of project artifacts, there appears to be a dependecy resolution problem", e);
        }

    }

    public ClassLoader getClassLoader(MavenProject project, final ClassLoader parent, Log log) throws DependencyResolutionRequiredException {

        @SuppressWarnings("unchecked")
        List<String> classpathElements = project.getCompileClasspathElements();

        final List<URL> classpathUrls = new ArrayList<URL>(classpathElements.size());

        for (String classpathElement : classpathElements) {
            try {
                log.debug("Adding project artifact to classpath: " + classpathElement);
                classpathUrls.add(new File(classpathElement).toURI().toURL());
            } catch (MalformedURLException e) {
                log.debug("Unable to use classpath entry as it could not be understood as a valid URL: " + classpathElement, e);
            }
        }

        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return new URLClassLoader(classpathUrls.toArray(new URL[classpathUrls.size()]), parent);
            }
        });

    }

}
