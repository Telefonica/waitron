FROM golang:1.14.4-alpine3.12

RUN mkdir /app

ADD . /app/

WORKDIR /app

RUN go build -o waitron .

CMD ["/app/waitron"]

EXPOSE 9090
