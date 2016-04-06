OpenAM Sample Modules
====================================
OpenAM Sample Modules
OpenAM version: 12.0.X

Build:

Run the following command:

mvn clean package

You can find the distribution zip file (openam-auth-sample-modules-dist.zip) inside the target folder

Installation:

* Copy the distribution zip file to your OpenAM server and extract contents to a directory of your choice.

* Copy jar files (openam-auth-sample-modules/lib/) to WEB-INF/lib
    $ cp openam-auth-sample-modules/lib/* $OPENAM_ROOT/WEB-INF/lib
    
* Copy properties file(s):
    $ cp openam-auth-sample-modules/config/amAuth<MODULE_NAME>.properties $OPENAM_ROOT/WEB-INF/classes

* Copy module descriptor(s):
    $ cp openam-auth-sample-modules/config/<MODULE_NAME>y.xml $OPENAM_ROOT/config/auth/default
    
* Register service(s)
    $ ssoadm create-svc -u amadmin -f /tmp/admin.pwd --xmlfile amAuth<MODULE_NAME>.xml

* Register module(s)
    $ ssoadm register-auth-module -u amadmin -f /tmp/admin.pwd --authmodule com.groman.openam.auth.<MODULE_NAME>
    
* Create module instance(s)
    $ ssoadm create-auth-instance -u amadmin -f /tmp/admin.pwd --authtype <MODULE_NAME> --name <MODULE_NAME> --realm <REALM_NAME>
    
* Update module properties Access Control > <REALM_NAME> > Authentication > Module Instances > <MODULE_NAME>
