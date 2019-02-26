FROM setusoft/scala-sbt-nodejs

RUN mkdir -p /backend
RUN mkdir -p /backend/out

COPY . /backend

WORKDIR /backend

RUN sbt compile
CMD ["sbt", "run"]
