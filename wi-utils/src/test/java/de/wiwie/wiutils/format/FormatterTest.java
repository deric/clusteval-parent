/**
 *
 */
package de.wiwie.wiutils.format;

import de.wiwie.wiutils.format.Formatter;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class FormatterTest extends Formatter {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFormatMsToDuration() {
        String s = formatMsToDuration(1000);
        Assert.assertEquals("1s 0ms", s);

        s = formatMsToDuration(10000);
        Assert.assertEquals("10s 0ms", s);

        s = formatMsToDuration(10020);
        Assert.assertEquals("10s 20ms", s);

        s = formatMsToDuration(70020);
        Assert.assertEquals("1m 10s 20ms", s);
    }

}
