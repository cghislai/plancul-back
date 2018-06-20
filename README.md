# Plancul backend

You will need the following JNDI resources available in your application server in order to deploy the ear:

- `mail/plancul-mail` a mail resource for smtp server access
- `jdbc/plancul-db` a jdbc resource for database access

For instance, on a payara/glassfish server, you could setup something like this:

```xml
<resources>
    <mail-resource host="localhost" description="" from="it@localhost" user="cghislai" jndi-name="mail/plancul-mail" />
    <jdbc-resource pool-name="plancul-db-pool" object-type="system-all" jndi-name="jdbc/plancul-db"/>
    <jdbc-connection-pool is-isolation-level-guaranteed="false" datasource-classname="org.h2.jdbcx.JdbcDataSource" name="plancul-db-pool" res-type="javax.sql.DataSource">
      <property name="URL" value="jdbc:h2:${com.sun.aas.instanceRoot}/lib/databases/plancul-db;AUTO_SERVER=TRUE"></property>
    </jdbc-connection-pool>
</resources>
<servers>
    <server config-ref="server-config" name="server">
      <resource-ref ref="jdbc/plancul-db"></resource-ref>
      <resource-ref ref="mail/plancul-mail"></resource-ref>
   </server>
</servers>
```
Refer to your application server documentation to correctly setup those.

There are some system properties you may want to provide as jcm options as well. They are listed in 
/plancul-ejb/src/main/resources/com/charlyghislain/plancul/configuration/parameters-defaults.properties.




