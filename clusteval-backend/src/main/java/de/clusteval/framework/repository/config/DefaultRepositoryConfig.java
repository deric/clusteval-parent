/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
package de.clusteval.framework.repository.config;

import de.clusteval.api.SQLConfig;
import de.clusteval.api.SQLConfig.DB_TYPE;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Wiwie
 *
 */
public class DefaultRepositoryConfig extends RepositoryConfig {

    public DefaultRepositoryConfig() {
        super(new SQLConfig(false, DB_TYPE.NONE, null, null, null, false), new HashMap<>());
        Logger log = LoggerFactory.getLogger(DefaultRepositoryConfig.class);
        log.info("using default repository config");
    }
}
