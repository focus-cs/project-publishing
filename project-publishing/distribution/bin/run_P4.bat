set BATCH_PATH=D:\path\to\API
set SCIFORMA_URL=https://schneider-mstt.sciforma.net/sciforma
set BATCH_MAIN=project-publishing.jar

set CONF_DIR=%BATCH_PATH%\conf
set LIB_DIR=%BATCH_PATH%\lib

cd %LIB_DIR%

IF EXIST "PSClient_en.jar" (
    del "PSClient_en.jar"
)
IF EXIST "PSClient.jar" (
    del "PSClient.jar"
)
IF EXIST "utilities.jar" (
    del "utilities.jar"
)

wget.exe  -O utilities.jar %SCIFORMA_URL%/utilities.jar
wget.exe  -O PSClient_en.jar %SCIFORMA_URL%/PSClient_en.jar
wget.exe -O PSClient.jar %SCIFORMA_URL%/PSClient.jar

set JAVA_ARGS=-showversion
set JAVA_ARGS=%JAVA_ARGS% -Xms1024m
set JAVA_ARGS=%JAVA_ARGS% -Xmx2048m
set JAVA_ARGS=%JAVA_ARGS% -DpropertySource=file:%CONF_DIR%\psconnect.properties
set JAVA_ARGS=%JAVA_ARGS% -Dlog4j.configuration=file:%CONF_DIR%\log4j.properties
set JAVA_ARGS=%JAVA_ARGS% -jar

java %JAVA_ARGS% %BATCH_MAIN%
