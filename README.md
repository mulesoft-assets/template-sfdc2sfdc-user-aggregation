
# Mule Kick: SFDC to SFDC Users report

+ [Use Case](#usecase)
+ [Run it!](#runit)
    * [Running on CloudHub](#runoncloudhub)
    * [Running on premise](#runonopremise)
        * [Properties to be configured](#propertiestobeconfigured)
+ [Customize It!](#customizeit)
    * [config.xml](#configxml)
    * [endpoints.xml](#endpointsxml)
    * [businessLogic.xml](#businesslogicxml)
    * [errorHandling.xml](#errorhandlingxml)


# Use Case <a name="usecase"/>
As a Salesforce admin I want to generate a report comparing users of two Salesforce Instances to see which users can only be found in one of the two and which users are in both instances. 

This Kick (template) should serve as a foundation for extracting data from two systems, comparing values of fields for the objects, and generating a report on the differences. 

As implemented, it gets users from two instances of Salesforce, compares by the email address of the users, and generates a CSV file which shows users in A, users in B, and Users in A and B. The report is then emailed to a configured group of email addresses.

# Run it! <a name="runit"/>

Simple steps to get SFDC to SFDC Users report running 

## Running on CloudHub <a name="runoncloudhub"/>

While [creating your application on CloudHub](http://www.mulesoft.org/documentation/display/current/Hello+World+on+CloudHub) (Or you can do it later as a next step), you need to go to Deployment > Advanced to set all environment variables detailed in **Properties to be configured** as well as the **mule.env**. 

Once your app is all set and started, supposing you choose as domain name `sfdcuserreports` to trigger the use case you just need to hit `http://sfdcuserreports.cloudhub.io/generatereport` and report will be sent to the emails configured.

## Running on premise <a name="runonopremise"/>
Complete all properties in one of the property files, for example in [mule.prod.properties] (../blob/master/src/main/resources/mule.prod.properties) and run your app with the corresponding environment variable to use it. To follow the example, this will be `mule.env=prod`.

After this, to trigger the use case you just need to hit the local http endpoint with the port you configured in your file. If this is, for instance, `9090` then you should hit: `http://localhost:9090/generatereport` and this will create a CSV report and send it to the mails set.

## Properties to be configured (With examples)<a name="propertiestobeconfigured"/>

In order to use this Mule Kick you need to configure properties (Credentials, configurations, etc.) either in properties file or in CloudHub as Environment Variables. Detail list with examples:

### Application configuration
+ http.port `9090` 

#### SalesForce Connector configuration for company A
+ sfdc.a.username `bob.dylan@orga`
+ sfdc.a.password `DylanPassword123`
+ sfdc.a.securityToken `avsfwCUl7apQs56Xq2AKi3X`
+ sfdc.a.url `https://login.salesforce.com/services/Soap/u/26.0`

#### SalesForce Connector configuration for company B
+ sfdc.b.username `joan.baez@orgb`
+ sfdc.b.password `JoanBaez456`
+ sfdc.b.securityToken `ces56arl7apQs56XTddf34X`
+ sfdc.b.url `https://login.salesforce.com/services/Soap/u/26.0`


#### SMPT Services configuration
+ smtp.host `smtp.gmail.com`
+ smtp.port `587`
+ smtp.user `exampleuser@gmail.com`
+ smtp.password `ExamplePassword456`

#### Mail details
+ mail.from `exampleuser@gmail.com`
+ mail.to `woody.guthrie@gmail.com`
+ mail.subject `SFDC Users Report`
+ mail.body `Users report comparing users from SFDC Accounts`
+ attachment.name `OrderedReport.csv`


# Customize It!<a name="customizeit"/>

This brief guide intends to give a high level idea of how this Kick is built and how you can change it according to your needs.
As mule applications are based on XML files, this page will be organized by describing all the XML that conform the Kick.
Of course more files will be found such as Test Classes and [Mule Application Files](http://www.mulesoft.org/documentation/display/current/Application+Format), but to keep it simple we will focus on the XMLs.

Here is a list of the main XML files you'll find in this application:

* [config.xml](#configxml)
* [endpoints.xml](#endpointsxml)
* [businessLogic.xml](#businesslogicxml)
* [errorHandling.xml](#errorhandlingxml)


## config.xml<a name="configxml"/>
Configuration for Connectors and [Properties Place Holders](http://www.mulesoft.org/documentation/display/current/Configuring+Properties) are set in this file. **Even you can change the configuration here, all parameters that can be modified here are in properties file, and this is the recommended place to do it so.** Of course if you want to do core changes to the logic you will probably need to modify this file.

In the visual editor they can be found on the *Global Element* tab.


## endpoints.xml<a name="endpointsxml"/>
This is the file where you will found the inbound and outbound sides of your integration app.
This Kick has an [HTTP Inbound Endpoint](http://www.mulesoft.org/documentation/display/current/HTTP+Endpoint+Reference) as the way to trigger the use case and an [SMTP Transport](http://www.mulesoft.org/documentation/display/current/SMTP+Transport+Reference) as the outbound way to send the report.

###  Inbound Flow
**HTTP Inbound Endpoint** - Start Report Generation
+ `${http.port}` is set as a property to be defined either on a property file or in CloudHub environment variables.
+ The path configured by default is `generatereport` and you are free to change for the one you prefer.
+ The host name for all endpoints in your CloudHub configuration should be defined as `localhost`. CloudHub will then route requests from your application domain URL to the endpoint.

###  Outbound Flow
**SMTP Outbound Endpoint** - Send Mail
+ Both SMTP Server configuration and the actual mail to be sent are defined in this endpoint.
+ This flow is going to be invoked from the flow that does all the functional work: *mainFlow*, the same that is invoked from the Inbound Flow upon triggering of the HTTP Endpoint.


## businessLogic.xml<a name="businesslogicxml"/>
Functional aspect of the kick is implemented on this XML, directed by one flow responsible of conducting the generation of the report.
The *mainFlow* organises the job in three different steps and finally invokes the *outboundFlow* that will deliver the report to the corresponding outbound endpoint.
This flow has Exception Strategy that basically consists on invoking the *defaultChoiseExceptionStrategy* defined in *errorHandling.xml* file.


###  Gather Data Flow
Mainly consisting of two calls (Queries) to SalesForce and storing each response on the Invocation Variable named *usersFromOrgA* or *usersFromOrgA* accordingly.

###  Process Data Flow
[Java Transformer](http://www.mulesoft.org/documentation/display/current/Java+Transformer+Reference) responsible for gathering the results from the two SalesForce Org Users and ordering the results.
Criteria and format applied:
+ Transformer receives a Mule Message with the two Invocation variables *usersFromOrgA* and *usersFromOrgA* to result in List of Maps with keys: **Name**, **Email**, **IDInA**, **UserNameInA**, **IDInB** and 
**UserNameInB**.
+ Users will be matched by mail, that is to say, a record in both SFDC organisations with same mail is considered the same user.

+ Results will have the following order:

1. Users only in Org A
2. Users only in Org B
3. Users in both Org A and Org B

All records ordered alphabetically by mail within each category.
If you want to change this order then the *compare* method should be modified.

###  Format Output Flow
+ CSV Report [DataMapper](http://www.mulesoft.org/documentation/display/current/Datamapper+User+Guide+and+Reference) transforming the List of Maps in CSV with headers **Name**, **Email**, **IDInA**, **UserNameInA**, **IDInB** and **UserNameInB**.
+ An [Object to string transformer](http://www.mulesoft.org/documentation/display/current/Transformers) is used to set the payload as an String. 


## errorHandling.xml<a name="errorhandlingxml"/>
Contains a [Catch Exception Strategy](http://www.mulesoft.org/documentation/display/current/Catch+Exception+Strategy) that is only Logging the exception thrown (If so). As you imagine, this is the right place to handle how your integration will react depending on the different exceptions. 
