:: Source script for windows based dev environments. This powers the reset-and-run.bat
:: script in the concept projects

@ECHO OFF
ECHO Determining artifactId
FOR /F "tokens=*" %%g IN ('mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate \"-Dexpression=project.artifactId\" \"-q\" \"-DforceStdout\"') do (SET artifactId=%%g)
ECHO %artifactId%
ECHO Determining local repository path
FOR /F "tokens=*" %%g IN ('mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate \"-Dexpression=settings.localRepository\" \"-q\" \"-DforceStdout\"') do (SET repoPath=%%g)
ECHO %repoPath%
ECHO Determining project version
FOR /F "tokens=*" %%g IN ('mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate \"-Dexpression=project.version\" \"-q\" \"-DforceStdout\"') do (SET version=%%g)
ECHO %version%
ECHO Determining change log path
FOR /F "tokens=*" %%g IN ('mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate \"-Dexpression=change-log-path\" \"-q\" \"-DforceStdout\"') do (SET changeLog=%%g)
ECHO %changeLog%
ECHO -------------------------------------------------------
ECHO Building App And Generating Liquibase Schema For %artifactId%
ECHO -------------------------------------------------------
call mvn "clean" "install" "-f" "%CD%\..\..\pom.xml" "-pl" ":%artifactId%" "-am" "-DskipTests"
ECHO -------------------------------------------------------
ECHO Launching Supporting Components In Docker
ECHO admingateway
ECHO adminweb
ECHO auth
ECHO commercegateway
ECHO commerceweb
ECHO kafka
ECHO solr
ECHO openapi
ECHO zookeeper
ECHO postgres
ECHO -------------------------------------------------------
call docker-compose "-f" "%CD%\..\..\docker\docker-compose.yml" "-f" "%CD%\..\..\docker\docker-compose.override.yml" "down"
call docker-compose "-f" "%CD%\..\..\docker\docker-compose.yml" "-f" "%CD%\..\..\docker\docker-compose.override.yml" "pull"
call docker-compose "-f" "%CD%\..\..\docker\docker-compose.yml" "-f" "%CD%\..\..\docker\docker-compose.override.yml" "up" "-d"
ECHO -------------------------------------------------------
ECHO Launching App
ECHO -------------------------------------------------------
call mvn "install" "-f" "%CD%\..\..\pom.xml" "-pl" ":%artifactId%" "-am" "-DskipTests" "-Papp,mapperCache" "-PnoSchema"
set NO_GCE_CHECK=true
set BROADLEAF_CATALOG_LIQUIBASE_CHANGELOG=%changeLog%
call java "--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED" "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED" "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED" "--add-opens=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED" "--add-opens=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED" "--add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED" "--add-opens=java.base/java.io=ALL-UNNAMED" "--add-opens=java.base/java.nio=ALL-UNNAMED" "--add-opens=java.base/java.util=ALL-UNNAMED" "--add-opens=java.base/java.lang=ALL-UNNAMED" "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED" "--add-opens=java.base/java.time=ALL-UNNAMED" "--add-opens=java.base/java.time.format=ALL-UNNAMED" "-jar" "target/%artifactId%-%version%.jar"
