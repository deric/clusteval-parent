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
package de.clusteval.api.run;

import de.clusteval.api.Pair;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author deric
 */
public class OptStatus {

    private Map<Pair<String, String>, Pair<Double, Map<String, Pair<Map<String, String>, String>>>> status;

    public OptStatus() {
        status = new HashMap<>(10);
    }

    public Pair<Double, Map<String, Pair<Map<String, String>, String>>> get(Pair<String, String> key) {
        return status.get(key);
    }

    public void put(Pair<String, String> key, Pair<Double, Map<String, Pair<Map<String, String>, String>>> value) {
        status.put(key, value);
    }

}
