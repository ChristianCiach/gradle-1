/*
 * Copyright 2007-2008 the original author or authors.
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

package org.gradle

import org.gradle.CacheUsage
import org.gradle.api.Project
import org.gradle.execution.BuildExecuter
import org.gradle.impl.execution.TaskNameResolvingBuildExecuter
import org.gradle.impl.execution.ProjectDefaultsBuildExecuter
import org.gradle.impl.groovy.scripts.StringScriptSource
import static org.gradle.util.ReflectionEqualsMatcher.*
import static org.hamcrest.Matchers.*
import static org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.gradle.api.initialization.Settings
import org.gradle.util.HelperUtil
import org.gradle.impl.execution.TaskNameResolvingBuildExecuter
import org.gradle.impl.execution.ProjectDefaultsBuildExecuter
import org.gradle.impl.execution.MergingBuildExecuter
import org.gradle.api.logging.LogLevel
import org.gradle.impl.DefaultStartParameter
import org.gradle.impl.groovy.scripts.StringScriptSource
import org.gradle.impl.execution.MergingBuildExecuter

/**
 * @author Hans Dockter
 */
class StartParameterTest {
    DefaultStartParameter testObj
    File gradleHome

    @Before public void setUp() {
        gradleHome = HelperUtil.testDir

        testObj = new DefaultStartParameter(
                settingsFileName: 'settingsfile',
                buildFileName: 'buildfile',
                taskNames: ['a'],
                currentDir: new File('a'),
                searchUpwards: true,
                projectProperties: [a: 'a'],
                systemPropertiesArgs: [b: 'b'],
                gradleUserHomeDir: new File('b'),
                defaultImportsFile: new File('imports'),
                pluginPropertiesFile: new File('plugin'),
                cacheUsage: CacheUsage.ON
        )
    }

    @Test public void testNewInstance() {
        DefaultStartParameter startParameter = testObj.newInstance()
        assert startParameter.equals(testObj)
    }

    @Test public void testDefaultValues() {
        DefaultStartParameter parameter = new DefaultStartParameter();
        assertThat(parameter.gradleUserHomeDir, equalTo(new File(AbstractMain.DEFAULT_GRADLE_USER_HOME)))
        assertThat(parameter.currentDir, equalTo(new File(System.getProperty("user.dir"))))
        assertThat(parameter.buildFileName, equalTo(Project.DEFAULT_BUILD_FILE))
        assertThat(parameter.logLevel, equalTo(LogLevel.LIFECYCLE))
        assertThat(parameter.settingsFileName, equalTo(Settings.DEFAULT_SETTINGS_FILE))
        assertThat(parameter.taskNames, notNullValue())
        assertThat(parameter.projectProperties, notNullValue())
        assertThat(parameter.systemPropertiesArgs, notNullValue())
        assertThat(parameter.buildExecuter, instanceOf(ProjectDefaultsBuildExecuter))
        assertFalse(parameter.mergedBuild)
    }

    @Test public void testSetTaskNames() {
        DefaultStartParameter parameter = new DefaultStartParameter()
        parameter.setTaskNames(Arrays.asList("a", "b"))
        assertThat(parameter.buildExecuter, reflectionEquals(new TaskNameResolvingBuildExecuter(Arrays.asList("a", "b"))))
    }

    @Test public void testSetTaskNamesToEmptyOrNullListUsesProjectDefaultTasks() {
        DefaultStartParameter parameter = new DefaultStartParameter()

        parameter.setBuildExecuter({} as BuildExecuter)
        parameter.setTaskNames(Collections.emptyList())
        assertThat(parameter.buildExecuter, instanceOf(ProjectDefaultsBuildExecuter))

        parameter.setBuildExecuter({} as BuildExecuter)
        parameter.setTaskNames(null)
        assertThat(parameter.buildExecuter, instanceOf(ProjectDefaultsBuildExecuter))
    }

    @Test public void testWrapsBuildExecuterWhenMergedBuild() {
        DefaultStartParameter parameter = new DefaultStartParameter()
        parameter.mergedBuild = true

        assertThat(parameter.buildExecuter, instanceOf(MergingBuildExecuter))
    }
  
    @Test public void testUseEmbeddedBuildFile() {
        DefaultStartParameter parameter = new DefaultStartParameter();
        parameter.useEmbeddedBuildFile("<content>")
        assertThat(parameter.buildScriptSource, reflectionEquals(new StringScriptSource("embedded build file", "<content>")))
        assertThat(parameter.buildFileName, equalTo(Project.EMBEDDED_SCRIPT_ID))
        assertThat(parameter.settingsScriptSource, reflectionEquals(new StringScriptSource("empty settings file", "")))
        assertThat(parameter.searchUpwards, equalTo(false))
    }

    @Test public void testSettingGradleHomeSetsDefaultLocationsIfNotAlreadySet() {
        DefaultStartParameter parameter = new DefaultStartParameter()
        parameter.gradleHomeDir = gradleHome
        assertThat(parameter.defaultImportsFile, equalTo(new File(gradleHome, AbstractMain.IMPORTS_FILE_NAME)))
        assertThat(parameter.pluginPropertiesFile, equalTo(new File(gradleHome, AbstractMain.DEFAULT_PLUGIN_PROPERTIES)))

        parameter = new DefaultStartParameter()
        parameter.defaultImportsFile = new File("imports")
        parameter.pluginPropertiesFile = new File("plugins")
        parameter.gradleHomeDir = gradleHome
        assertThat(parameter.defaultImportsFile, equalTo(new File("imports")))
        assertThat(parameter.pluginPropertiesFile, equalTo(new File("plugins")))
    }
    
    @Test public void testNewBuild() {
        DefaultStartParameter parameter = new DefaultStartParameter()

        // Copied properties
        parameter.gradleHomeDir = gradleHome
        parameter.gradleUserHomeDir = new File("home")
        parameter.cacheUsage = CacheUsage.OFF
        parameter.pluginPropertiesFile = new File("plugins")
        parameter.defaultImportsFile = new File("imports")

        // Non-copied
        parameter.setBuildFileName("b");
        parameter.getTaskNames().add("t1");
        parameter.mergedBuild = true

        DefaultStartParameter newParameter = parameter.newBuild();

        assertThat(newParameter, not(equalTo(parameter)));

        assertThat(newParameter.gradleHomeDir, equalTo(parameter.gradleHomeDir));
        assertThat(newParameter.gradleUserHomeDir, equalTo(parameter.gradleUserHomeDir));
        assertThat(newParameter.cacheUsage, equalTo(parameter.cacheUsage));
        assertThat(newParameter.pluginPropertiesFile, equalTo(parameter.pluginPropertiesFile));
        assertThat(newParameter.defaultImportsFile, equalTo(parameter.defaultImportsFile));

        assertThat(newParameter.buildFileName, equalTo(Project.DEFAULT_BUILD_FILE))
        assertTrue(newParameter.taskNames.empty)
        assertFalse(newParameter.mergedBuild)
    }
}
