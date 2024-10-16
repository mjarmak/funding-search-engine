### Run in order:
```
docker build -t funding-search-engine:0.1.5 .
docker tag funding-search-engine:0.1.5 jeniustech/funding-search-engine:0.1.5
docker push jeniustech/funding-search-engine:0.1.5

```

```
docker pull jeniustech/funding-search-engine:0.0.6
```

```
sudo adduser --system --group --no-create-home solr
sudo chown -R solr:solr /var/data/data_solr
sudo chmod -R 755 /var/data/data_solr
uid=113(solr) gid=121(solr) groups=121(solr)
```

Copy uid and gid to docker-compose-solr.yml
```
sudo chown 8983:8983 /var/data/data_solr/
sudo chmod -R 755 /var/data/data_solr/**
```
```
sudo ufw ssh enable
sudo ufw allow '5432'
sudo ufw allow '8983'
```

### Start all services
```
docker compose -f docker-compose-portainer.yml up -d
docker compose -f docker-compose-keycloak.yml up -d
docker compose -f docker-compose-postgres.yml up -d
docker compose -f docker-compose-solr.yml up -d
docker compose -f docker-compose-funding-search-engine.yml up -d
```


### Add SSL
```
sudo apt install certbot python3-certbot-nginx -y
```
```
sudo certbot certonly --nginx -d www.innovilyse.com
sudo certbot certonly --nginx -d auth.innovilyse.com
sudo certbot certonly --nginx -d app.innovilyse.com
sudo certbot certonly --nginx -d api.innovilyse.com
```
```
sudo ln -s /etc/nginx/sites-available/innovilyse.com.conf /etc/nginx/sites-enabled/
```


### Restart scrape data for projects
```
truncate table user_organisation_join;
truncate table user_project_join;
truncate table organisation_project_join;
truncate table organisation_contact_info;
delete from long_text where project_id != null;
truncate table location_coordinates CASCADE;
truncate table projects CASCADE;
truncate table organisations CASCADE;
```

### Restart scrape data for organisations
```
truncate table user_organisation_join;
truncate table organisation_project_join;
truncate table organisation_contact_info;
truncate table location_coordinates CASCADE;
truncate table organisations CASCADE;
```


## Test scripts
```
select organisation_id ,count(id) from organisation_contact_info oci group by organisation_id  ;
select vat_number ,count(id) from organisations o  group by vat_number  ;
select reference_id ,count(id) from organisations o  group by reference_id  ;
select name ,count(id) from organisations o  group by name  ;
select short_name ,count(id) from organisations o  group by short_name  ;
select count(id) from organisations o ;
```


````log book view
select
    ud.first_name || ' ' || ud.last_name as user,
    ud.email as email,
  CASE
    WHEN type = 0 THEN 'SEARCH CALL'
    WHEN type = 1 THEN 'EXPORT EXCEL'
    WHEN type = 2 THEN 'EXPORT PDF'
    WHEN type = 3 THEN 'SEARCH PROJECT'
    WHEN type = 4 THEN 'SEARCH PARTNER'
    ELSE 'UNKNOWN' 
  END AS type,
log_text,
lb.created_at as date
from log_book lb
left join user_data ud on lb.user_id = ud.id
where user_id IN (22,26,27,28,19)
````
