#Q-Book
===================

Booking Solution For Service Providers, build with SpringBoot, Mongo, Javascript, CSS, HTML5

##BUILD APP LOCALLY
mvn clean package -Dmaven.test.skip=true

##SSH Onto Server
- ssh -i ~/.ssh/q-book-demo.pem ubuntu@ec2-13-244-115-200.af-south-1.compute.amazonaws.com

##KILL RUNNING APP
- ps -ef | grep java | grep -v javaStub | grep -v swapback | grep -v grep | awk '{ print $2 }' | xargs kill -9

##REMOVE OR BACKUP EXISTING JAR
- rm ROOT.war

##UPLOAD NEW APP FROM LOCAL MACHINE
- scp -i ~/.ssh/q-book-demo.pem target/ROOT.war ubuntu@ec2-13-244-115-200.af-south-1.compute.amazonaws.com:/home/ubuntu/ROOT.war

##RUN IT
- nohup java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=production -jar ROOT.war > app.log &
- tail -f app.log
