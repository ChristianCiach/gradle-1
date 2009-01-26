/*
 * Copyright 2008 the original author or authors.
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
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.util.GUtil;
import static org.gradle.util.WrapUtil.toLinkedSet;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;

@RunWith(JMock.class)
public class PropertyReportTaskTest {
    private final JUnit4Mockery context = new JUnit4Mockery();
    private ProjectInternal project;
    private PropertyReportTask task;
    private PropertyReportRenderer renderer;

    @Before
    public void setup() {
        context.setImposteriser(ClassImposteriser.INSTANCE);
        project = context.mock(ProjectInternal.class);
        renderer = context.mock(PropertyReportRenderer.class);

        context.checking(new Expectations() {{
            allowing(project).getRootProject();
            will(returnValue(project));
            allowing(project).absolutePath("list");
            will(returnValue(":path"));
        }});

        task = new PropertyReportTask(project, "list");
        task.setRenderer(renderer);
    }

    @Test
    public void isDagNeutral() {
        assertTrue(task.isDagNeutral());
    }

    @Test
    public void passesCurrentProjectAndEachSubProjectToRenderer() throws IOException {
        final Project child1 = context.mock(Project.class, "child1");
        final Project child2 = context.mock(Project.class, "child2");

        context.checking(new Expectations() {{
            one(project).getAllprojects();
            will(returnValue(toLinkedSet(child1, project, child2)));

            allowing(project).getProperties();
            will(returnValue(Collections.emptyMap()));

            allowing(child1).getProperties();
            will(returnValue(Collections.emptyMap()));

            allowing(child2).getProperties();
            will(returnValue(Collections.emptyMap()));

            allowing(project).compareTo(child1);
            will(returnValue(-1));

            allowing(child2).compareTo(child1);
            will(returnValue(1));

            Sequence sequence = context.sequence("seq");

            one(renderer).startProject(project);
            inSequence(sequence);
            one(renderer).completeProject(project);
            inSequence(sequence);
            one(renderer).startProject(child1);
            inSequence(sequence);
            one(renderer).completeProject(child1);
            inSequence(sequence);
            one(renderer).startProject(child2);
            inSequence(sequence);
            one(renderer).completeProject(child2);
            inSequence(sequence);
            one(renderer).complete();
            inSequence(sequence);
        }});

        task.execute();
    }

    @Test
    public void passesEachPropertyToRenderer() throws IOException {
        context.checking(new Expectations() {{
            one(project).getAllprojects();
            will(returnValue(toLinkedSet(project)));
            one(project).getProperties();
            will(returnValue(GUtil.map("b", "value2", "a", "value1")));

            Sequence sequence = context.sequence("seq");

            one(renderer).startProject(project);
            inSequence(sequence);
            one(renderer).addProperty("a", "value1");
            inSequence(sequence);
            one(renderer).addProperty("b", "value2");
            inSequence(sequence);
            one(renderer).completeProject(project);
            inSequence(sequence);
            one(renderer).complete();
            inSequence(sequence);
        }});

        task.execute();
    }
}
