FROM debian:latest

USER root

RUN apt-get update && \
    apt-get install -y \
    wget \
    openjdk-21-jre-headless \
    && apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Download and extract Apache Jena
RUN wget -q https://dlcdn.apache.org/jena/binaries/apache-jena-5.6.0.tar.gz && \
    tar -xzf apache-jena-5.6.0.tar.gz && \
    mv apache-jena-5.6.0 /jena && \
    rm apache-jena-5.6.0.tar.gz

ENV PATH="/jena/bin:${PATH}"

WORKDIR /data

ENTRYPOINT ["shacl"]
