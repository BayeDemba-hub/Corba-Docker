FROM maven:3.8.6-openjdk-8 AS builder

WORKDIR /build

COPY idl/PDFService.idl /build/idl/PDFService.idl

RUN mkdir -p /build/server/src/main/java && \
    idlj -fall \
         -td /build/server/src/main/java \
         /build/idl/PDFService.idl

COPY server/pom.xml /build/server/pom.xml
COPY server/src /build/server/src

WORKDIR /build/server
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:8-jre

WORKDIR /app

COPY --from=builder \
     /build/server/target/corba-pdf-server-1.8-jar-with-dependencies.jar \
     /app/corba-pdf-server.jar

RUN mkdir -p /tmp/corba_pdf_output

EXPOSE 1050

CMD java \
    -Dorg.omg.CORBA.ORBInitialHost=orbd \
    -Dorg.omg.CORBA.ORBInitialPort=900 \
    -classpath /app/corba-pdf-server.jar \
    com.corba.pdf.PDFServer \
    -ORBInitialHost orbd \
    -ORBInitialPort 900
# cache-bust: Tue May  5 21:45:09 UTC 2026
