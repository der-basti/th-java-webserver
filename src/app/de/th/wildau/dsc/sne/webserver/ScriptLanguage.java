package de.th.wildau.dsc.sne.webserver;

/**
 * TODO javadoc
 * 
 * @author sne
 * 
 */
public enum ScriptLanguage {

	PHP("", ".php"), PERL("", ".pl"), PYTHON("", ".py"), RUBY("", ".rb");

	private String executeComand;
	private String fileExtension;

	private ScriptLanguage(String executeCommand, String fileExtension) {
		this.executeComand = executeCommand;
		this.fileExtension = fileExtension;
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
