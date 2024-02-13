FROM debian

RUN apt-get update

RUN apt-get install -y nodejs npm
#RUN apt-get install -y wget
#RUN apt-get install -y gradle
#RUN apt-get install -y openjdk-17-jdk

#RUN wget https://redirector.gvt1.com/edgedl/android/studio/ide-zips/2023.3.1.4/android-studio-2023.3.1.4-linux.tar.gz

#RUN tar -xvzf android-studio-2023.3.1.4-linux.tar.gz

#ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64/
#ENV PATH $PATH:$JAVA_HOME/bin

#ENV ANDROID_HOME /android-studio
#ENV PATH $PATH:$ANDROID_HOME/bin

#RUN npm install -g @quasar/cli@2.3.0
RUN npm install -g cordova

WORKDIR /app

COPY Quasar .

RUN npm install

RUN npx cordova create src-cordova

WORKDIR /app/src-cordova

RUN npx cordova platform add android

WORKDIR /app

RUN quasar build -m cordova -T android
