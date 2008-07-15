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
package org.gradle.api.internal.project

import org.gradle.api.internal.project.DefaultProject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Hans Dockter
 */
class BuildScriptFinder {
    private static Logger logger = LoggerFactory.getLogger(BuildScriptFinder)

    ImportsReader importsReader

    String buildFileName

    BuildScriptFinder() {

    }

    BuildScriptFinder(String buildFileName) {
        this.buildFileName = buildFileName
    }

    String getBuildScript(DefaultProject project) {
        File projectFile = new File(project.projectDir, buildFileName)
        logger.debug("Evaluating project={} Looking for build file={}", project.path, projectFile.canonicalFile)
        if (projectFile.isFile()) {
            logger.debug("Project file found, reading text.")
            String scriptText = projectFile.text
            return scriptText
        } else {
            logger.info("No project file available. Using empty script!")
            return ''
        }
    }

    private String buildScriptWithImports(DefaultProject project) {
        String importsResult = importsReader.getImports(project.rootDir)
        project.buildScriptFinder.getBuildScript(project) + System.properties['line.separator'] + importsResult
    }
}
