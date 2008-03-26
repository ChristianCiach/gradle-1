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

package org.gradle.api.internal.dependencies

import org.apache.ivy.core.settings.IvySettings
import org.apache.ivy.plugins.resolver.ChainResolver
import org.apache.ivy.plugins.resolver.RepositoryResolver

/**
 * @author Hans Dockter
 */
class SettingsConverter {
    static final String CHAIN_RESOLVER_NAME = 'chain'

    IvySettings ivySettings
    
    IvySettings convert(def classpathResolvers, def otherResolvers, File gradleUserHome, RepositoryResolver buildResolver) {
        if (ivySettings) {return ivySettings}
        ChainResolver chainResolver = new ChainResolver()
        chainResolver.name = CHAIN_RESOLVER_NAME
        chainResolver.add(buildResolver)
        classpathResolvers.each {
            chainResolver.add(it)
        }
        IvySettings ivySettings = new IvySettings()
        otherResolvers.each {ivySettings.addResolver(it)}
        ivySettings.addResolver(chainResolver)
        ivySettings.setDefaultResolver(CHAIN_RESOLVER_NAME)
        ivySettings.setVariable('ivy.cache.dir', gradleUserHome.canonicalPath + '/cache')
        buildResolver.repositoryCacheManager.settings = ivySettings
        ivySettings
    }
}
