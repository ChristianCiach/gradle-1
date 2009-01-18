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

import groovy.mock.interceptor.MockFor
import org.gradle.impl.DefaultStartParameter
import org.gradle.api.DependencyManager
import org.gradle.api.Project
import org.gradle.api.internal.dependencies.DefaultDependencyManagerFactory
import org.gradle.api.internal.dependencies.DependencyManagerFactory
import org.gradle.api.internal.project.ImportsReader
import org.gradle.groovy.scripts.*
import org.gradle.impl.initialization.DefaultSettings
import org.gradle.impl.initialization.ScriptEvaluatingSettingsProcessor
import org.gradle.util.HelperUtil
import org.gradle.util.JUnit4GroovyMockery
import org.gradle.util.ReflectionEqualsMatcher
import org.hamcrest.Matchers
import org.jmock.lib.legacy.ClassImposteriser
import org.junit.After
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.gradle.impl.DefaultStartParameter
import org.gradle.impl.DefaultStartParameter
import org.gradle.impl.DefaultStartParameter
import org.gradle.impl.groovy.scripts.EmptyScript
import org.gradle.impl.groovy.scripts.ISettingsScriptMetaData
import org.gradle.impl.initialization.DefaultProjectDescriptorRegistry
import org.gradle.impl.initialization.DefaultSettings
import org.gradle.impl.initialization.DefaultSettingsFinder
import org.gradle.impl.initialization.ScriptEvaluatingSettingsProcessor
import org.gradle.impl.initialization.SettingsFactory

/**
 * @author Hans Dockter
 */
class ScriptEvaluatingSettingsProcessorTest {
    static final File TEST_ROOT_DIR = new File('rootDir')
    static final File TEST_CURRENT_DIR = new File('currentDir')
    ScriptEvaluatingSettingsProcessor settingsProcessor
    DefaultSettingsFinder expectedSettingsFinder
    ImportsReader importsReader
    SettingsFactory settingsFactory
    DefaultStartParameter expectedStartParameter
    IScriptProcessor scriptProcessorMock
    ISettingsScriptMetaData settingsScriptMetaData
    DefaultSettings expectedSettings
    MockFor settingsFactoryMocker
    ScriptSource scriptSourceMock
    IGradlePropertiesLoader propertiesLoaderMock
    Map expectedGradleProperties

    JUnit4GroovyMockery context = new JUnit4GroovyMockery()

    @Before public void setUp() {
        context.setImposteriser(ClassImposteriser.INSTANCE)
        instantiateConstructorArgs()
        settingsProcessor = new ScriptEvaluatingSettingsProcessor(settingsScriptMetaData, scriptProcessorMock, importsReader, settingsFactory)
        initSettingsFinder()
        expectedStartParameter = new DefaultStartParameter()
        expectedGradleProperties = [a: 'b']
        propertiesLoaderMock = [getGradleProperties: { expectedGradleProperties } ] as IGradlePropertiesLoader
        initExpectedSettings()
    }

    private void instantiateConstructorArgs() {
        settingsScriptMetaData = context.mock(ISettingsScriptMetaData)
        scriptProcessorMock = context.mock(IScriptProcessor)
        importsReader = new ImportsReader()
        settingsFactory = context.mock(SettingsFactory)
    }

    private void initExpectedSettings() {
        expectedSettings = new DefaultSettings()
        DefaultProjectDescriptorRegistry projectDescriptorRegistry = new DefaultProjectDescriptorRegistry()
        expectedSettings.setRootProjectDescriptor(new DefaultProjectDescriptor(null, TEST_ROOT_DIR.name,
                TEST_ROOT_DIR, projectDescriptorRegistry))
        expectedSettings.setProjectDescriptorRegistry(projectDescriptorRegistry)
        expectedSettings.setStartParameter(expectedStartParameter)
        context.checking {
            one(settingsFactory).createSettings(TEST_ROOT_DIR, scriptSourceMock, expectedGradleProperties, expectedStartParameter)
            will(returnValue(expectedSettings))
        }
    }

    private void initSettingsFinder() {
        expectedSettingsFinder = new DefaultSettingsFinder()
        scriptSourceMock = context.mock(ScriptSource)
        expectedSettingsFinder.setSettingsScriptSource(scriptSourceMock)
        expectedSettingsFinder.setSettingsDir(TEST_ROOT_DIR)
    }

    @After
    public void tearDown() {
        HelperUtil.deleteTestDir()
    }

    @Test public void testSettingsProcessor() {
        assertSame(settingsScriptMetaData, settingsProcessor.settingsScriptMetaData)
        assertSame(scriptProcessorMock, settingsProcessor.scriptProcessor)
        assert settingsProcessor.importsReader.is(importsReader)
        assert settingsProcessor.settingsFactory.is(settingsFactory)
    }

    @Test public void testProcessWithSettingsFile() {
        expectedStartParameter.setCurrentDir(TEST_ROOT_DIR)
        prepareScriptProcessorMock()
        context.checking {
            allowing(scriptSourceMock).getText(); will(returnValue(""))
        }
        assertSame(expectedSettings, settingsProcessor.process(expectedSettingsFinder, expectedStartParameter, propertiesLoaderMock))
    }

    private void prepareScriptProcessorMock() {
        ScriptSource expectedScriptSource = new ImportsScriptSource(scriptSourceMock, importsReader, TEST_ROOT_DIR);
        Script expectedScript = new EmptyScript()
        context.checking {
            one(scriptProcessorMock).createScript(
                    withParam(ReflectionEqualsMatcher.reflectionEquals(expectedScriptSource)),
                    withParam(Matchers.sameInstance(Thread.currentThread().contextClassLoader)),
                    withParam(Matchers.equalTo(ScriptWithSource.class)))
            will(returnValue(expectedScript))
            one(settingsScriptMetaData).applyMetaData(expectedScript, expectedSettings)
        }
    }
}