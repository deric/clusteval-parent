/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.api;

/**
 *
 * @author deric
 */
public interface Matrix {

    /**
     * @param id1
     * @param id2
     * @return
     */
    double getSimilarity(final int id1, final int id2);

    /**
     * @param id1
     * @param id2
     * @param similarity
     */
    void setSimilarity(final int id1, final int id2, final double similarity);

    int getRows();

    int getColumns();
}