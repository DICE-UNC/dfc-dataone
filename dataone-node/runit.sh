#!/bin/sh

if [ -f /tmp/cert/server.crt ];
then
   echo "Cert will be imported"
   keytool -delete -noprompt -alias mycert -keystore /usr/lib/jvm/default-jvm/jre/lib/security/cacerts -storepass changeit
   keytool -import -trustcacerts -keystore /usr/lib/jvm/default-jvm/jre/lib/security/cacerts -storepass changeit -noprompt -alias mycert -file /tmp/cert/server.crt
else
   echo "No cert to import"
fi

echo "augmenting libs"

if [ -d /tmp/lib ];
then
echo "additional libs found in /tmp/lib"
	cp -R /tmp/lib /usr/local/tomcat
else
 echo "No libs to import"
fi


echo "running catalina"

catalina.sh run

