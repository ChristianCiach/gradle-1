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

package org.gradle.impl.initialization

import org.gradle.StartParameter
import org.gradle.groovy.scripts.ScriptSource
import org.gradle.impl.api.internal.dependencies.DependencyManagerFactory
import org.gradle.impl.initialization.BaseSettings
import org.gradle.impl.initialization.BuildSourceBuilder
import org.gradle.initialization.IProjectDescriptorRegistry

/**
 * @author Hans Dockter
 */
public class DefaultSettings extends BaseSettings {
    public DefaultSettings() {}

    DefaultSettings(DependencyManagerFactory dependencyManagerFactory, IProjectDescriptorRegistry projectDescriptorRegistry,
                    BuildSourceBuilder buildSourceBuilder, File settingsDir, ScriptSource settingsScript, StartParameter startParameter) {
        super(dependencyManagerFactory, projectDescriptorRegistry, buildSourceBuilder, settingsDir, settingsScript, startParameter)
    }

    def propertyMissing(String property) {
        return dynamicObjectHelper.getProperty(property)
    }

    void setProperty(String name, value) {
        dynamicObjectHelper.setProperty(name, value) 
    }
}
