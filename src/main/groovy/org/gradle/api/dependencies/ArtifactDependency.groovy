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

import org.apache.ivy.core.module.descriptor.DefaultDependencyArtifactDescriptor
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor
import org.apache.ivy.core.module.descriptor.DependencyDescriptor
import org.gradle.api.internal.project.DefaultProject

/**
* @author Hans Dockter
*/
class ArtifactDependency extends AbstractDependency {
    ArtifactDependency(Set confs, Object userDependencyDescription, DefaultProject project) {
        super(confs, userDependencyDescription, project)
    }

    boolean isValidDescription(Object userDependencyDescription) {
        (userDependencyDescription as String).split(':').size() == 4
    }

    Class[] userDepencencyDescriptionType() {
        [String]
    }

    DependencyDescriptor createDepencencyDescriptor() {
        List dependencyParts = (userDependencyDescription as String).split(':')
        DefaultDependencyDescriptor dd = new DefaultDependencyDescriptor(null, createModuleRevisionId(dependencyParts[0], dependencyParts[1], dependencyParts[2]), false, false, false)
        DefaultDependencyArtifactDescriptor artifactDescriptor = new DefaultDependencyArtifactDescriptor(dependencyParts[1], dependencyParts[3], dependencyParts[3], null, null)
        dd.addDependencyArtifact(Dependency.DEFAULT_CONFIGURATION, artifactDescriptor)
        confs.each {
            dd.addDependencyConfiguration(it, Dependency.DEFAULT_CONFIGURATION)
        }
        dd
    }
}
