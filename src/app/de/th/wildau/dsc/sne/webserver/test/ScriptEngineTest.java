package de.th.wildau.dsc.sne.webserver.test;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptEngineTest {

	public static void main(String[] args) throws ScriptException {

		ScriptEngineManager mgr = new ScriptEngineManager();

		List<ScriptEngineFactory> factories = mgr.getEngineFactories();
		for (ScriptEngineFactory factory : factories) {
			System.out.println("ScriptEngineFactory Info");
			String engName = factory.getEngineName();
			String engVersion = factory.getEngineVersion();
			String langName = factory.getLanguageName();
			String langVersion = factory.getLanguageVersion();
			System.out
					.printf("\tScript Engine: %s (%s)\n", engName, engVersion);
			List<String> engNames = factory.getNames();
			for (String name : engNames) {
				System.out.printf("\tEngine Alias: %s\n", name);
			}
			System.out.printf("\tLanguage: %s (%s)\n", langName, langVersion);
		}

		System.out.println();
		System.out.println("=========");

		ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
		try {
			jsEngine.eval("print('Hello, world!')");
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}

		System.out.println();
		System.out.println("=========");

		// ScriptEngineManager m = new ScriptEngineManager();
		// ScriptEngine phpEngine = m.getEngineByExtension("php");
		// ScriptContext context = phpEngine.getContext();
		// Object php2javaResult = phpEngine.eval(
		// "<?php echo \"hello world\"; ?>", context);
	}
}
