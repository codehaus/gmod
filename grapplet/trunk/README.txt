== Building ==

Grapplet uses Maven2 as its build tool, which means that if you want to 
build your own version of Grapplet from source you'll need to have it 
installed.  Follow the instructions at http://maven.apache.org 
Once Maven2 is installed you will also need to install the java-plugin into
your maven repository (but it wouldn't hurt to check at 
http://mvnrepository.org if it is already there). Usually the plugin is
located at $JDK_HOME/jre/lib/plugin.jar

You can install it on your local Maven2 repo with the following command

 mvn install:installFile -DgroupId=com.sun.java-plugin -Dversion=<jdkversion> \
     -Dpackaging=jar -DartifactId=java-plugin \
     -Dfile= $JDK_HOME/jre/lib/plugin.jar

where <jdkversion> is the version number of the selected jdk. Grapplet has
version 1.6.0 configured, if you change version you'll have to update pom.xml

After you have the required dependencies installe, you may generate the package
by typing

   mvn package

Now you'll have to sign grapplet-<version>.jar, copy it and groovy.js to your
webapp or webserver dir. The file src/html/grapplet.html should give you some
pointers in how it should be configured.
The next section will describe the process of self-signing the jar.

== Signing ==

=Signing your Grapplet=

In order to run Groovy on a browser you'll need to sign the applet.
Follow the steps to sign an applet with your own certificate.

1. Create a keystore which will hold the certificate.
I created an external keystore so I wouldn't mess up my personal security
settings while finding out the correct way to do it. All you have to do 
is issue the following command:

   keytool -genkey -keystore groovy -storepass groovy -keypass groovy \
           -alias groovy

2. Trust your own certificate.
Unless you want to spend some bucks on this experiment I recommend you 
selfcert your certificate. To selfcert your newly created certificate, 
issue the following command:

   keytool -selfcert -keystore groovy -storepass groovy -keypass groovy \
           -alias groovy

3. Export your certificate.Export your certificate to an external file with
the following command:

   keytool -export -keystore groovy -storepass groovy -keypass groovy \
           -alias groovy -file groovy.cer

4. Sign the jar. This will attach the certificate to the jar and add entries
to the jar's manifest.

   jarsigner -keystore groovy -storepass groovy -keypass groovy \
             grapplet-0.1.jar groovy

5. Verify your jar (just in case). You may verify that your jar has indeed
been signed and includes the certificate, for more information on 
jarsigner's output refer to the command's help (jarsigner -help):

   jarsigner -verify -verbose -certs -keystore groovy grapplet-0.1.jar

6. Configure your local security settings. For this step you must touch
$JRE_HOME/lib/security/java.policy and $JRE_HOME/lib/security/java.security,
in windows $JRE_HOME usally points to "c:/Program Files/Java/jdk1.x.x/".

   1. Add the following lines at the end of java.policy:
      grant {
         java.lang.RuntimePermission "usePolicy";
      };
   2. Create a file named '.java.policy' at $USER_HOME with the following 
       contents:
       keystore "file:${user.home}/groovy";
       grant signedBy "groovy" {
          permission java.security.AllPermission;
       };
       grant codeBase "http://localhost" {
          permission java.security.AllPermission;
       };
   3. Copy the keystore 'groovy' and 'groovy.cer' (just in case) to $USER_HOME.

7. Copy the binary dist to your webserver. I'm using Apache 2.2 in my laptop,
so I copied grapplet.html, groovy.js, grapplet-0.1.jar, groovy-all-1.0.jar, 
groovy and groovy.cer to $APACHE_HOME/htdocs/grapplet


Have fun!
