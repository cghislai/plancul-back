# Plancul

Plan cul is a culture scheduling web application targeted at truck farmers.

## Getting started

### Dependencies

Plancul packages as an ear to deploy on a recent java application server (payar 5.183).

It relies on the following external resources:
- A `jdbc/plancul-db` JNDI resource for database access
- A `jdbc/plancul-astronomy-db` JNDI resource for astronomical event cache database
- A `mail/plancul-mail` JNDI mail resource for smtp server access
- An [authenticator](https://github.com/cghislai/authenticator) service for authentication
- An [almanac](https://github.com/cghislai/almanac) service for astronomical event computation 

Refer to your application server documentation to correctly setup the JNDI resources.

You can also take a look at the provided docker-compose in plancul-stack/

Configuration use the microprofile-config mechanism. Default values are located there: 
/plancul-ejb/src/main/resources/com/charlyghislain/plancul/configuration/parameters-defaults.properties.

Additionally, modules integrating external services may have additional configuration parameters.

### Startup

On deployment, an admin activation token is logged out if no admin account is active. You need it on first run. 
Once the admin account is set up, you can create new user account (or log in with your authenticator account).

