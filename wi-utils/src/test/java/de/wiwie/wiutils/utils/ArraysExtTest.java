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

import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ArraysExtTest {

    public ArraysExtTest() {
    }

    @Test
    public void testRev() {
        double[] test = new double[]{1, 2, 3};
        double[] expect = new double[]{3, 2, 1};
        double[] res = ArraysExt.rev(test);

        assertTrue(Arrays.equals(expect, res));
    }

    @Test
    public void testRev_int() {
        int[] test = new int[]{1, 2, 3};
        int[] expect = new int[]{3, 2, 1};
        int[] res = ArraysExt.rev(test);

        assertTrue(Arrays.equals(expect, res));
    }

}
