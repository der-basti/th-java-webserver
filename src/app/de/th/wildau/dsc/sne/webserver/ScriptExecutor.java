package de.th.wildau.dsc.sne.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author sne
 * 
 */
public class ScriptExecutor {
	
	public static void main(String[] args) {
		
		List<ScriptLanguage> list = ScriptExecutor.getSupportedScriptLanguages();
		System.out.println(Arrays.toString(list.toArray()));
		ScriptExecutor se = new ScriptExecutor();
		System.out.println(se.execute(list.get(0), new File("/Users/sne/Public/hello.php")));
	}

	public enum ScriptLanguage {
		PHP("", "php"), PERL("", "pl"), PYTHON("", "py"), RUBY("", "rb");
		private String executeComand;
		private String fileExtension;

		private ScriptLanguage(String executeCommand, String fileExtension) {
			this.executeComand = executeCommand;
		}

		@Override
		public String toString() {
			return this.name() + " (" + this.executeComand + ")";
		}

		public String getExecuteComand() {
			return this.executeComand;
		}

		public void setExecuteComand(String executeComand) {
			this.executeComand = executeComand;
		}

		public String getFileExtension() {
			return this.fileExtension;
		}
	}

	/**
	 * Check which scripting languages are supported on the current maschine.
	 * 
	 * @return list of {@link ScriptLanguages}
	 */
	protected static List<ScriptLanguage> getSupportedScriptLanguages() {

		ScriptExecutor se = new ScriptExecutor();
		List<ScriptLanguage> scriptLanguages = new ArrayList<ScriptExecutor.ScriptLanguage>();

		// php
		if (!se.execute("php -version").trim().isEmpty()) {
			ScriptLanguage sl = ScriptLanguage.PHP;
			sl.setExecuteComand("php");
			scriptLanguages.add(sl);
		} else if (!se.execute("/usr/bin/php -version").trim().isEmpty()) {
			ScriptLanguage sl = ScriptLanguage.PHP;
			sl.setExecuteComand("/usr/bin/php");
			scriptLanguages.add(sl);
		}

		// perl
		if (!se.execute("perl -version").trim().isEmpty()) {
			ScriptLanguage sl = ScriptLanguage.PERL;
			sl.setExecuteComand("perl");
			scriptLanguages.add(sl);
		} else if (!se.execute("/usr/bin/perl -version").trim().isEmpty()) {
			ScriptLanguage sl = ScriptLanguage.PERL;
			sl.setExecuteComand("/usr/bin/perl");
			scriptLanguages.add(sl);
		}

//		// python
//		if (!se.execute("python").trim().isEmpty()) {
//			ScriptLanguage sl = ScriptLanguage.PYTHON;
//			sl.setExecuteComand("python");
//			scriptLanguages.add(sl);
//		} else if (!se.execute("/usr/bin/python").trim().isEmpty()) {
//			ScriptLanguage sl = ScriptLanguage.PYTHON;
//			sl.setExecuteComand("/usr/bin/python");
//			scriptLanguages.add(sl);
//		}

		// ruby
		if (!se.execute("ruby -version").trim().isEmpty()) {
			ScriptLanguage sl = ScriptLanguage.RUBY;
			sl.setExecuteComand("ruby");
			scriptLanguages.add(sl);
		} else if (!se.execute("/usr/bin/ruby -version").trim().isEmpty()) {
			ScriptLanguage sl = ScriptLanguage.RUBY;
			sl.setExecuteComand("/usr/bin/ruby");
			scriptLanguages.add(sl);
		}

		return Collections.unmodifiableList(scriptLanguages);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param command
	 * @return
	 */
	public String execute(ScriptLanguage scriptLanguage, File file) {

		// TODO [dsc] check file
		return execute(scriptLanguage.executeComand + " " + file.toString());
	}

	/**
	 * TODO javadoc
	 */
	private String execute(String command) {

		System.out.println(command);
		
		Process process;
		try {
			process = Runtime.getRuntime().exec(command);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			// PrintStream out = new PrintStream(process.getOutputStream());
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (IOException ex) {
			Log.error("Script execution failed.", ex);
		}
		return null;
	}
}
