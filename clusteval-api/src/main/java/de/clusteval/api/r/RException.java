/**
 * Copyright (C) 2013 Christian Wiwie.
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
 *
 *
 * Contributors: Christian Wiwie - initial API and implementation
 *
 */
package de.clusteval.api.r;

import de.clusteval.api.exceptions.ClustEvalException;

/**
 * @author Christian Wiwie
 *
 */
public class RException extends ClustEvalException {

    protected IRengine engine;

    /**
     *
     * @param rEngine
     * @param message
     */
    public RException(final IRengine rEngine, final String message) {
        super(message);
        this.engine = rEngine;
    }

    public IRengine getEngine() {
        return engine;
    }

}
