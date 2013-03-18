
INSTALLING TNRS
===============

Installing your own instance of TNRS will allow you to add your own name sources and run the service on a local network. This is helpful if the organisms for which the names need to be resolved are not covered by the online TNRS.

The following dependencies are required:
*   Java 1.6.0 or higher
*   Git 1.7.4 or higher
*   MySQL 5.0.95 or higher
*   PHP 5.3.3 or higher, including 
    * mysql
    * mbstring
*   Maven 2.2.1 or higher
*   Apache Tomcat 7.0.33 or higher
*   Apache HTTP Server 2.2.3 or higher
*   Apache JK Modules 1.2.31 or higher
*   YAML 0.1.4
*   Ruby 1.9.3 or higher
*   Rubygems 1.8.23 or higher

Instructions for installing dependencies are available in DEPENDENCIES.

To install the TNRS application, follow the steps below.

    1. Obtain the TNRS application
    2. Set up the TNRS database
    3. Install the GNI Parser
    4. Install Taxamatch
    5. Install the communication service layer
    6. Install the TNRS batch service
    7. Deploy the frontend
    8. Configure Tomcat
    9. Register the services
    10. Start the TNRS application
    11. Set up cron monitoring (optional)

1. OBTAIN THE TNRS APPLICATION
------------------------------
*Note: Different Linux distributions use different system paths. These instructions have been tested on a CentOS 5.8 VM, such as the iPlant Atmosphere base VM (image ID: emi-F1F122E4) and different paths might need to be used in other systems.*

    sudo su -
    cd
    git clone https://github.com/nmatasci/TNRS.git
    cd TNRS

1.1.    Create the TNRS system user.

    useradd tnrs -U -c 'TNRS services'
    
1.2.    Create the storage directory.

    mkdir /tnrs-jobs
    chown tnrs:tnrs /tnrs-jobs

*Note: Do not modify the name and location of the storage directory.*

1.3.    Create the logs directory.

    mkdir /var/run/tnrs
    chown tnrs:tnrs /var/run/tnrs

1.4.    Create the configuration directory.

    mkdir /home/tnrs/.tnrs
    cp ~/TNRS/config/tnrs.properties /home/tnrs/.tnrs/
    chown -R tnrs:tnrs /home/tnrs/.tnrs

*Note: Do not modify the name and location of the configuration directory.*

1.5.    Configure TNRS.

Manually edit /home/tnrs/.tnrs/tnrs.properties to enter the address of your server. This will need to be edited throughout the properties file.

1.6.    Link the configuration directory into the home directory of the tomcat process owner, in this case root.

    ln -s  /home/tnrs/.tnrs /root
    chown -R root:root /root/.tnrs


2. SET UP THE TNRS DATABASE
---------------------------
Note: All data source names must be lowercase (e.g. tropicos_example).

2.1.   Start the MySQL service.

    service mysqld start

2.2.  Log in to the mysql database.

    mysql -D mysql

*Note: you are now in the MySQL console.*

2.3.   Create a temporary privileged user. 

    CREATE USER 'tnrs_root'@'localhost' IDENTIFIED BY 'rooter';
    GRANT ALL PRIVILEGES ON *.* TO 'tnrs_root'@'localhost';
    EXIT;

*Note: The user credentials must match those set in the ~/TNRS/tnrs3_db_scripts/global_params.inc file. Changing them is not necessary or recommended.*

2.4.   Run the loading script (answer yes to all questions)
*Note: You are now back in the system console.*

    cd ~/TNRS/tnrs3_db_scripts
    php load_tnrs.php

2.5.   Log in to the mysql database.

    mysql -D mysql

*Note: you are now in the MySQL console.*

2.6.   Remove the temporary privileged MySQL user.

    DROP USER 'tnrs_root'@'localhost';

2.7.   Create the persistent TNRS database user. 

    CREATE USER 'tnrs'@'localhost' IDENTIFIED BY 'tnrs';
    GRANT SELECT ON tnrs_test.* TO 'tnrs'@'localhost';
    SET GLOBAL read_only=1;
    EXIT;

*Note: To use different credentials from the default settings (optional but recommended for production environments), modify the configuration file for Taxamatch (~/TNRS/taxamatch-webservice-read-only/api/config.php) to match the credentials of the MySQL persistent user.*


3. INSTALL THE GNI PARSER
-------------------------
3.1.   Obtain the GNI parser.

    cd /opt
    git clone git://github.com/GlobalNamesArchitecture/biodiversity.git

3.2.   Replace the file scientific_name_clean.treetop with the iPlant custom version.

    cd biodiversity/lib/biodiversity/parser/
    mv scientific_name_clean.treetop scientific_name_clean.treetop.original
    wget http://svn.iplantcollaborative.org/iptol/data_integration/biodiversity_1.0.9/parser/scientific_name_clean.treetop

3.3.   Install the GNI parser.

    gem install biodiversity19

3.4.   Copy the startup script into the GNI parser directory.

    cp ~/TNRS/scripts/parserver_start.sh /opt/biodiversity
    chown -R tnrs:tnrs /opt/biodiversity

