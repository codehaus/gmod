gsh = new groosh.Groosh();

cat = gsh.cat('src/test/resources/blah.txt');
lines = gsh.groovy { is, os -> 
  is.eachLine { line -> 
  	os.write("*".getBytes())
  	os.write(line.getBytes())
  	os.write("\n".getBytes())
	}
};

cat.pipeTo(lines);
lines.toStdOut();