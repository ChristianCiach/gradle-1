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

package org.gradle.api.internal.artifacts.dependencies;

import groovy.lang.GString;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.artifacts.DependencyArtifact;
import org.gradle.api.artifacts.DependencyConfigurationMappingContainer;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.internal.artifacts.ivyservice.DefaultDependencyDescriptorFactory;
import org.gradle.api.internal.artifacts.ivyservice.DependencyDescriptorFactory;
import org.gradle.util.WrapUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hans Dockter
 */
public class DefaultModuleDependency extends AbstractDescriptorDependency implements ModuleDependency {
    private static final Pattern extensionSplitter = Pattern.compile("^(.+)\\@([^:]+$)");

    private String group;
    private String name;
    private String version;

    private boolean force = false;
    private boolean changing = false;
    private boolean transitive = true;
    private DependencyDescriptorFactory dependencyDescriptorFactory = new DefaultDependencyDescriptorFactory();

    public DefaultModuleDependency() {
        super();
    }

    public DefaultModuleDependency(DependencyConfigurationMappingContainer dependencyConfigurationMappings, Object userDescription) {
        super(dependencyConfigurationMappings, userDescription);
        initFromUserDescription(userDescription.toString());
    }

    private void initFromUserDescription(String userDescription) {
        Matcher matcher = extensionSplitter.matcher(userDescription);
        String moduleDescription = userDescription;
        String artifactType = null;
        String classifier = null;
        transitive = true;
        if (matcher.matches()) {
            if (matcher.groupCount() != 2) {
                throw new InvalidUserDataException("The description " + userDescription + " is invalid");
            }
            moduleDescription = matcher.group(1);
            artifactType = matcher.group(2);
            transitive = false;
        }
        String[] moduleDescriptionParts = moduleDescription.split(":");
        setModulePropertiesFromParsedDescription(moduleDescriptionParts);
        if (moduleDescriptionParts.length == 4) {
            classifier = moduleDescriptionParts[3];
            if (artifactType == null) {
                artifactType = DependencyArtifact.DEFAULT_TYPE;
            }
        }
        if (artifactType != null) {
            addArtifact(new DefaultDependencyArtifact(name, artifactType, artifactType, classifier, null));
        }
    }

    private void setModulePropertiesFromParsedDescription(String[] dependencyParts) {
        group = dependencyParts[0];
        name = dependencyParts[1];
        version = dependencyParts[2];
    }

    public boolean isValidDescription(Object userDependencyDescription) {
        int elementCount = (userDependencyDescription.toString()).split(":").length;
        return (elementCount == 3 || elementCount == 4);
    }

    public Class[] userDepencencyDescriptionType() {
        return WrapUtil.toArray(String.class, GString.class);
    }

    public DependencyDescriptor createDependencyDescriptor(ModuleDescriptor parent) {
        DependencyDescriptor descriptor = getDependencyDescriptorFactory().createFromModuleDependency(parent, this);
        return getTransformer().transform(descriptor);
    }

    public DefaultModuleDependency force(boolean force) {
        this.force = force;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public DefaultModuleDependency setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getName() {
        return name;
    }

    public DefaultModuleDependency setName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public DefaultModuleDependency setVersion(String version) {
        this.version = version;
        return this;
    }

    public boolean isTransitive() {
        return transitive;
    }

    public DefaultModuleDependency setTransitive(boolean transitive) {
        this.transitive = transitive;
        return this;
    }

    public boolean isForce() {
        return force;
    }

    public DefaultModuleDependency setForce(boolean force) {
        this.force = force;
        return this;
    }

    public boolean isChanging() {
        return changing;
    }

    public DefaultModuleDependency setChanging(boolean changing) {
        this.changing = changing;
        return this;
    }

    public DependencyDescriptorFactory getDependencyDescriptorFactory() {
        return dependencyDescriptorFactory;
    }

    public void setDependencyDescriptorFactory(DependencyDescriptorFactory dependencyDescriptorFactory) {
        this.dependencyDescriptorFactory = dependencyDescriptorFactory;
    }
}
