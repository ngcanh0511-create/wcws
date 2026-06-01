FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY docker-entrypoint.sh /docker-entrypoint.d/40-wcpl-config.sh
COPY . /usr/share/nginx/html

RUN chmod +x /docker-entrypoint.d/40-wcpl-config.sh

EXPOSE 8080
