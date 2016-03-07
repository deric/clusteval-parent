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
package utils;

/**
 *
 * @author deric
 */
public abstract class AbstractSimilarityMatrix {

    public AbstractSimilarityMatrix(int rows, int columns) {

    }

    public abstract double get(int id1, int id2);

    abstract int getRows();

    abstract boolean isSymmetric();

    abstract void set(int id1, int id2, double similarity);

}
