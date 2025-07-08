FROM openjdk:17
MAINTAINER Vika
RUN javac HotelBookingApplication.java
COPY target/HotelBooking-0.0.1-SNAPSHOT-plain.jar /app/app.jar
WORKDIR /app
CMD ["java", "HotelBookingApplication", "-jar", "app.jar"]