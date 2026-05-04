@ECHO OFF
SETLOCAL
SET MAVEN_VERSION=3.9.9
SET MAVEN_BASE_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%-bin
SET MAVEN_HOME=%MAVEN_BASE_DIR%\apache-maven-%MAVEN_VERSION%
SET MAVEN_ZIP=%MAVEN_BASE_DIR%\apache-maven-%MAVEN_VERSION%-bin.zip

IF NOT EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
    ECHO Baixando Maven %MAVEN_VERSION%...
    IF NOT EXIST "%MAVEN_BASE_DIR%" mkdir "%MAVEN_BASE_DIR%"
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%MAVEN_ZIP%'"
    powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_BASE_DIR%' -Force"
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
ENDLOCAL
