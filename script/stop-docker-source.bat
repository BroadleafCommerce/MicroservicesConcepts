:: Source script for windows based dev environments. This powers the stop-docker.bat
:: script in the concept projects

@ECHO OFF
call docker-compose "-f" "%CD%\..\..\docker\docker-compose.yml" "-f" "%CD%\..\..\docker\docker-compose.override.yml" "down"