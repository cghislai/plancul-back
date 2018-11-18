#!/bin/bash

create-jdbc-connection-pool --datasourceclassname org.mariadb.jdbc.MySQLDataSource --restype javax.sql.DataSource --property User="plancul":DatabaseName="plancul":Password="'db-password'":ServerName="db" plancul-db-pool

create-jdbc-resource --connectionpoolid plancul-db-pool jdbc/plancul-db

create-javamail-resource  --mailhost postfix --fromaddress plancul@charlyghislain.com --mailuser plancul mail/plancul-mail

set-config-secrets-dir --directory=/opt/payara/secrets

set configs.config.server-config.network-config.protocols.protocol.http-listener-2.ssl.cert-nickname=localhost

deploy --force --enabled=true  /opt/payara/deployments/plancul-ear.ear
