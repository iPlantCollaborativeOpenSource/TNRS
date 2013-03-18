TNRS DEPENDENCIES
=================
The following instructions can be used to install TNRS dependencies on a CentOS 5.8 system such as the iPlant Atmosphere base VM (image ID: emi-F1F122E4). 


**Some links may be unavailable. Please visit the specific developer's home page to locate the required files.**

Instructions are given for the components listed below
1.	Git 1.7.4 or higher
2.	MySQL 5.0.95 or higher
3.	PHP 5.3.3 or higher, including mysql and mbstring
4.	Maven 2.2.1 or higher
5.	Apache Tomcat 7.0.33 or higher
6.	Apache JK Modules 1.2.31 or higher
7.	YAML 0.1.4
8.	Ruby 1.9.3 or higher
9.	Rubygems 1.8.23 or higher

Apache HTTP Server 2.2.3 or higher and Java 1.6 are already available on the VM image.
---

SYSTEM SETUP
------------
a.	  Gain root access

	sudo su -

b.	  Remove graphic features

	yum groupremove 'GNOME Desktop Environment' -y
	
c.	  Upgrade the system

    yum upgrade -y

##1. Git >=1.7.4

	yum install git -y

##2. MySQL >=5.0.95

	yum install mysql-server -y

##3. PHP >=5.3.3

	yum remove php.x86_64 -y
	yum remove php-cli.x86_64 -y
	yum remove php-common.x86_64 -y
	yum install php53 -y

###3.1. PHP mysql extension >=5.3.3

	yum install php53-mysql -y

###3.2. PHP mbstring (multi-byte string handling) extension >=5.3.3

	yum install php53-mbstring -y

##4. Maven >=2.2.1

	wget http://mirrors.ibiblio.org/apache/maven/maven-2/2.2.1/binaries/apache-maven-2.2.1-bin.tar.gz
	tar -xf apache-maven-2.2.1-bin.tar.gz
	mv apache-maven-2.2.1 /opt/
	ln -s /opt/apache-maven-2.2.1/ /opt/maven

	echo "
		export M2_HOME=/opt/maven
		export M2=\$M2_HOME/bin
		export PATH=\$M2:\$PATH" >>/etc/profile.d/maven.sh
	chmod 755 /etc/profile.d/maven.sh
	source /etc/profile.d/maven.sh

*Note: Add a mirror to Maven's setting file ~/.m2/settings.xml*

	cd
	mkdir .m2
	cd .m2
	echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http:// maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd">
		   <mirrors>
    	       <mirror>
                   <id>jboss-public</id>
                   <name>JBoss Public Nexus Repository</name>
                   <url>https://repository.jboss.org/nexus/content/groups/public/</url>
                   <mirrorOf>jboss</mirrorOf>
         	  </mirror>
		   </mirrors>
	</settings>
	' >> settings.xml

##5. Apache Tomcat >=7.0.33
	cd /tmp
	wget http://mirror.cc.columbia.edu/pub/software/apache/tomcat/tomcat-7/v7.0.33/bin/apache-tomcat-7.0.33.tar.gz
	tar -xf apache-tomcat-7.0.33.tar.gz
	mv apache-tomcat-7.0.33 /opt
	ln -s /opt/apache-tomcat-7.0.33 /opt/tomcat

##6. Apache JK Modules >= 1.2.31

	wget http://archive.apache.org/dist/tomcat/tomcat-connectors/jk/binaries/linux/jk-1.2.31/x86_64/mod_jk-1.2.31-httpd-2.2.x.so
	cp mod_jk-1.2.31-httpd-2.2.x.so /etc/httpd/modules/mod_jk.so
	chmod 755 /etc/httpd/modules/mod_jk.so

##7. YAML 0.1.4

	wget http://pyyaml.org/download/libyaml/yaml-0.1.4.tar.gz
	tar xzvf yaml-0.1.4.tar.gz
	cd yaml-0.1.4
	./configure
	make
	make install
	cd ..

##8. Ruby >=1.9.3

	wget http://ftp.ruby-lang.org/pub/ruby/1.9/ruby-1.9.3-p194.tar.gz
	tar -xf ruby-1.9.3-p194.tar.gz
	cd ruby-1.9.3-p194
	./configure
	make
	make test
	make install
	cd ..

##9. Rubygems >=1.8.23

	gem update