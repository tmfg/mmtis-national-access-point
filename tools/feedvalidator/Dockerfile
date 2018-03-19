FROM debian:latest

USER root

RUN apt-get update && \
    apt-get install -y \
    python \
    python-pip \
    git


RUN git clone https://github.com/google/transitfeed.git

RUN cd transitfeed && pip install transitfeed

RUN sed -i "s|/usr/bin/python2.5|/usr/bin/python|g" transitfeed/feedvalidator.py

RUN apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["/transitfeed/feedvalidator.py", "-o", "/data/output.html", "/data/gtfs.zip"]