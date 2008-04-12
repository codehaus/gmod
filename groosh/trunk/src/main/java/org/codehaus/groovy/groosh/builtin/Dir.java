package org.codehaus.groovy.groosh.builtin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.groosh.AbstractBuiltInProcess;
import org.codehaus.groovy.groosh.Aliases;
import org.codehaus.groovy.util.ExecDir;

@Aliases( { "dir", "ls", "list" })
public class Dir extends AbstractBuiltInProcess {

	private String dirName;

	public Dir(List<String> args, Map<String, String> env, ExecDir execDir) {
		this.dirName = args.get(0);
	}

	public Boolean call() throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

		try {
			File dir = new File(dirName);
			if (dir.exists() && dir.isDirectory()) {
				list(dir, writer);
			}
		} finally {
			os.close();
		}
		return true;
	}

	private void list(File dir, BufferedWriter writer) throws IOException {
		File[] entries = dir.listFiles();
		if (entries == null) {
			return;
		}
		for (File entry : entries) {
			if (entry.isDirectory()) {
				writer.newLine();
				writer.write(entry.getAbsolutePath());
				list(entry, writer);
			} else {
				writer.write(entry.getName());
			}
			writer.newLine();
			writer.flush();
		}
	}

}
