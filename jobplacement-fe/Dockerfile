# Stage 1: Build
FROM node:20.17.0-alpine AS build
LABEL mantainer="Group 3"
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# Stage 2: Serve the built files
FROM nginx:stable-alpine
EXPOSE 80
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
CMD ["nginx", "-g", "daemon off;"]
