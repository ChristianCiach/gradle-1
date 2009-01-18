/*
 * Copyright 2007, 2008 the original author or authors.
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
package org.gradle.impl.execution;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.diagnostics.TaskReportTask;
import org.gradle.api.tasks.diagnostics.PropertyReportTask;
import org.gradle.api.tasks.diagnostics.DependencyReportTask;
import org.gradle.execution.BuildExecuter;
import org.gradle.execution.TaskExecuter;

import java.util.Collections;

/**
 * A {@link org.gradle.execution.BuildExecuter} which executes the built-in tasks which are executable from the command-line.
 */
public class BuiltInTasksBuildExecuter implements BuildExecuter {
    public enum Options {
        TASKS {
            @Override
            public String toString() {
                return "task list";
            }
            Task createTask(Project project) {
                return new TaskReportTask(project, "taskList");
            }},
        PROPERTIES {
            @Override
            public String toString() {
                return "property list";
            }
            Task createTask(Project project) {
                return new PropertyReportTask(project, "propertyList");
            }},
        DEPENDENCIES {
            @Override
            public String toString() {
                return "dependency list";
            }
            Task createTask(Project project) {
                return new DependencyReportTask(project, "dependencyList");
            }};

        abstract Task createTask(Project project);
        }

    private Options options;
    private boolean selected;
    private Task task;

    public BuiltInTasksBuildExecuter(Options options) {
        this.options = options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public boolean hasNext() {
        return !selected;
    }

    public void select(Project project) {
        assert !selected;
        task = options.createTask(project);
        selected = true;
    }

    public String getDescription() {
        return options.toString();
    }

    public Task getTask() {
        return task;
    }

    public void execute(TaskExecuter executer) {
        assert selected;
        executer.execute(Collections.singleton(task));
    }

    public boolean requiresProjectReload() {
        return false;
    }
}
