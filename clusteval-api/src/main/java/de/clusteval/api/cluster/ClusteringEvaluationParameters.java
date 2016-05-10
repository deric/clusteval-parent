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
package de.clusteval.api.cluster;

import java.util.HashMap;

/**
 * @author Christian Wiwie
 *
 */
public class ClusteringEvaluationParameters extends HashMap<String, String> {

    private static final long serialVersionUID = -6949276396401908242L;

    public ClusteringEvaluationParameters() {
        super();
    }

    /**
     * @param other
     */
    public ClusteringEvaluationParameters(
            final ClusteringEvaluationParameters other) {
        super();
        this.putAll(other);
    }

    @Override
    public ClusteringEvaluationParameters clone() {
        return new ClusteringEvaluationParameters(this);
    }

}
