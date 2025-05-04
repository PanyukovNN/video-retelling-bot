FROM amazoncorretto:17.0.7-alpine AS BUILD_IMAGE

COPY . ./

RUN ./gradlew build

# ---

FROM amazoncorretto:17.0.7-alpine

COPY --from=BUILD_IMAGE ./build/libs/video-retelling-bot.jar /video-retelling-bot.jar
COPY ./yt-dlp /yt-dlp

EXPOSE 8008
ENV TZ=Europe/Moscow

RUN chmod -R 744 /yt-dlp

ENTRYPOINT ["java", "-jar", "/video-retelling-bot.jar"]