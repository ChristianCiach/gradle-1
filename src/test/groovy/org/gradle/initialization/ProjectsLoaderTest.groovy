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

package org.gradle.initialization

import org.gradle.StartParameter
import org.gradle.api.Project
import org.gradle.api.internal.dependencies.DefaultDependencyManagerFactory
import org.gradle.api.internal.project.*
import org.gradle.initialization.DefaultSettings
import org.gradle.initialization.ProjectsLoader
import org.gradle.util.HelperUtil
import org.junit.After
import static org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * @author Hans Dockter
 */
class ProjectsLoaderTest {
    ProjectsLoader projectLoader
    ProjectFactory projectFactory
    BuildScriptProcessor buildScriptProcessor
    PluginRegistry pluginRegistry
    File testDir
    File testUserDir
    File testRootProjectDir
    File testParentProjectDir
    ClassLoader testClassLoader
    Map testProjectProperties

    @Before public void setUp()  {
        testClassLoader = new URLClassLoader([] as URL[])
        testProjectProperties = [startProp1: 'startPropValue1', startProp2: 'startPropValue2']
        projectFactory = new ProjectFactory(new TaskFactory(), new DefaultDependencyManagerFactory(new File('root')), null, null, "build.gradle", new ProjectRegistry(), null)
        buildScriptProcessor = new BuildScriptProcessor()
        pluginRegistry = new PluginRegistry()
        projectLoader = new ProjectsLoader(projectFactory)
        testDir = HelperUtil.makeNewTestDir()
        (testUserDir = new File(testDir, 'userDir')).mkdirs()
        (testRootProjectDir = new File(testDir, 'root')).mkdirs()
        (testParentProjectDir = new File(testRootProjectDir, 'parent')).mkdirs()
    }


    @Test public void testProjectsLoader() {
        assertSame(projectFactory, projectLoader.projectFactory)
    }

    @After
    public void tearDown() {
        HelperUtil.deleteTestDir()
    }

