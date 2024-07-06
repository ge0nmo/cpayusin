FROM openjdk:17

EXPOSE 8080

ADD target/cpayusin.jar cpayusin.jar

ENTRYPOINT ["java", "-jar", "/cpayusin.jar"]