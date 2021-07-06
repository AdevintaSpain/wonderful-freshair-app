@echo off

call .\gradlew.bat -q installDist
call .\build\install\wonderful-freshair-app\bin\wonderful-freshair-app.bat %*
