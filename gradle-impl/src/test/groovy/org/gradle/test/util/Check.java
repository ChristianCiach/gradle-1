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
package org.gradle.test.util;

import org.gradle.api.Task;
import org.gradle.api.TaskAction;
import org.junit.Assert;

/**
 * @author Hans Dockter
 */
public class Check {
    public static void shouldFailWithCause(Class cause, Execute execute) {
        try {
            execute.execute();
        } catch (Exception e) {
            if (!(cause.equals(e.getCause().getClass()))) {
                Assert.fail();
            }
            return;
        }
        Assert.fail();
    }

    public static interface Execute {
        void execute();
    }

    public static TaskAction createTaskAction() {
        return new TaskAction() {
            public void execute(Task task) {
                
            }
        };
    }
}
