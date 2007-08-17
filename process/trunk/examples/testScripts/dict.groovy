gsh = new com.baulsupp.groovy.groosh.Groosh();

gsh.cat('src/test/resources/words').pipeTo(gsh._grep('lexia')).toStdOut();


