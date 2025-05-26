@echo off
setlocal enabledelayedexpansion

:: ===== CONFIG =====
set MAIN_CLASS=Client
set OUT_DIR=out
set SRC_DIR=src
set CACHE_DIR=cache
set BUILD_DIR=..\BestBudzBuild
set JAR_NAME=BestBudzClient.jar
set ICON_PATH=..\BestBudzPotLeaf.ico
set LAUNCH4J_PATH=..\..\devTools\Launch4j\launch4j.exe
set LAUNCH4J_CONFIG=BestBudz.xml
:: ==================

echo.
echo 🔄 Cleaning build directories...
rmdir /s /q "%OUT_DIR%" >nul 2>&1
rmdir /s /q "%BUILD_DIR%" >nul 2>&1
mkdir "%OUT_DIR%"
mkdir "%BUILD_DIR%"

echo.
echo 🧵 Compiling Java sources in bulk...
pushd "%SRC_DIR%"
call javac -d "%~dp0\%OUT_DIR%" *.java
popd


echo.
echo 📦 Creating JAR...
jar cfe "%BUILD_DIR%\%JAR_NAME%" Client -C "%OUT_DIR%" .

echo.
echo 🧩 Copying bundled JDK...
xcopy /E /I /Y "C:\RSPS\BestBudz\BestBudzRSPS\BestBudzPortableJDK" "%BUILD_DIR%\runtime" >nul

echo.
echo 📁 Copying cache files...
xcopy /E /I /Y "%CACHE_DIR%" "%BUILD_DIR%\cache" >nul

echo.
echo 🧰 Writing launcher script...
echo @echo off > "%BUILD_DIR%\run.bat"
echo java -jar %JAR_NAME% >> "%BUILD_DIR%\run.bat"
echo pause >> "%BUILD_DIR%\run.bat"

echo.
echo 🧾 Using static Launch4j config at: %LAUNCH4J_CONFIG%


echo.
echo 🧪 Building .exe via Launch4j...
"%LAUNCH4J_PATH%" "%LAUNCH4J_CONFIG%" > "%BUILD_DIR%\launch4j.log" 2>&1

if errorlevel 1 (
    echo ❌ Launch4j failed! View log at: %BUILD_DIR%\launch4j.log
    pause
    exit /b
)

echo Running: "%LAUNCH4J_PATH%" "%LAUNCH4J_CONFIG%"


echo.
echo ✅ Build complete: %BUILD_DIR%\BestBudzRSPS.exe

echo.
echo Running the JAR client
C:\RSPS\BestBudz\BestBudzRSPS\BestBudzPortableJDK\bin\java.exe -jar C:\RSPS\BestBudz\BestBudzRSPS\BestBudzBuild\BestBudzClient.jar
pause
