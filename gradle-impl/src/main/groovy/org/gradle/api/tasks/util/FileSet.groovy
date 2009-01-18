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

package org.gradle.api.tasks.util

import org.gradle.api.InvalidUserDataException

/**
 * @author Hans Dockter
 */
// todo rename dir to base
class FileSet extends PatternSet {
    File dir

    FileSet() {
        this(null)
    }

    FileSet(File dir) {
        this(dir, null)
    }

    FileSet(Object contextObject) {
        super(contextObject)
    }

    FileSet(File dir, Object contextObject) {
        super(contextObject)
        this.dir = dir
    }

    FileSet(Map args) {
        super(transformToFile(args))
        if (!args.dir) { throw new InvalidUserDataException ('A basedir must be specified in the task or via a method argument!') }

    }

    private static Map transformToFile(Map args) {
        Map newArgs = new HashMap(args)
        newArgs.dir = new File(newArgs.dir.toString())
        newArgs
    }

    def addToAntBuilder(node, String childNodeName) {
        node."${childNodeName ?: 'fileset'}"(dir: dir.absolutePath) {
            addIncludesAndExcludesToBuilder(delegate)
        }
    }

}
