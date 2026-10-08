@REM Maven Wrapper for Windows
@SET BASE_DIR=%~dp0
@SET MAVEN_WRAPPER_PROPERTIES=%BASE_DIR%.mvn\wrapper\maven-wrapper.properties
@FOR /F "usebackq tokens=2 delims==" %%A IN (`findstr /i "distributionUrl" "%MAVEN_WRAPPER_PROPERTIES%"`) DO (SET DISTRIBUTION_URL=%%A)
@SET MAVEN_USER_HOME=%USERPROFILE%\.m2
@SET MAVEN_WRAPPER_HOME=%MAVEN_USER_HOME%\wrapper
@FOR %%A IN ("%DISTRIBUTION_URL%") DO (SET DISTRIBUTION_NAME=%%~nxA)
@SET DIST_FOLDER=%DISTRIBUTION_NAME:-bin.zip=%
@SET DISTRIBUTION_DIR=%MAVEN_WRAPPER_HOME%\dists\%DIST_FOLDER%
@IF EXIST "%DISTRIBUTION_DIR%\bin\mvn.cmd" GOTO run_maven
@ECHO Downloading Maven...
@IF NOT EXIST "%DISTRIBUTION_DIR%" MKDIR "%DISTRIBUTION_DIR%"
@powershell -Command "Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '%DISTRIBUTION_DIR%\download.zip'"
@powershell -Command "Expand-Archive -Path '%DISTRIBUTION_DIR%\download.zip' -DestinationPath '%DISTRIBUTION_DIR%' -Force"
@DEL "%DISTRIBUTION_DIR%\download.zip"
:run_maven
@FOR /D %%D IN ("%DISTRIBUTION_DIR%\apache-maven-*") DO (SET "MAVEN_HOME=%%D")
@"%MAVEN_HOME%\bin\mvn.cmd" %*
