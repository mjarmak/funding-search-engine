# innovilyse-keycloak-theme

## Development

### Refresh Theme

#### Locally

Delete /opt/jboss/keycloak/standalone/tmp to refresh theme

#### Remotely
docker exec -t -i jenius_auth /bin/bash
cd opt/jboss/keycloak/standalone
rm -r tmp
