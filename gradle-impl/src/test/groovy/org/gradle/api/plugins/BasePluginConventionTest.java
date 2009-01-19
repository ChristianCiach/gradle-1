/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.api.plugins;

import org.gradle.api.Project;
import org.gradle.impl.api.plugins.BasePluginConvention;
import static org.hamcrest.Matchers.equalTo;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(JMock.class)
public class BasePluginConventionTest {
    private final JUnit4Mockery context = new JUnit4Mockery();
    private final Project project = context.mock(Project.class);
    private final BasePluginConvention convention = new BasePluginConvention(project);
    private final File buildDir = new File("build-dir");

    @Before
    public void setUp() {
        context.checking(new Expectations() {{
            allowing(project).getBuildDir();
            will(returnValue(buildDir));
        }});
    }

    @Test
    public void defaultValues() {
        assertThat(convention.getReportsDirName(), equalTo("reports"));
    }

    @Test
    public void createsReportsDirFromReportsDirName() {
        convention.setReportsDirName("new-reports");
        assertThat(convention.getReportsDir(), equalTo(new File(buildDir, "new-reports")));
    }
}
