# SFDC2SFDC-UsersReport Mule Kick

Use case: As a admin I want to see a report of SFDC users that belong to each organization to be able to detect inconsistencies between users permissions between organizations.

## Triggering use case

In order to generate the report you need to hit the http endpoint you configure with **/generatereport** path.

## Configuration

In order to use this Mule Kick you need to configure properties (Credentials, configurations, etc.) either in properties file or in CloudHub as Environment Variables.

#### Application configuration
* http.port

#### SalesForce Connector configuration for company A
* sfdc.a.username
* sfdc.a.password
* sfdc.a.securityToken
* sfdc.a.url

#### SalesForce Connector configuration for company A
* sfdc.b.username
* sfdc.b.password
* sfdc.b.securityToken
* sfdc.b.url

#### SMPT Services configuration
* smtp.host
* smtp.port
* smtp.user
* smtp.password

#### Mail details
* mail.from
* mail.to
* mail.subject
* mail.body
* attachment.name

