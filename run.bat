@echo off
cd src
javac --module-path ..\javafx-lib --add-modules javafx.controls -classpath ..\lib\mysql-connector-j-9.7.0.jar;. MokkiGUI.java
java -Djava.library.path=..\javafx-lib --module-path ..\javafx-lib --add-modules javafx.controls,javafx.graphics -classpath ..\lib\mysql-connector-j-9.7.0.jar;. MokkiGUI
cd ..
pause