    @Test public void testCreateProjects() {
        ParentDirSettingsFinder parentDirSettingsFinder = new ParentDirSettingsFinder(settingsDir: testRootProjectDir)
        StartParameter startParameter = new StartParameter(currentDir: new File(testRootProjectDir, 'parent'), gradleUserHomeDir: testUserDir)
        DefaultSettings settings = new DefaultSettings(new DefaultDependencyManagerFactory(new File('root')), new BuildSourceBuilder(),
                parentDirSettingsFinder, startParameter)
        settings.include('parent' + Project.PATH_SEPARATOR + 'child1', 'parent' + Project.PATH_SEPARATOR + 'child2',
                'parent' + Project.PATH_SEPARATOR + 'folder' + Project.PATH_SEPARATOR + 'child3')
        Map testUserProps = [prop1: 'value1', prop2: 'value2', prop3: 'value3']
        Map testRootProjectProps = [rootProp1: 'rootValue1', rootProp2: 'rootValue2', prop1: 'rootValue']
        Map testParentProjectProps = [parentProp1: 'parentValue1', parentProp2: 'parentValue2', prop1: 'parentValue']
        Map testSystemProps = [
                (ProjectsLoader.SYSTEM_PROJECT_PROPERTIES_PREFIX + "mySystemProp"): 'mySystemPropValue',
                (ProjectsLoader.SYSTEM_PROJECT_PROPERTIES_PREFIX + "prop2"): 'systemPropValue2',
                prop1: 'someSystemPropValue1',
                (ProjectsLoader.SYSTEM_PROJECT_PROPERTIES_PREFIX): 'someValue'
        ]
        Map testEnvProps = [
                (ProjectsLoader.ENV_PROJECT_PROPERTIES_PREFIX + "myEnvProp"): 'myEnvPropValue',
                (ProjectsLoader.ENV_PROJECT_PROPERTIES_PREFIX + "prop3"): 'envPropValue2',
                prop3: 'someEnvPropValue1',
                (ProjectsLoader.ENV_PROJECT_PROPERTIES_PREFIX): 'someValue'
        ]
        new Properties(testUserProps).store(new FileOutputStream(new File(testUserDir, Project.GRADLE_PROPERTIES)), '')
        new Properties(testRootProjectProps).store(new FileOutputStream(new File(testRootProjectDir, Project.GRADLE_PROPERTIES)), '')
        new Properties(testParentProjectProps).store(new FileOutputStream(new File(testParentProjectDir, Project.GRADLE_PROPERTIES)), '')

        projectLoader.load(settings, testClassLoader, startParameter, testProjectProperties, testSystemProps, testEnvProps)

        ProjectInternal rootProject = projectLoader.rootProject
        assert rootProject.buildScriptClassLoader.is(testClassLoader)
        assertSame(testRootProjectDir, rootProject.rootDir)
        assertEquals(Project.PATH_SEPARATOR, rootProject.path)
        assertEquals("$testRootProjectDir.name" as String, rootProject.name)
        assertEquals 1, rootProject.childProjects.size()
        assertNotNull rootProject.childProjects.parent
        assertEquals 3, rootProject.childProjects.parent.childProjects.size()
        assertNotNull rootProject.childProjects.parent.childProjects.child1
        assertNotNull rootProject.childProjects.parent.childProjects.child2
        assertNotNull rootProject.childProjects.parent.childProjects.folder
        assertEquals 1, rootProject.childProjects.parent.childProjects.folder.childProjects.size()
        assertNotNull rootProject.childProjects.parent.childProjects.folder.childProjects.child3

        checkUserProperties(testUserDir, [
                mySystemProp: 'mySystemPropValue',
                prop2: 'systemPropValue2',
                myEnvProp: 'myEnvPropValue',
                prop3: 'envPropValue2'
        ],
                [],
                rootProject, rootProject.childProjects.parent,
                rootProject.childProjects.parent.childProjects.child1,
                rootProject.childProjects.parent.childProjects.child2,
                rootProject.childProjects.parent.childProjects.folder,
                rootProject.childProjects.parent.childProjects.folder.childProjects.child3)
        checkUserProperties(testUserDir, testUserProps, ['prop2', 'prop3'], rootProject, rootProject.childProjects.parent,
                rootProject.childProjects.parent.childProjects.child1,
                rootProject.childProjects.parent.childProjects.child2,
                rootProject.childProjects.parent.childProjects.folder,
                rootProject.childProjects.parent.childProjects.folder.childProjects.child3)
        checkProjectProperties(testRootProjectProps, rootProject, ['prop1'])
        checkProjectProperties(testParentProjectProps, rootProject.childProjects.parent, ['prop1'])
        checkProjectProperties(testProjectProperties, rootProject)
        assertNull(rootProject.childProjects.parent.additionalProperties[new ArrayList(testProjectProperties.keySet())[0]])
    }

    @Test public void testCreateProjectsWithNonExistingUserAndProjectGradleAndProjectProperties() {
        ParentDirSettingsFinder parentDirSettingsFinder = new ParentDirSettingsFinder(settingsDir: testRootProjectDir)
        StartParameter startParameter = new StartParameter(currentDir: testRootProjectDir, gradleUserHomeDir: new File('guh'))
        DefaultSettings settings = new DefaultSettings(new DefaultDependencyManagerFactory(new File('root')), new BuildSourceBuilder(),
                parentDirSettingsFinder, startParameter)

        startParameter.gradleUserHomeDir = new File('nonexistingGradleHome')
        projectLoader.load(settings, testClassLoader, startParameter, [:], [:], [:])

        ProjectInternal rootProject = projectLoader.rootProject

        checkUserProperties(startParameter.gradleUserHomeDir, [:], [], rootProject)
    }

    private void checkUserProperties(File gradleUserHomeDir, Map properties, List excludeKeys, Project[] projects) {
        projects.each {Project project ->
            assertEquals(gradleUserHomeDir.canonicalPath, project.gradleUserHome)
            checkProjectProperties(properties, project, excludeKeys)
        }
    }

    private checkProjectProperties(Map properties, Project project, List excludeKeys = []) {
        properties.each {key, value ->
            if (excludeKeys.contains(key)) {return}
            assertEquals(project."$key", value)
        }
    }

}