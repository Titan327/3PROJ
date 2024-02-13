# Utiliser l'image officielle de Node.js en tant que base
FROM node:14

# Mettre en place le répertoire de travail
WORKDIR /app

# Copier le package.json et le package-lock.json pour installer les dépendances
COPY package*.json ./

# Installer les dépendances
RUN npm install

# Copier le reste des fichiers du projet dans le conteneur
COPY . .

# Installer Cordova de manière globale
RUN npm install -g cordova

# Installer les dépendances Cordova
RUN cordova prepare

# Builder l'application Quasar
RUN quasar build

# Builder l'application Cordova pour Android
RUN cordova platform add android
RUN cordova build android

# Lorsque vous exécutez le conteneur, le fichier APK sera dans le répertoire platforms/android/app/build/outputs/apk/debug/
