/*
 * Copyright (C) 2016 deric
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.wiwie.wiutils.utils;

import static junit.framework.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class SimilarityMatrixTest {

    public SimilarityMatrixTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMatrix() {
        SimilarityMatrix A = new SimilarityMatrix(new double[][]{new double[]{1.0, 2.0}, new double[]{2.0, 1.0}});
        System.out.println("A: " + A.toString());
    }

    @Test
    public void testEquals() {
        SimilarityMatrix A = new SimilarityMatrix(new double[][]{new double[]{1.0, 2.0}, new double[]{2.0, 1.0}});
        SimilarityMatrix B = new SimilarityMatrix(new double[][]{new double[]{1.0, 2.0}, new double[]{2.0, 1.0}});

        assertEquals(A, B);
    }

}
