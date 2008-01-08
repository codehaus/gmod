= Groosh = 

A Groovy module that provides a shell-like capability for handling 
external processes

== INSTALL ==

For openSUSE there are RPMs available from
http://download.opensuse.org/repositories/home:/eggeral/openSUSE_10.2/
Just install it and you are ready to go.

If you use the binary distribution e.g. (groosh-0.1.1-bin.tar.gz). 
Untar it and copy groosh-0.1.1.jar to your $GROOVY_HOME/lib. 

Try the following script to check if it worked:

def gsh = new com.baulsupp.groovy.groosh.Groosh();
gsh.ls().toStdOut();

Have a look at the examples directory for more examples how to use
groosh 

== BUILDING FROM SOURCE ==

Untar/unzip the source distribution file (e.g. groosh-0.1.1-src.tar.gz)

Groosh uses Maven 2 for building. You have to have Maven 2 installed. 
Just execute mvn in the groosh directory and groosh-0.1.1.jar gets build
as well as the source and binary distribution.

== BUILDING FROM SVN ==

Check out the source from 
http://svn.codehaus.org/groovy-contrib/groosh/trunk/

Building works the same as for the source distribution. 