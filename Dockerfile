FROM openjdk:21-slim

COPY ./build/libs/video-retelling-bot.jar /video-retelling-bot.jar
COPY ./yt-dlp /yt-dlp

EXPOSE 8008
ENV TZ=Europe/Moscow

RUN chmod -R 744 /yt-dlp

ENTRYPOINT ["java", "-jar", "/video-retelling-bot.jar"]