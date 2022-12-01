# Build step
FROM node:16.13.2-alpine AS build
WORKDIR /app
COPY ./frontend_ta/package.json/ ./
RUN npm install
RUN npm install react-router-dom
COPY ./frontend_ta/ ./
RUN npm run build

# Release step
FROM nginx:1.21.5-alpine AS release
COPY --from=build /app/build/ /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]