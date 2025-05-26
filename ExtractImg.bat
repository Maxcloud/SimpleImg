@echo off
@title Dump
set CLASSPATH=.;\simpleimg-1.4.jar;lib\*
java -Dlogback.configurationFile=config/logback.xml wz.ExtractImg
pause