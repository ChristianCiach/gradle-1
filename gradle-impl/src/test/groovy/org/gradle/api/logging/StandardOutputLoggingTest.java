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
package org.gradle.api.logging;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.slf4j.Marker;
import org.gradle.impl.api.logging.StandardOutputLogging;
import org.gradle.impl.api.logging.StandardOutputState;
import ch.qos.logback.classic.Level;

/**
 * @author Hans Dockter
 */
public class StandardOutputLoggingTest {

    @Before
    public void setUp() {
        StandardOutputLogging.off();
    }

    private void setToNonDefaultValues(boolean out, boolean err) {
        if (out) { StandardOutputLogging.onOut(LogLevel.DEBUG); }
        if (err) { StandardOutputLogging.onErr(LogLevel.INFO); }
    }

    @After
    public void tearDown() {
        StandardOutputLogging.off();
    }

    @Test
    public void on() {
        setToNonDefaultValues(true, true);
        StandardOutputLogging.on(LogLevel.INFO);
        checkOut(Level.INFO, null);
        checkErr(Level.ERROR);
    }

    @Test
    public void onOut() {
        setToNonDefaultValues(true, false);
        StandardOutputLogging.onOut(LogLevel.INFO);
        checkOut(Level.INFO, null);
        assertSame(StandardOutputLogging.DEFAULT_ERR, System.err);
    }

    @Test
    public void onOutWithLifecycle() {
        setToNonDefaultValues(true, false);
        StandardOutputLogging.onOut(LogLevel.LIFECYCLE);
        checkOut(Level.INFO, Logging.LIFECYCLE);
        assertSame(StandardOutputLogging.DEFAULT_ERR, System.err);
    }

    @Test
    public void onOutWithQuiet() {
        setToNonDefaultValues(true, false);
        StandardOutputLogging.onOut(LogLevel.QUIET);
        checkOut(Level.INFO, Logging.QUIET);
        assertSame(StandardOutputLogging.DEFAULT_ERR, System.err);
    }

    @Test
    public void onErr() {
        setToNonDefaultValues(false, true);
        StandardOutputLogging.onErr(LogLevel.ERROR);
        checkErr(Level.ERROR);
        assertEquals(StandardOutputLogging.DEFAULT_OUT, System.out);
    }

    @Test
    public void off() {
        StandardOutputLogging.off();
        assertEquals(StandardOutputLogging.DEFAULT_OUT, System.out);
        assertEquals(StandardOutputLogging.DEFAULT_ERR, System.err);
    }

    @Test
    public void offOut() {
        StandardOutputLogging.on(LogLevel.INFO);
        StandardOutputLogging.offOut();
        assertEquals(StandardOutputLogging.DEFAULT_OUT, System.out);
        assertEquals(StandardOutputLogging.ERR_LOGGING_STREAM.get(), System.err);
    }

    @Test
    public void offErr() {
        StandardOutputLogging.on(LogLevel.INFO);
        StandardOutputLogging.offErr();
        assertEquals(StandardOutputLogging.OUT_LOGGING_STREAM.get(), System.out);
        assertEquals(StandardOutputLogging.DEFAULT_ERR, System.err);
    }

    @Test
    public void init() {
        assertEquals(StandardOutputLogging.DEFAULT_OUT, System.out);
        assertEquals(StandardOutputLogging.DEFAULT_ERR, System.err);
    }

    @Test
    public void testGetAndRestoreState() {
        StandardOutputLogging.on(LogLevel.INFO);
        StandardOutputState state = StandardOutputLogging.getStateSnapshot();
        assertEquals(StandardOutputLogging.OUT_LOGGING_STREAM.get(), state.getOutStream());
        assertEquals(StandardOutputLogging.ERR_LOGGING_STREAM.get(), state.getErrStream());
        StandardOutputLogging.off();
        StandardOutputLogging.restoreState(state);
        assertEquals(StandardOutputLogging.OUT_LOGGING_STREAM.get(), System.out);
        assertEquals(StandardOutputLogging.ERR_LOGGING_STREAM.get(), System.err);
    }

    private void checkOut(Level expectedOut, Marker expectedOutMarker) {
        assertEquals(StandardOutputLogging.OUT_LOGGING_STREAM.get(), System.out);
        assertEquals(StandardOutputLogging.getOutAdapter().getLevel(), expectedOut);
        assertEquals(StandardOutputLogging.getOutAdapter().getMarker(), expectedOutMarker);
    }

    private void checkErr(Level expectedErr) {
        assertEquals(StandardOutputLogging.ERR_LOGGING_STREAM.get(), System.err);
        assertEquals(StandardOutputLogging.getErrAdapter().getLevel(), expectedErr);
        assertEquals(StandardOutputLogging.getErrAdapter().getMarker(), null);
    }
}
