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

package org.gradle.impl.api.internal.dependencies;

import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.descriptor.ExcludeRule;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.plugins.conflict.LatestConflictManager;
import org.apache.ivy.plugins.latest.LatestRevisionStrategy;
import org.apache.ivy.plugins.matcher.ExactPatternMatcher;
import org.gradle.api.DependencyManager;
import org.gradle.api.internal.dependencies.ivy.IvyUtil;
import org.gradle.api.internal.dependencies.DependencyManagerInternal;
import org.gradle.api.internal.ChainingTransformer;
import org.gradle.api.Transformer;
import org.gradle.api.dependencies.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import groovy.lang.Closure;

/**
 * @author Hans Dockter
 */
public class DefaultModuleDescriptorConverter implements ModuleDescriptorConverter {
    private static Logger logger = LoggerFactory.getLogger(DefaultModuleDescriptorConverter.class);
    private ChainingTransformer<DefaultModuleDescriptor> transformer
            = new ChainingTransformer<DefaultModuleDescriptor>(DefaultModuleDescriptor.class);

    public DefaultModuleDescriptorConverter() {
    }

    public ModuleDescriptor convert(DependencyManagerInternal dependencyManager, boolean includeProjectDependencies) {
        String status = DependencyManager.DEFAULT_STATUS;
        if (dependencyManager.getProject().hasProperty("status")) {
            status = (String) dependencyManager.getProject().property("status");
        }
        DefaultModuleDescriptor moduleDescriptor = new DefaultModuleDescriptor(dependencyManager.createModuleRevisionId(),
                status, null);
        for (Configuration configuration : dependencyManager.getConfigurations().values()) {
            moduleDescriptor.addConfiguration(((DefaultConfiguration) configuration).getIvyConfiguration());
        }
        addDependencyDescriptors(moduleDescriptor, dependencyManager, includeProjectDependencies);
        addArtifacts(moduleDescriptor, dependencyManager);
        addExcludes(moduleDescriptor, dependencyManager);
        moduleDescriptor.addConflictManager(new ModuleId(ExactPatternMatcher.ANY_EXPRESSION,
                    ExactPatternMatcher.ANY_EXPRESSION), ExactPatternMatcher.INSTANCE,
                new LatestConflictManager(new LatestRevisionStrategy()));
        return transformer.transform(moduleDescriptor);
    }

    public void addIvyTransformer(Transformer<DefaultModuleDescriptor> transformer) {
        this.transformer.add(transformer);
    }

    public void addIvyTransformer(Closure tranformer) {
        this.transformer.add(tranformer);
    }
    
    private void addExcludes(DefaultModuleDescriptor moduleDescriptor, DependencyManagerInternal dependencyManager) {
        for (ExcludeRule excludeRule : dependencyManager.getExcludeRules().createRules(IvyUtil.getAllMasterConfs(moduleDescriptor.getConfigurations()))) {
            moduleDescriptor.addExcludeRule(excludeRule);
        }
    }

    private void addDependencyDescriptors(DefaultModuleDescriptor moduleDescriptor, DependencyManagerInternal dependencyManager,
                                          boolean includeProjectDependencies) {
        for (Dependency dependency : dependencyManager.getDependencies()) {
            if (includeProjectDependencies || !(dependency instanceof ProjectDependency)) {
                moduleDescriptor.addDependency(dependency.createDependencyDescriptor(moduleDescriptor));
            }
        }
        for (DependencyDescriptor dependencyDescriptor : dependencyManager.getDependencyDescriptors()) {
            moduleDescriptor.addDependency(dependencyDescriptor);
        }
    }

    private void addArtifacts(DefaultModuleDescriptor moduleDescriptor, DependencyManagerInternal dependencyManager) {
        for (String conf : dependencyManager.getArtifacts().keySet()) {
            List<PublishArtifact> publishArtifacts = dependencyManager.getArtifacts().get(conf);
            for (PublishArtifact publishArtifact : publishArtifacts) {
                logger.debug("Add publishArtifact: {} to configuration={}", publishArtifact, conf);
                moduleDescriptor.addArtifact(conf, publishArtifact.createIvyArtifact(dependencyManager.createModuleRevisionId()));
            }
        }
        for (String conf : dependencyManager.getArtifactDescriptors().keySet()) {
            List<Artifact> artifacts = dependencyManager.getArtifactDescriptors().get(conf);
            for (Artifact artifact : artifacts) {
                moduleDescriptor.addArtifact(conf, artifact);
            }
        }
    }
}
