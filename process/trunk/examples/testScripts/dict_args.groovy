gsh = new com.baulsupp.groovy.groosh.Groosh();

if (args.length == 0) {
  System.err.println("please provide a search pattern");
  System.err.println("usage: dict_args querystring");
} else {
  gsh.cat('src/test/resources/words').pipeTo(gsh._grep(args[0])).toStdOut();
}

