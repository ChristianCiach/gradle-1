/*
 * Copyright 2007-2008 the original author or authors.
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
package org.gradle.api.tasks.ide.eclipse;

import org.apache.commons.io.IOUtils;
import org.gradle.api.tasks.AbstractTaskTest;
import org.gradle.impl.api.internal.AbstractTask;
import org.gradle.impl.api.tasks.ide.eclipse.EclipseProject;
import org.gradle.impl.api.tasks.ide.eclipse.ProjectType;
import org.gradle.util.GFileUtils;
import org.gradle.util.HelperUtil;
import org.hamcrest.Matchers;
import org.junit.After;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Hans Dockter
 */
public class EclipseProjectTest extends AbstractTaskTest {
    private EclipseProject eclipseProject;
    private File projectDir;

    public AbstractTask getTask() {
        return eclipseProject;
    }

    @Before
    public void setUp() {
        super.setUp();
        projectDir = HelperUtil.makeNewTestDir();
        eclipseProject = new EclipseProject(getProject(), AbstractTaskTest.TEST_TASK_NAME);
        eclipseProject.setProjectName("myProject");
    }

    @After
    public void tearDown() {
        HelperUtil.deleteTestDir();
    }

    @Test
    public void generateJavaProject() throws IOException {
        eclipseProject.setProjectType(ProjectType.JAVA);
        eclipseProject.execute();
        checkProjectFile("expectedJavaProjectFile.txt");
    }

    @Test
    public void generateSimpleProject() throws IOException {
        eclipseProject.setProjectType(ProjectType.SIMPLE);
        eclipseProject.execute();
        checkProjectFile("expectedSimpleProjectFile.txt");
    }

    private void checkProjectFile(String expectedResourcePath) throws IOException {
        File project = new File(getProject().getProjectDir(), EclipseProject.PROJECT_FILE_NAME);
        assertTrue(project.isFile());
        assertThat(GFileUtils.readFileToString(project),
                Matchers.equalTo(IOUtils.toString(this.getClass().getResourceAsStream(expectedResourcePath))));
    }
}
