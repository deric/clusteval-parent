/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
package de.clusteval.program;

import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.File;
import junitx.framework.ArrayAssert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class ProgramParameterTest extends AbstractClustEvalTest {

    @Test
    public void testOptionsString() throws UnknownProgramParameterException {
        ProgramConfig pc = (ProgramConfig) this
                .getRepository()
                .getRegisteredObject(
                        new File(
                                "testCaseRepository/programs/configs/testOptionsString.config")
                        .getAbsoluteFile());
        IProgramParameter<?> param = pc.getParamWithId("method");
        assertEquals("", param.getMinValue());
        assertEquals("", param.getMaxValue());
        ArrayAssert.assertEquals(new String[]{"ward", "single", "complete"}, param.getOptions());
    }

    @Test
    public void testOptionsFloat() throws UnknownProgramParameterException {
        ProgramConfig pc = (ProgramConfig) this
                .getRepository()
                .getRegisteredObject(
                        new File(
                                "testCaseRepository/programs/configs/testOptionsFloat.config")
                        .getAbsoluteFile());
        IProgramParameter<?> param = pc.getParamWithId("method");
        assertEquals("", param.getMinValue());
        assertEquals("", param.getMaxValue());
        ArrayAssert.assertEquals(new String[]{"0.0", "0.5", "1.0"}, param.getOptions());
    }

    @Test
    public void testOptionsInteger() throws UnknownProgramParameterException {
        ProgramConfig pc = (ProgramConfig) this
                .getRepository()
                .getRegisteredObject(
                        new File(
                                "testCaseRepository/programs/configs/testOptionsInteger.config")
                        .getAbsoluteFile());
        IProgramParameter<?> param = pc.getParamWithId("method");
        assertEquals("", param.getMinValue());
        assertEquals("", param.getMaxValue());
        assertArrayEquals(new String[]{"0", "1", "2"}, param.getOptions());
    }

}
