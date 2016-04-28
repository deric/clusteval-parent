#!/bin/bash
JAVA_HOME=/usr/lib/jvm/java-8-oracle
mvn install -Dmaven.test.skip=true && mvn "-Dexec.args=-classpath %classpath de.clusteval.framework.ClustevalBackendServer" -Dexec.executable=${JAVA_HOME}/bin/java org.codehaus.mojo:exec-maven-plugin:1.2.1:exec
