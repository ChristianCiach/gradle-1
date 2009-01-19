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

package org.gradle.api.plugins

import org.gradle.api.plugins.AbstractPluginConventionTest
import org.gradle.impl.api.plugins.GroovyPluginConvention
import org.gradle.impl.api.plugins.JavaPluginConvention
import org.junit.Before
import org.junit.Test
import static org.hamcrest.Matchers.equalTo
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThat

/**
 * @author Hans Dockter
 */
class GroovyPluginConventionTest extends AbstractPluginConventionTest {
    private GroovyPluginConvention groovyConvention
    private JavaPluginConvention javaConvention

    Class getType() {
        GroovyPluginConvention
    }

    Map getCustomValues() {
        [groovySrcDirNames: ['newSourceRootName']]
    }

    @Before public void setUp()  {
        super.setUp()
        javaConvention = new JavaPluginConvention(project, [:])
        project.convention.plugins.java = javaConvention
        groovyConvention = new GroovyPluginConvention(project, [:])
    }

    @Test public void testGroovyConvention() {
        assertEquals(['main/groovy'], groovyConvention.groovySrcDirNames)
        assertEquals(['test/groovy'], groovyConvention.groovyTestSrcDirNames)
        assertEquals([], groovyConvention.floatingGroovySrcDirs)
        assertEquals([], groovyConvention.floatingGroovyTestSrcDirs)
    }

    @Test public void testGroovyDefaultDirs() {
        checkGroovyDirs(project.srcRootName)
    }

    @Test public void testGroovyDynamicDirs() {
        project.srcRootName = 'mysrc'
        project.buildDirName = 'mybuild'
        checkGroovyDirs(project.srcRootName)
    }

    @Test public void testGroovyDocDirUsesJavaConventionToDetermineDocsDir() {
        assertThat(groovyConvention.groovydocDir, equalTo(new File(javaConvention.docsDir, "groovydoc")))

        groovyConvention.groovydocDirName = "other-dir"
        assertThat(groovyConvention.groovydocDir, equalTo(new File(javaConvention.docsDir, "other-dir")))
    }
    
    private void checkGroovyDirs(String srcRootName) {
        groovyConvention.floatingGroovySrcDirs << 'someGroovySrcDir' as File
        groovyConvention.floatingGroovyTestSrcDirs <<'someGroovyTestSrcDir' as File
        assertEquals([new File(project.srcRoot, groovyConvention.groovySrcDirNames[0])] + groovyConvention.floatingGroovySrcDirs,
                groovyConvention.groovySrcDirs)
        assertEquals([new File(project.srcRoot, groovyConvention.groovyTestSrcDirNames[0])] + groovyConvention.floatingGroovyTestSrcDirs,
                groovyConvention.groovyTestSrcDirs)
    }
}
