#!/bin/sh

python -m http.server 10000 &

orbd -ORBInitialPort 900 &

sleep 5

java \
-Dorg.omg.CORBA.ORBInitialHost=localhost \
-Dorg.omg.CORBA.ORBInitialPort=900 \
-classpath /app/corba-pdf-server.jar \
com.corba.pdf.PDFServer \
-ORBInitialHost localhost \
-ORBInitialPort 900
