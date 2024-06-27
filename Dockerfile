# https://hub.docker.com/_/maven
FROM openjdk

#FROM maven:3.8.5-openjdk-17 AS build
LABEL authors="tais_"

# Defina as variáveis de ambiente
ENV X_RAPIDAPI_KEY=a41e1263b1mshf1453c5878e1bdep1c779ejsnd4418b33f16a
ENV X_RAPIDAPI_HOST=nsfw-image-classification1.p.rapidapi.com
ENV API_PASSWORD=test
ENV CLOUDINARY_CLOUD_NAME=dgueb0wir
ENV CLOUDINARY_API_KEY=546318655587864
ENV CLOUDINARY_API_SECRET=UPEpuVA_PWlah9B5BrkZMx7E5VE
ENV PERSPECTIVE_API_KEY=AIzaSyAR_kjALl3HzXJRaG6V3eKlo1pgEBLwtKw
ENV RECAPTCHA_SITE_KEY=6LfOFAMqAAAAAKvHm--E8qVYG7o_3yni9DVyEYgG
ENV TWILIO_ACCOUNT_SID=AC30d2adc6827d2186d06f36785556ac8c
ENV TWILIO_AUTH_TOKEN=8a3dd9e822be613339400f3f35102d96
ENV TWILIO_VERIFY_SERVICE_SID=VA6c8f3c66a214024b60d809e1953dad68

WORKDIR /app

COPY target/servicebook-2.7.1-SNAPSHOT.jar /app/servicebook.jar
#COPY pom.xml /app/pom.xml
COPY src/main/webapp/assets/libraries/materialize/ /app/src/main/webapp/assets/libraries/materialize/
COPY src/main/webapp/assets/libraries/select2/ /app/src/main/webapp/assets/libraries/select2/
COPY src/main/webapp/assets/resources/images/ /app/src/main/webapp/assets/resources/images/
COPY src/main/webapp/assets/resources/scripts/ /app/src/main/webapp/assets/resources/scripts/
COPY src/main/webapp/assets/resources/styles/ /app/src/main/webapp/assets/resources/styles/

COPY src/main/webapp/WEB-INF/tags/ /app/src/main/webapp/WEB-INF/tags/

COPY src/main/webapp/WEB-INF/view/ /app/src/main/webapp/WEB-INF/view/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "servicebook.jar"]
