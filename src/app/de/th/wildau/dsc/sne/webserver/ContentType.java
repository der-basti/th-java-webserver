package de.th.wildau.dsc.sne.webserver;

public enum ContentType {

	TEXT_PLAIN("text/plain"),

	// text/html | application/xhtml+xml | application/xml | text/xml
	TEXT_HTML("text/html"),

	IMAGE_JPEG("image/jpeg"),

	IMAGE_GIF("image/gif"),

	IMAGE_PNG("image/png"),

	APPLICATION_GZIP("application/x-zip-compressed"),

	APPLICATION_PDF("application/pdf");

	private final String contentType;
	private String contentDisposition;

	private ContentType(String contentType) {

		this(contentType, null);
	}

	private ContentType(String contentType, String contentDisposition) {

		this.contentType = contentType;
		this.contentDisposition = contentDisposition;
	}

	protected String getContentDisposition() {
		return contentDisposition;
	}

	protected void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	protected String getContentType() {
		return contentType;
	}
}
