FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD /home/travis/build/bank-microservice/accounts/target/accounts-1.0.0-SNAPSHOT-uber.jar /app

EXPOSE 8080

CMD ["java", "-jar", "accounts-1.0.0-SNAPSHOT-uber.jar"]