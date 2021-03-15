package de.jadr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Date;

import de.jadr.JadrHTTPServer.HTTPClient;

public class HTTPResponse {
	
	private final PrintWriter out;
	private final BufferedOutputStream outData;
	private final HTTPClient client;
	
	private HTTPResponse(HTTPClient client){
		this.out = client.getPrintWriter();
		this.outData = client.getBufferedOutputStream();
		this.client = client;
	}
	
	public static class FileResponse extends HTTPResponse{
		public FileResponse(HTTPClient client,String code, String mime, File f) throws IOException {
			super(client);
			long fileSize = Files.size(f.toPath());
			String type = HTTPUtils.getFileType(JUtils.getFileExtension(f));
			super.out.println("HTTP/1.1 " + code);
			super.out.println("Server: by Jadr");
			super.out.println("Date: " + new Date());
			super.out.println("Content-lenght: " + fileSize);
			super.out.println("Content-type: " + mime);
			super.out.println();
			super.out.flush();

			// HTTP Body
			super.outData.write(JUtils.fileToBytearray(f, fileSize), 0, (int) fileSize);
			super.outData.flush();
			client.close();
		}
	}
	
	public static class StringResponse extends HTTPResponse{
		public StringResponse(HTTPClient client,String html, String returnCode) throws IOException {
			super(client);
			byte[] htmlBytes = html.getBytes();
			String type = HTTPUtils.getFileType("html");
			super.out.println("HTTP/1.1 " + returnCode);
			super.out.println("Server: JadrHTTP");
			super.out.println("Date: " + new Date());
			super.out.println("Content-type: " + type);
			super.out.println("Content-lenght: " + htmlBytes.length);
			super.out.println();

			super.out.write(html);
			super.out.flush();
			client.close();
		}
	}
	

}
