@echo off
java -cp .;CubeShaft.jar;jar\lwjgl.jar;jar\lwjgl_util.jar;jar\jinput.jar; -Djava.library.path=native\windows com.naronco.cubeshaft.Cubeshaft -Xmx=1000
pause