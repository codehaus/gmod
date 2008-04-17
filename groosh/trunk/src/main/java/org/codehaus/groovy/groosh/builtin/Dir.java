package org.codehaus.groovy.groosh.builtin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.groosh.AbstractBuiltInProcess;
import org.codehaus.groovy.groosh.Aliases;
import org.codehaus.groovy.util.ExecDir;

@Aliases( { "list" })
public class Dir extends AbstractBuiltInProcess {

	private List<File> dirNames = new ArrayList<File>();

	private boolean recursive = false;

	public Dir(List<String> args, Map<String, String> env, ExecDir execDir) {
		if (args != null && !args.isEmpty()) {

			for (String arg : args) {
				if (arg.startsWith("-")) {
					if (arg.contains("R")) {
						recursive = true;
					}
				} else {
					dirNames.add(new File(arg));
				}
			}
		}
		if (dirNames.isEmpty()) {
			dirNames.add(execDir.getDir());
		}

	}

	public Boolean call() throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

		try {
			if (dirNames.size() > 1 || recursive) {
				processSubDirs(dirNames, writer);
			} else {
				File dir = dirNames.get(0);
				if (dir.exists() && dir.isDirectory()) {
					list(dir, writer);
				}
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
		List<File> subDirs = new ArrayList<File>();

		Collections.sort(Arrays.asList(entries));
		for (File entry : entries) {
			if (entry.isDirectory()) {
				subDirs.add(entry);
			}
			writer.write(entry.getName());
			writer.newLine();
			writer.flush();
		}
		if (recursive) {
			writer.newLine();
			writer.flush();
			processSubDirs(subDirs, writer);
		}
	}

	private void processSubDirs(List<File> dirs, BufferedWriter writer)
			throws IOException {
		if (dirs == null || dirs.size() == 0) {
			return;
		}

		for (File file : dirs) {
			writer.write(file.getAbsolutePath());
			writer.write(":");
			writer.newLine();
			writer.flush();
			list(file, writer);
		}

	}

}
