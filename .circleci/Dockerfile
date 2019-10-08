FROM circleci/clojure:lein-2.7.1

USER root

ENV NODE_VERSION 8
ENV TERM xterm

# Allow installing when the main user is root
ENV npm_config_unsafe_perm true

# AWS CLI needs the PYTHONIOENCODING to handle UTF-8 correctly:
ENV PYTHONIOENCODING=UTF-8


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

# Install postgres-client to controll napotedb within our container
RUN apt-get install -y --no-install-recommends postgresql-client maven

# Install extra packages (for our CI scripts)
RUN apt-get install -y --no-install-recommends sudo zip git gettext-base


# Install awscli bundle
RUN curl "https://s3.amazonaws.com/aws-cli/awscli-bundle.zip" -o "awscli-bundle.zip"&& \
    unzip awscli-bundle.zip && \
    ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws

# Install gtfs to netex converstion shell tool
RUN curl "https://ote-assets.s3.eu-central-1.amazonaws.com/chouette-command.zip" -o "/tmp/chouette-command.zip" && \
    unzip -o /tmp/chouette-command.zip -d /opt/chouette/ && \
    cp -r /opt/chouette/chouette-cmd_3.4.8/* /opt/chouette/ && \
    rm -rf /opt/chouette/chouette-cmd_3.4.8 && \
    rm -f /tmp/chouette-command.zip

# Cleanup
RUN apt-get -y autoremove && \
    apt-get -y autoclean && \
    rm -rf /var/lib/apt/lists/*

RUN google-chrome --version && aws --version && node --version && npm --version

USER circleci

CMD ["/bin/sh"]
