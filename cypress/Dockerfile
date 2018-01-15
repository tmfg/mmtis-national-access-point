FROM openjdk:8-jdk

USER root

ENV NODE_VERSION 6

# Install cypress deps
RUN apt-get update && \
  apt-get install -y \
    libgtk2.0-0 \
    libnotify-dev \
    libgconf-2-4 \
    libnss3 \
    libxss1 \
    libasound2 \
    xvfb

# install Chrome browser
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google.list && \
    apt-get update && \
    apt-get install -y dbus-x11 google-chrome-stable

# "fake" dbus address to prevent errors
# https://github.com/SeleniumHQ/docker-selenium/issues/87
ENV DBUS_SESSION_BUS_ADDRESS=/dev/null

# Install NodeJS
RUN apt-get install --no-install-recommends -y apt-transport-https  && \
    curl -sL https://deb.nodesource.com/setup_${NODE_VERSION}.x | bash - && \
    apt-get install --no-install-recommends -y nodejs && \
    node -v && npm -v && \
    npm i -g npx

# Install extra packages (for CI scripts)
RUN apt-get install -y sudo zip

# Cleanup
RUN apt-get -y autoremove && \
    apt-get -y autoclean && \
    rm -rf /var/lib/apt/lists/*

# Install cypress
RUN npm i -g cypress@1.x
RUN cypress verify

RUN cypress --version
RUN google-chrome --version

RUN mkdir /cypress-env

VOLUME ["/cypress-env"]