3.5.   Set the environmental variables.

    echo "
    export LC_CTYPE=en_US.UTF-8
    export LANG=en_US.UTF-8
    unset LC_ALL
    " > /etc/profile.d/parserver_lang.sh
    chmod 755 /etc/profile.d/parserver_lang.sh
    source /etc/profile.d/parserver_lang.sh


4. INSTALL TAXAMATCH
--------------------

    cp -r ~/TNRS/taxamatch-webservice-read-only /var/www/html/
    chown -R tnrs:tnrs /var/www/html/taxamatch-webservice-read-only/


5. INSTALL THE COMMUNICATION SERVICE LAYER
------------------------------------------

5.1.   Use Apache Maven to build the service.

    cd ~/TNRS/tnrs-services
    mvn clean compile package
    mvn dependency:copy-dependencies
    cd target
    cp dependency/* .

5.2.   Create the tnrs-services directory.

    mkdir /opt/tnrs-services

5.3.   Copy the files into the tnrs-services directory.

    cp *.jar /opt/tnrs-services

5.4.   Copy the configuration file into the tnrs-services directory.

    cp classes/mule-config.xml /opt/tnrs-services

5.5.   Copy the startup file into the tnrs-services directory.

    cp ~/TNRS/scripts/services_start.sh /opt/tnrs-services
	chmod 755 /opt/tnrs-services/services_start.sh

5.6.   Change ownership of the tnrs-services directory to the tnrs user.

    chown -R tnrs:tnrs /opt/tnrs-services


6. INSTALL THE TNRS BATCH SERVICE
---------------------------------
6.1.   Use Apache Maven to build the service.

    cd ~/TNRS/tnrs-batch-server/
    mvn clean compile package
    mvn dependency:copy-dependencies
    cd target
    cp dependency/* .

6.2.   Create the tnrs-batch directory.

    mkdir /opt/tnrs-batch

6.3.   Copy the files into the tnrs-batch directory.

    cp *.jar /opt/tnrs-batch

6.4.   Copy the startup file into the tnrs-batch directory.

    cp ~/TNRS/scripts/batch_start.sh /opt/tnrs-batch
    chmod 755 /opt/tnrs-batch/batch_start.sh

6.5.   Change ownership of the tnrs-batch directory to the tnrs user.

    chown -R tnrs:tnrs /opt/tnrs-batch


7. DEPLOY THE WEB FRONTEND
--------------------------
7.1.   Use Apache Maven to build and package the frontend into a WAR file.

    cd ~/TNRS/tnrs-standalone
    mvn clean compile package

7.2.  Copy the WAR file into the tomcat/webapps directory.

    cp target/tnrs-standalone.war /opt/tomcat/webapps

7.3.   Set the TNRS frontend as the root of the website.

    cd /opt/tomcat/webapps
    mv ROOT ROOT~
    ln -s tnrs-standalone ROOT


8. CONFIGURE TOMCAT
-------------------
8.1.   Configure Tomcat.
Manually add the following lines to /opt/tomcat/conf/catalina.policy.

    grant codeBase "file:${catalina.base}/webapps/" {     
        permission java.net.SocketPermission;
    };

8.2.   Configure the httpd server.
Manually edit the httpd configuration file /etc/httpd/conf/httpd.conf
* Set **KeepAlive** to **On**
* Add the JK Modules after the Dynamic Shared Object (DSO) Support section:

    #Load mod_jk module
    LoadModule  jk_module  modules/mod_jk.so
    
    #Where to find workers.properties
    JkWorkersFile /etc/httpd/conf/workers.properties

    #Where to put jk shared memory
    JkShmFile   /var/log/httpd/mod_jk.shm

    #Where to put jk logs
    JkLogFile   /var/log/httpd/mod_jk.log

    #Set the jk log level [debug/error/info]
    JkLogLevel  error
    
    #Select the timestamp log format
    JkLogStampFormat "[%a %b %d %H:%M:%S %Y] "

8.3.   Copy the configuration files for tomcat and for the virtual hosts.

    cp ~/TNRS/config/workers.properties /etc/httpd/conf/
    cp ~/TNRS/config/_vhosts.conf  /etc/httpd/conf.d/


9. REGISTER THE SERVICES
------------------------

    cp ~/TNRS/scripts/services/tnrs* /etc/init.d/
    chkconfig --add tnrs-services
    chkconfig --add tnrs-batch
    chkconfig --add tnrs-parserver


10. START THE TNRS APPLICATION
------------------------------

    /opt/tomcat/bin/startup.sh
    service tnrs-batch start
    service tnrs-services start
    service tnrs-parserver start
    service httpd start
    

11. SET UP CRON MONITORING (OPTIONAL, REQUIRES VIXIE-CRON)
----------------------------------------------------------

    [yum install -y vixie-cron]
    cp ~/TNRS/scripts/tnrs-warn /etc/cron.daily/
    cp ~/TNRS/scripts/tnrs-status /etc/
    cd /etc

 Add the following line to crontab (and change the email address, if necessary):

    15 * * * * root /etc/tnrs-status 1>/dev/null 2>> /var/log/tnrs_status.log

Start the cron service

    service crond start