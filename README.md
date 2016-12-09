# Multithreading-Java-Servlet
Code example of a multi-threading Java servlet.

This is an Eclipse project that is set as a Gradle Project inside Eclipse, requiring the Eclipse Gradle Plugin. 

This project requires Java 8. All other dependencies are downloaded by Gradle.

This application runs in an embedded Jetty server. 

To launch the Jetty server and deploy the application:
  Run the Gradle build with the "jettyRunWar" task.
  
Also a War file is included at zipservlet\build\libs: zipservlet-1.0.war

To submit a file to the server use this syntax:
curl -F file=@testFiles.zip http://localhost:8080/zipservlet/zippost
