gsh = new com.baulsupp.groovy.groosh.Groosh();

s = gsh.cat('src/test/resources/blah.txt').pipeTo(gsh._grep('a')).toStringOut();

System.out.println('->' + s.toString() + '<-');

