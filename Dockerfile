FROM openjdk:8-jdk-alpine
EXPOSE 8083
ADD target/bal-des-projets-1.1.7.jar bal-des-projets-1.1.7.jar
ENTRYPOINT ["java","-jar","/bal-des-projets-1.1.7.jar"]