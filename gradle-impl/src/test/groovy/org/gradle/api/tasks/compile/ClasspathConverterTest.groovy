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

package org.gradle.api.tasks.compile

import org.gradle.api.InvalidUserDataException
import org.gradle.api.PathValidation
import org.gradle.impl.api.tasks.util.BaseDirConverter
import static org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.gradle.impl.api.tasks.compile.ClasspathConverter
import org.gradle.impl.api.tasks.util.BaseDirConverter

/**
 * @author Hans Dockter
 */
class ClasspathConverterTest {
    ClasspathConverter classpathConverter

    @Before public void setUp() {
        classpathConverter = new ClasspathConverter()
    }

    @Test public void testCreateFileClasspath() {
        File expectedBaseDir = 'basedir' as File
        List elementNames = ['element1', 'element2', 'element3', 'element4']
        List inputClasspath = [elementNames[0], elementNames[1] as File, [elementNames[2], elementNames[3]]]
        classpathConverter.baseDirConverter = [baseDir: {String path, File baseDir, PathValidation validation ->
            assertEquals(expectedBaseDir, baseDir)
            assertEquals(PathValidation.EXISTS, validation)
            path as File
        }] as BaseDirConverter
        List fileClasspath = classpathConverter.createFileClasspath(expectedBaseDir, [inputClasspath])
        fileClasspath.eachWithIndex {File file, int i ->
            assertEquals(elementNames[i] as File, file)
        }
    }

    @Test (expected = InvalidUserDataException) public void testCreateFileClasspathWithNullElement() {
        classpathConverter.createFileClasspath(new File('basedir'), ["element1", null])
    }

    @Test (expected = InvalidUserDataException) public void testCreateFileClasspathWithIllegalElement() {
        classpathConverter.createFileClasspath(new File('basedir'), [5])
    }
}
