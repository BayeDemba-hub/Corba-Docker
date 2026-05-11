#!/bin/sh

echo "Démarrage ORBD..."

orbd -ORBInitialPort 900 &
sleep 5

echo "Démarrage serveur CORBA..."

java \
  -Dorg.omg.CORBA.ORBInitialHost=localhost \
  -Dorg.omg.CORBA.ORBInitialPort=900 \
  -classpath /app/corba-pdf-server.jar \
  com.corba.pdf.PDFServer \
  -ORBInitialHost localhost \
  -ORBInitialPort 900
