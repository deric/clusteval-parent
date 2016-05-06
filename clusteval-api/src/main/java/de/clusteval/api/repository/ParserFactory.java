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
package de.clusteval.api.repository;

import de.clusteval.api.factory.ServiceFactory;
import de.clusteval.api.factory.UnknownProviderException;
import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 */
public class ParserFactory extends ServiceFactory<IParser> {

    private static ParserFactory instance;

    public static ParserFactory getInstance() {
        if (instance == null) {
            instance = new ParserFactory();
        }
        return instance;
    }

    private ParserFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends IParser> list = Lookup.getDefault().lookupAll(IParser.class);
        for (IParser c : list) {
            providers.put(c.getClass().getSimpleName(), c);
        }
        sort();
    }

    public static IParser parseFromString(String name) throws UnknownProviderException {
        return getInstance().getProvider(name);
    }

    public static IParser findParser(final File file) throws UnknownProviderException {
        //TODO: here we need to find out mode: Parser#getModeOfRun(final File absPath)
        return getInstance().getProvider(file.getName());
    }
}
