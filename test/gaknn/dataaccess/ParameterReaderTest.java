/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gaknn.dataaccess;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Admin
 */
public class ParameterReaderTest {

    public ParameterReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of ReadWeights method, of class ParameterReader.
     */
    @Test
    public void testReadWeights() {
        System.out.println("ReadWeights");
        ParameterReader instance = null;
        double[] expResult = null;
        double[] result = instance.ReadWeights();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ReadK method, of class ParameterReader.
     */
    @Test
    public void testReadK() {
        System.out.println("ReadK");
        ParameterReader instance = null;
        int expResult = 0;
        int result = instance.ReadK();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
