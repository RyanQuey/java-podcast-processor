FROM openjdk:8
COPY . /usr/src/podcast_analysis_tool
WORKDIR /usr/src/podcast_analysis_tool
RUN javac Main.java
CMD ["java", "Main"]
