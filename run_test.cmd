@echo off
REM Check environment variables JAVA_HOME and MAVEN_HOME

if "%JAVA_HOME%"=="" (
    echo ERROR: Environment variable JAVA_HOME is not set.
    exit /b 1
)

if not exist "%JAVA_HOME%" (
    echo ERROR: JAVA_HOME directory "%JAVA_HOME%" does not exist.
    exit /b 1
)

if "%MAVEN_HOME%"=="" (
    echo ERROR: Environment variable MAVEN_HOME is not set.
    exit /b 1
)

if not exist "%MAVEN_HOME%" (
    echo ERROR: MAVEN_HOME directory "%MAVEN_HOME%" does not exist.
    exit /b 1
)

echo JAVA_HOME=%JAVA_HOME%
echo MAVEN_HOME=%MAVEN_HOME%

set MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd

if not exist "%MVN_CMD%" (
    echo ERROR: Maven executable not found at %MVN_CMD%
    exit /b 1
)

echo Running Maven test...

"%MVN_CMD%" clean test -Dtest=FullSetupWSandNHANES -X

if %ERRORLEVEL% EQU 0 (
    echo Test executed successfully.
) else (
    echo Test failed with error code %ERRORLEVEL%.
)

echo Surefire reports generated at: target\surefire-reports

exit /b %ERRORLEVEL%
