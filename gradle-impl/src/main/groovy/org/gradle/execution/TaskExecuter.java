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
package org.gradle.execution;

import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;

public interface TaskExecuter extends TaskExecutionGraph {
    /**
     * Adds the given tasks and their dependencies to this graph.
     */
    void addTasks(Iterable<? extends Task> tasks);

    /**
     * Executes the tasks in this graph. Discards the contents of this graph when completed.
     */
    boolean execute();

    /**
     * Adds the given tasks and their dependencies to this graph, then executes all the tasks in this graph. Discards
     * the contents of this graph when completed.
     */
    boolean execute(Iterable<? extends Task> tasks);
}
