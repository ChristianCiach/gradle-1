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

package org.gradle.api.tasks.bundling

/**
 * @author Hans Dockter
 */
class AntJar extends AbstractAntArchive {
    void execute(AntMetaArchiveParameter parameter) {
        assert parameter
        
        Map args = [:]
        if (parameter.gradleManifest?.file) {
            args.manifest = parameter.gradleManifest.file.absolutePath
        }
        args.destfile = "${parameter.destinationDir.absolutePath}/$parameter.archiveName"
        args.whenmanifestonly = parameter.emptyPolicy()
        parameter.ant.jar(args) {
            addMetaArchiveParameter(parameter, delegate)
        }
    }
}
