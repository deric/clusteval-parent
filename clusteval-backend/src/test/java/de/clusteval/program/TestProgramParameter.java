/**
 * 
 */
package de.clusteval.program;

import java.io.File;

import junit.framework.Assert;
import junitx.framework.ArrayAssert;

import org.junit.Test;

import de.clusteval.utils.AbstractClustEvalTest;

/**
 * @author Christian Wiwie
 * 
 */
public class TestProgramParameter extends AbstractClustEvalTest {

	@Test
	public void testOptionsString() throws UnknownProgramParameterException {
		ProgramConfig pc = (ProgramConfig) this
				.getRepository()
				.getRegisteredObject(
						new File(
								"testCaseRepository/programs/configs/testOptionsString.config")
								.getAbsoluteFile());
		ProgramParameter<?> param = pc.getParamWithId("method");
		Assert.assertEquals("", param.minValue);
		Assert.assertEquals("", param.maxValue);
		ArrayAssert.assertEquals(new String[]{"ward", "single", "complete"},
				param.options);
	}

	@Test
	public void testOptionsFloat() throws UnknownProgramParameterException {
		ProgramConfig pc = (ProgramConfig) this
				.getRepository()
				.getRegisteredObject(
						new File(
								"testCaseRepository/programs/configs/testOptionsFloat.config")
								.getAbsoluteFile());
		ProgramParameter<?> param = pc.getParamWithId("method");
		Assert.assertEquals("", param.minValue);
		Assert.assertEquals("", param.maxValue);
		ArrayAssert.assertEquals(new String[]{"0.0", "0.5", "1.0"},
				param.options);
	}

	@Test
	public void testOptionsInteger() throws UnknownProgramParameterException {
		ProgramConfig pc = (ProgramConfig) this
				.getRepository()
				.getRegisteredObject(
						new File(
								"testCaseRepository/programs/configs/testOptionsInteger.config")
								.getAbsoluteFile());
		ProgramParameter<?> param = pc.getParamWithId("method");
		Assert.assertEquals("", param.minValue);
		Assert.assertEquals("", param.maxValue);
		ArrayAssert.assertEquals(new String[]{"0", "1", "2"}, param.options);
	}

}
