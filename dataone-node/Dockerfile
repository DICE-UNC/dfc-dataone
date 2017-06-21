FROM tomcat:jre8-alpine
LABEL organization="RENCI"
LABEL maintainer="michael_conway@unc.edu"
LABEL description="DFC Member Node"
ADD target/dataone-node.war /usr/local/tomcat/webapps/
ADD runit.sh /
ADD context.xml /usr/local/tomcat/conf
CMD ["/runit.sh"]
#CMD ["sh"]
# build: docker build -t diceunc/dataone:4.2.1.0-SNAPSHOT .


# run:  docker run -i -p 8080:8080 -v /etc/irods-ext:/etc/irods-ext -v /etc/irods-ext/d1plugins:/etc/irods-ext/d1plugins -v /home/mcc/webdavcert:/tmp/cert  --add-host irods420.irodslocal:172.16.250.101 diceunc/dataone:4.2.1.0-SNAPSHOT

# to add addl libs -v /etc/irods-ext/d1libs:/tmp/lib


#  docker run -i -p 8080:8080 -v /home/mcc/external:/tmp/lib -v /etc/irods-ext:/etc/irods-ext -v /home/mcc/d1plugin:/etc/irods-ext/d1plugins -v /home/mcc/webdavcert:/tmp/cert  --add-host irods420.irodslocal:172.16.250.101 diceunc/dataone:4.2.1.0-SNAPSHOT