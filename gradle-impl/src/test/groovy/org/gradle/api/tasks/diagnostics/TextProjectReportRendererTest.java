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
package org.gradle.api.tasks.diagnostics;

import org.gradle.api.Project;
import org.gradle.util.HelperUtil;
import static org.hamcrest.Matchers.*;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

@RunWith(JMock.class)
public class TextProjectReportRendererTest {
    private final JUnit4Mockery context = new JUnit4Mockery();

    @Test
    public void writesReportToStandardOutByDefault() throws IOException {
        TextProjectReportRenderer renderer = new TextProjectReportRenderer();
        assertThat(renderer.getWriter(), sameInstance((Appendable) System.out));

        renderer.complete();

        assertThat(renderer.getWriter(), sameInstance((Appendable) System.out));
    }

    @Test
    public void writesReportToAFile() throws IOException {
        File outDir = HelperUtil.makeNewTestDir();
        File outFile = new File(outDir, "report.txt");
        TextProjectReportRenderer renderer = new TextProjectReportRenderer();
        renderer.setOutputFile(outFile);
        assertThat(renderer.getWriter(), instanceOf(FileWriter.class));

        renderer.complete();

        assertTrue(outFile.isFile());
        assertThat(renderer.getWriter(), sameInstance((Appendable) System.out));
    }

    @Test
    public void writeProjectHeader() throws IOException {
        final Project project = context.mock(Project.class);
        StringWriter writer = new StringWriter();

        context.checking(new Expectations() {{
            allowing(project).getPath();
            will(returnValue("<path>"));
        }});

        TextProjectReportRenderer renderer = new TextProjectReportRenderer(writer);
        renderer.startProject(project);
        renderer.completeProject(project);
        renderer.complete();

        assertThat(writer.toString(), containsString("Project <path>"));
    }
}
