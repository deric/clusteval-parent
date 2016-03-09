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

import cern.colt.matrix.tint.impl.DenseIntMatrix2D;

/**
 *
 * @author deric
 */
public class SimilarityMatrixInt extends AbstractSimilarityMatrix {

    private final DenseIntMatrix2D matrix;

    public SimilarityMatrixInt(int rows, int columns) {
        super(rows, columns);
        this.matrix = new DenseIntMatrix2D(rows, columns);
    }

    @Override
    public double get(int id1, int id2) {
        return matrix.getQuick(id1, id2);
    }

    @Override
    int getRows() {
        return matrix.rows();
    }

    @Override
    boolean isSymmetric() {
        //TODO: not supported?
        return false;
    }

    @Override
    void set(int id1, int id2, double similarity) {
        matrix.setQuick(id1, id2, (int) similarity);
    }

    @Override
    int getColumns() {
        return matrix.columns();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SimilarityMatrixInt)) {
            return false;
        }

        SimilarityMatrixInt other = (SimilarityMatrixInt) obj;
        return matrix.equals(other.matrix);
    }

}
