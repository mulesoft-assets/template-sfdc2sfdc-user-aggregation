## Use Case
As a Salesforce admin I want to generate a report comparing users of two Salesforce Instances to see which users can only be found in one of the two and which users are in both instances. 

This Kick (template) should serve as a foundation for extracting data from two systems, comparing values of fields for the objects, and generating a report on the differences. 

As implemented, it gets users from two instances of Salesforce, compares by the email address of the users, and generates a CSV file which shows users in A, users in B, and Users in A and B. The report is then emailed to a configured group of email addresses.

## Run it!

Simple steps to get SFDC to SFDC Users report running [here] (https://github.com/mulesoft/sfdc2sfdc-usersreport/wiki/Run-SFDC2SFDC-UsersReport-Mule-Kick!).

## Details

Having an idea on how this Kick is built and how can you customise it can be found [here] (https://github.com/mulesoft/sfdc2sfdc-usersreport/wiki/How-this-kick-was-built.-Customise-it!).
