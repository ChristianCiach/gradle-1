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
 
package org.gradle.api.dependencies

import java.awt.Point
import org.apache.ivy.core.module.descriptor.DependencyArtifactDescriptor
import org.apache.ivy.core.module.descriptor.DependencyDescriptor
import org.apache.ivy.core.module.id.ModuleId
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.gradle.api.InvalidUserDataException
import org.gradle.api.internal.project.DefaultProject

/**
* @author Hans Dockter
*/
class ArtifactDependencyTest extends GroovyTestCase {
    static final String TEST_CONF = "conf"
    static final Set TEST_CONF_SET = [TEST_CONF]
    static final String TEST_ORG = "org.springframework"
    static final String TEST_NAME = "spring"
    static final String TEST_VERSION = "2.5"
    static final String TEST_TYPE = "jar"
    static final DefaultProject TEST_PROJECT = new DefaultProject()
    static final ModuleRevisionId TEST_MODULE_REVISION_ID = new ModuleRevisionId(new ModuleId(TEST_ORG, TEST_NAME), TEST_VERSION)
    static final String TEST_DESCRIPTOR = "$TEST_ORG:$TEST_NAME:$TEST_VERSION:$TEST_TYPE"
    ArtifactDependency artifactDependency

    void setUp() {
        artifactDependency = new ArtifactDependency(TEST_CONF_SET, TEST_DESCRIPTOR, TEST_PROJECT)
    }

    void testArtifactDependency() {
        assertEquals(TEST_CONF_SET, artifactDependency.confs)
        assertEquals(TEST_DESCRIPTOR, artifactDependency.userDependencyDescription)
        assertEquals(TEST_PROJECT, artifactDependency.project)
    }

    void testValidation() {
        shouldFail(InvalidUserDataException) {
            new ArtifactDependency(TEST_CONF_SET, "singlestring", TEST_PROJECT)
        }
        shouldFail(InvalidUserDataException) {
            new ArtifactDependency(TEST_CONF_SET, "junit:junit", TEST_PROJECT)
        }
        shouldFail(InvalidUserDataException) {
            new ArtifactDependency(TEST_CONF_SET, "junit:junit:3.8.2", TEST_PROJECT)
        }
        shouldFail(InvalidUserDataException) {
            new ArtifactDependency(TEST_CONF_SET, new Point(3,4), TEST_PROJECT)
        }
    }

    void testCreateDependencyDescriptor() {
        DependencyDescriptor dependencyDescriptor = artifactDependency.createDepencencyDescriptor()
        assertEquals(TEST_MODULE_REVISION_ID, dependencyDescriptor.dependencyRevisionId)
        assertEquals(1, dependencyDescriptor.getDependencyConfigurations(TEST_CONF).size())
        assertEquals('default', dependencyDescriptor.getDependencyConfigurations(TEST_CONF)[0])
        DependencyArtifactDescriptor artifactDescriptor = dependencyDescriptor.getAllDependencyArtifacts()[0]
        assertEquals('jar', artifactDescriptor.ext)
        assertEquals('jar', artifactDescriptor.type)
    }
}
