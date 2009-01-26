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
package org.gradle;

import org.gradle.initialization.LoggingConfigurer;
import org.gradle.util.HelperUtil;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

/**
 * @author Hans Dockter
 */
public class DefaultGradleFactoryTest {
    private JUnit4Mockery context = new JUnit4Mockery();

    @Test
    public void newInstance() {
        final StartParameter startParameter = HelperUtil.dummyStartParameter();
        final LoggingConfigurer loggingConfigurer = context.mock(LoggingConfigurer.class);
        context.checking(new Expectations() {{
            one(loggingConfigurer).configure(startParameter.getLogLevel());
        }});
        new DefaultGradleFactory(loggingConfigurer).newInstance(startParameter);
    }
}
