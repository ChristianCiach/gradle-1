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

import groovy.mock.interceptor.MockFor
import org.gradle.api.tasks.AbstractTaskTest
import org.gradle.api.tasks.util.FileSet
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.gradle.api.tasks.bundling.*
import static org.junit.Assert.assertEquals

/**
 * @author Hans Dockter
 */
class JarTest extends AbstractArchiveTaskTest {
    Jar jar

    MockFor antJarMocker

    @Before public void setUp()  {
        super.setUp()
        jar = new Jar(project, AbstractTaskTest.TEST_TASK_NAME)
        configure(jar)
        jar.manifest = new GradleManifest()
        jar.metaInfResourceCollections = [new FileSet()]
        antJarMocker = new MockFor(AntJar)
    }

    AbstractArchiveTask getArchiveTask() {
        jar
    }

    MockFor getAntMocker(boolean toBeCalled) {
        antJarMocker.demand.execute(toBeCalled ? 1..1 : 0..0) {AntMetaArchiveParameter metaArchiveParameter ->
            if (toBeCalled) {
                checkMetaArchiveParameterEqualsArchive(metaArchiveParameter, jar)
            }
        }
        antJarMocker
    }

    def getAnt() {
        jar.antJar
    }

    @Test public void testJar() {
        assertEquals(Jar.DEFAULT_EXTENSION, jar.extension)
    }

}