/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.project;

import org.gradle.api.internal.dependencies.DependencyManagerFactory;
import org.gradle.api.Project;
import org.gradle.groovy.scripts.FileScriptSource;
import org.gradle.groovy.scripts.ScriptSource;
import org.gradle.groovy.scripts.StringScriptSource;
import org.gradle.util.HelperUtil;
import org.gradle.util.ReflectionEqualsMatcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Hans Dockter
 */
public class ProjectFactoryTest {
    private final JUnit4Mockery context = new JUnit4Mockery();
    private final ClassLoader buildScriptClassLoader = new URLClassLoader(new URL[0]);
    private final File rootDir = new File("/root");
    private DependencyManagerFactory dependencyManagerFactoryMock;
    private ITaskFactory taskFactoryMock;
    private DefaultProject rootProject;
    private Project parentProject;
    private BuildScriptProcessor buildScriptProcessor;
    private PluginRegistry pluginRegistry;
    private ProjectRegistry projectRegistry;

    @Before
    public void setUp() throws Exception {
        context.setImposteriser(ClassImposteriser.INSTANCE);
        dependencyManagerFactoryMock = context.mock(DependencyManagerFactory.class);
        taskFactoryMock = context.mock(ITaskFactory.class);
        buildScriptProcessor = context.mock(BuildScriptProcessor.class);
        pluginRegistry = context.mock(PluginRegistry.class);

        rootProject = HelperUtil.createRootProject(rootDir);
        parentProject = HelperUtil.createChildProject(rootProject, "parent");
        projectRegistry = rootProject.getProjectRegistry();

        context.checking(new Expectations() {{
          allowing(dependencyManagerFactoryMock).createDependencyManager(with(any(Project.class)));
        }});
    }

    @Test
    public void testConstructsRootProjectWithBuildFile() {
        ProjectFactory projectFactory = new ProjectFactory(taskFactoryMock, dependencyManagerFactoryMock, buildScriptProcessor, pluginRegistry,
                "build.gradle", projectRegistry, null);

        DefaultProject project = projectFactory.createProject("somename", null, rootDir, buildScriptClassLoader);

        assertEquals("somename", project.getName());
        assertEquals("build.gradle", project.getBuildFileName());
        assertNull(project.getParent());
        assertSame(rootDir, project.getRootDir());
        assertSame(project, project.getRootProject());
        checkProjectResources(project);

        ScriptSource expectedScriptSource = new FileScriptSource("build file", new File(rootDir, "build.gradle"));
        assertThat(project.getBuildScriptSource(), ReflectionEqualsMatcher.reflectionEquals(expectedScriptSource));
    }

    @Test public void testConstructsProjectWithBuildFile() {
        ProjectFactory projectFactory = new ProjectFactory(taskFactoryMock, dependencyManagerFactoryMock, buildScriptProcessor, pluginRegistry,
                "build.gradle", projectRegistry, null);

        DefaultProject project = projectFactory.createProject("somename", parentProject, rootDir, buildScriptClassLoader);

        assertEquals("somename", project.getName());
        assertEquals("build.gradle", project.getBuildFileName());
        assertSame(parentProject, project.getParent());
        assertSame(rootDir, project.getRootDir());
        assertSame(rootProject, project.getRootProject());
        checkProjectResources(project);

        ScriptSource expectedScriptSource = new FileScriptSource("build file", new File(rootDir, "parent/somename/build.gradle"));
        assertThat(project.getBuildScriptSource(), ReflectionEqualsMatcher.reflectionEquals(expectedScriptSource));
    }

    @Test
    public void testConstructsRootProjectWithEmbeddedBuildScript() {
        ProjectFactory projectFactory = new ProjectFactory(taskFactoryMock, dependencyManagerFactoryMock, buildScriptProcessor, pluginRegistry,
                "build.gradle", projectRegistry, "<content>");

        DefaultProject project = projectFactory.createProject("somename", null, rootDir, buildScriptClassLoader);

        assertEquals("somename", project.getName());
        assertEquals("build.gradle", project.getBuildFileName());
        assertSame(rootDir, project.getRootDir());
        assertNull(project.getParent());
        assertSame(project, project.getRootProject());
        checkProjectResources(project);

        ScriptSource expectedScriptSource = new StringScriptSource("embedded build file", "<content>");
        assertThat(project.getBuildScriptSource(), ReflectionEqualsMatcher.reflectionEquals(expectedScriptSource));
    }

    private void checkProjectResources(DefaultProject project) {
        assertSame(taskFactoryMock, project.getTaskFactory());
        assertSame(buildScriptClassLoader, project.getBuildScriptClassLoader());
        assertSame(dependencyManagerFactoryMock, project.getDependencyManagerFactory());
        assertSame(buildScriptProcessor, project.getBuildScriptProcessor());
        assertSame(pluginRegistry, project.getPluginRegistry());
        assertSame(projectRegistry, project.getProjectRegistry());
    }
}
