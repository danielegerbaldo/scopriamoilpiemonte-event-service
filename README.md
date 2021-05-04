# scopriamoilpiemonte-event-service
ScopriamoIlPiemonte Event Microservice Repository

## Docker-compose example file
<pre><code>
version: "3.7"
services:
   db-eventi:
      image: postgres:latest
      environment:
          POSTGRES_USER: poi
          POSTGRES_PASSWORD: poi
          POSTGRES_DB: Piemonte      
      volumes:
         - db-eventi:/usr/app/

   pgadmin:
        image: dpage/pgadmin4:4.23
        environment:
            PGADMIN_DEFAULT_EMAIL: admin@pgadmin.com
            PGADMIN_DEFAULT_PASSWORD: password
            PGADMIN_LISTEN_PORT: 6060
        ports:
            - 6060:6060
        volumes:
            - pgadmin:/var/lib/pgadmin
        depends_on:
            - db-eventi
   
   servizio-eventi:
      image: dangerbaldo/scopriamoilpiemonte-event-service:latest
      container_name: servizio-eventi
      expose: 
         - 8080
      ports:
         - 8080:8080
      volumes:
         - servizio-eventi:/usr/app/ 

volumes:
   db-eventi:
   pgadmin:
   servizio-eventi:
</code></pre>
## Installation
<pre><code>How to run it with docker-compose:
$> docker-compose up
</code></pre>
