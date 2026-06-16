# Etapa 1: Build con Node
FROM node:24 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
RUN chmod +x node_modules/.bin/ng
COPY . .
RUN node node_modules/@angular/cli/bin/ng.js build --configuration production

# Etapa 2: Servir con Nginx
FROM nginx:alpine
COPY --from=build /app/dist/ebook/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80