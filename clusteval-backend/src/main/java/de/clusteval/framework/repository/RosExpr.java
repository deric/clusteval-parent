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
package de.clusteval.framework.repository;

import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.r.ROperationNotSupported;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

/**
 * Wrapper around Rosuda expression
 *
 * @author deric
 */
public class RosExpr implements RExpr {

    private REXP r;

    public RosExpr(REXP r, IRengine engine) throws RException {
        this.r = r;

        if (r == null) {
            throw new RException(engine, "Evaluation error");
        } else if (r.inherits("try-error")) {
            try {
                throw new RException(engine, r.asString().replace("\n", " - "));
            } catch (REXPMismatchException e) {
                throw new ROperationNotSupported(engine, "Evaluation error");
            }
        }
    }
}
