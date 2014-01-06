@echo off
dir /s /B *.java > sources.txt
javac -cp "jar\*;" @sources.txt
del /F /Q sources.txt
jar cvf CubeShaft.jar -C src .
pause