package de.jadr;

import java.util.HashMap;
import java.util.StringTokenizer;

public class HTTPUtils {

	private static HashMap<String, String> fileTypes;

	public static String getFileType(String ending) {
		if (fileTypes == null)
			fillFileTypes();

		if (fileTypes.containsKey(ending)) {
			return fileTypes.get(ending);
		}
		return "application/octet-stream";
	}
	
	public static String defaultHTML(String title, String body) {
		return "<!DOCTYPE html><html><head><title>"+title+"</title></head><body>"+body+"</body></html>";
	}

	public static String optimizeHTML(String html) {
		html = html.replaceAll("\\s{2,}","");
		if(true)return html;
		char space = 9;
		char tab = 32;
		boolean isInString = false;
		boolean isEndTag = false;
		boolean isInTag = false;
		char[] chars = html.toCharArray();
		StringBuilder builder = new StringBuilder(chars.length);
		for (char c : chars) {
			if ((c == space || c == tab) && !isInString && !isInTag) {
				continue;
			} else if (c == '"' || c == '\'') {
				if (isInString) {
					isInString = false;
				} else {
					isInString = true;
				}
			}
			else if (c == '<') {
				isInTag = true;
			}
			else if (c == '>') {
				isEndTag = false;
				isInTag = false;
			}
			builder.append(c);

		}
		return builder.toString();
	}

	private static void fillFileTypes() {
		fileTypes = new HashMap<>();
		fileTypes.put("css", "text/css");
		fileTypes.put("htm", "text/html");
		fileTypes.put("html", "text/html");
		fileTypes.put("jar", "application/jar");

		fileTypes.put("txt", "text/plain");
		fileTypes.put("json", "text/json");
		fileTypes.put("js", "application/javascript");
		fileTypes.put("jpg", "image/jpeg");
		fileTypes.put("jpeg", "image/jpeg");
		fileTypes.put("png", "image/png");
		fileTypes.put("svg", "image/svg+xml");
		fileTypes.put("asc", "text/plain");
		fileTypes.put("gif", "image/gif");

		fileTypes.put("mp3", "audio/mpeg");
		fileTypes.put("m3u", "audio/mpeg-url");
		fileTypes.put("mp4", "video/mp4");
		fileTypes.put("ogv", "video/ogg");
		fileTypes.put("flv", "video/x-flv");
		fileTypes.put("mov", "video/quicktime");
		fileTypes.put("swf", "application/x-shockwave-flash");
		fileTypes.put("xml", "text/xml");
		fileTypes.put("pdf", "application/pdf");
		fileTypes.put("doc", "application/msword");
		fileTypes.put("ogg", "application/x-ogg");
		fileTypes.put("zip", "application/octet-stream");
		fileTypes.put("exe", "application/octet-stream");
		fileTypes.put("class", "application/octet-stream");

	}
	
	public static final class HTTPCodes{
		public static final String HTTP_OK = "200 OK", HTTP_PARTIALCONTENT = "206 Partial Content",
				HTTP_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable", HTTP_REDIRECT = "301 Moved Permanently",
				HTTP_NOTMODIFIED = "304 Not Modified", HTTP_FORBIDDEN = "403 Forbidden", HTTP_NOTFOUND = "404 Not Found",
				HTTP_BADREQUEST = "400 Bad Request", HTTP_INTERNALERROR = "500 Internal Server Error",
				HTTP_NOTIMPLEMENTED = "501 Not Implemented";
	}
	
	public static final class MIMECodes{
		public static final String MIME_PLAINTEXT = "text/plain", MIME_HTML = "text/html",
				MIME_DEFAULT_BINARY = "application/octet-stream", MIME_XML = "text/xml";
	}
}
