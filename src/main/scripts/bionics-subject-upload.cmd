@echo off

cmd /k java -cp %~dp0\daris-bionics-client.jar daris.client.bionics.BionicsSubjectUpload %*
