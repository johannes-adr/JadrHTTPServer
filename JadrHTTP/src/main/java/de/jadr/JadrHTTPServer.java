package de.jadr;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.jadr.HTTPResponse.StringResponse;

public abstract class JadrHTTPServer {

	public static final File ROOT = new File("HTTP");

	private final ArrayList<HTTPClient> CLIENTS = new ArrayList<HTTPClient>();
	private final Executor EXECUTOR;
	private final int PORT;

	private boolean running = true, consolelog = false, logging = true;
	protected ServerSocket server;
	

	/**
	 * 
	 * @param port
	 * @throws IOException
	 */
	public JadrHTTPServer(int port) {
		this.PORT = port;
		this.EXECUTOR = Executors.newCachedThreadPool();
	}

	public JadrHTTPServer(int port, Executor e) {
		this.PORT = port;
		this.EXECUTOR = e;
	}
	
	public JadrHTTPServer doLogging(boolean logging) {
		this.logging = logging;
		return this;
	}
	
	public JadrHTTPServer enableConsoleLog() {
		consolelog = true;
		return this;
	}
	
	public boolean isLogging() {
		return logging;
	}

	/**
	 * Creates a JadrHTTP instace with default port (80)
	 */
	public JadrHTTPServer() {
		this(80);
	}

	public final Executor getExecutor() {
		return EXECUTOR;
	}
	
	public final int getPort() {
		return PORT;
	}
	
	private void log(String s) {
		if(consolelog)System.out.println(JUtils.ANSI_RED + s);
		if(isLogging()) {
			//Do logging
		}
	}
	
	public void startAsync() {
		EXECUTOR.execute(()->{
			try {
				start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void start() throws IOException {
		if (!ROOT.exists())
			ROOT.mkdir();
		server = new ServerSocket(PORT);
		System.out.println("Server started!");
		while (running) {
			Socket s;
			try {
				s = server.accept();
				EXECUTOR.execute(() -> {
					HTTPClient client = null;
					try {
						client = new HTTPClient(s);
						CLIENTS.add(client);
					} catch (IOException e) {
						e.printStackTrace();
						if(client != null) {
							
							log("Error with client " + client.s.getRemoteSocketAddress() + " - " + e.getMessage());
							client.close();
						}
					}
				});
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void stop() throws IOException {
		running = false;
		for (HTTPClient httpClient : CLIENTS) {
			httpClient.close();
		}
		CLIENTS.clear();
		server.close();
		if (EXECUTOR instanceof ExecutorService) {
			((ExecutorService) EXECUTOR).shutdown();
		}
	}
	
	public abstract void onClientConnect();
	public abstract void onClientDisconnect();

	
	

	public class HTTPClient {
		private final BufferedReader in;
		private final PrintWriter out;
		private final BufferedOutputStream outData;
		private final Socket s;
		private final Map<String, String[]> headerfields = new HashMap<>();

		

		
		
		
		private HTTPClient(Socket s) throws IOException{
			this.s = s;
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(s.getOutputStream());
			outData = new BufferedOutputStream(s.getOutputStream());
			String start = in.readLine();
			
			String line = null;
			while((line = in.readLine()) != null) {
				String[] field = line.split(":");
				System.out.println(Arrays.toString(field));
				if(field.length > 1) {
					ArrayList<String> args = new ArrayList<>();
					for (String arg : field[1].split(",")) {
						args.add(arg);
					}
					headerfields.put(field[0], args.toArray(new String[args.size()]));
				}else {
					break;
				}
			}

			StringTokenizer st = new StringTokenizer(start);
			String method = st.nextToken();
			String[] requestURL = st.nextToken().split("\\?");
			String httptype = st.nextToken();

			
			String fileSubURL = requestURL[0];
			File requestedFile = getFileFromSubUrl(fileSubURL);
			
			if (requestedFile.isDirectory()) {
				for (File f : requestedFile.listFiles()) {
					if (f.getName().equalsIgnoreCase("index.html")) {
						requestedFile = f;
						break;
					}
				}
			}
			answer(requestedFile);
			close();
		}
		
		protected HTTPResponse answer(File requestedFile) throws IOException{
			if (!requestedFile.exists()) {
				return new HTTPResponse.StringResponse(this,
				HTTPUtils.defaultHTML
					(
					"404 Not found",
					"The requested file <b>" + requestedFile.getName() + "</b> dont exists"
					),
				HTTPUtils.HTTPCodes.HTTP_NOTFOUND);
			}
			if(requestedFile.isDirectory()) {
				return new HTTPResponse.StringResponse(this, getFolderContentHTML(requestedFile, "Folder: " + requestedFile.getName()),HTTPUtils.HTTPCodes.HTTP_OK);
			}

			
			String fileEnd = JUtils.getFileExtension(requestedFile);
			if (fileEnd.equals("html")) {
				return new HTTPResponse.FileResponse(this, HTTPUtils.HTTPCodes.HTTP_OK, HTTPUtils.MIMECodes.MIME_HTML, requestedFile);
			} else {
				return new HTTPResponse.FileResponse(this, HTTPUtils.HTTPCodes.HTTP_OK, HTTPUtils.getFileType(fileEnd), requestedFile);
			}
		}
		
		public String getFolderContentHTML(File folder, String title) {
			StringBuilder content = new StringBuilder();
			content.append("<!DOCTYPE html><html><head><title>"+title+"</title>");
			content.append("<style>");
			content.append("ul{list-style-type: none;}li{font-size: 20px;margin:20px}");
			content.append("</style>");
			content.append("</head><body><h1>"+title+"</h1>");
			content.append("<ul>");
			for (File f : folder.listFiles()) {
				content.append("<li><a href='"+f.getAbsolutePath().replace(ROOT.getAbsolutePath(), "")+"'>"+f.getName()+"</a></li>");
			}
			content.append("</ul></body></html>");
			return content.toString();
		}

		public BufferedOutputStream getBufferedOutputStream(){
			return outData;
		}
		
		public PrintWriter getPrintWriter() {
			return out;
		}
		
		public BufferedReader getBufferedInputReader() {
			return in;
		}


		public File getFileFromSubUrl(String subURL) {
			File f = new File(ROOT.getAbsolutePath() + subURL);
			return f;
		}

		public void close() {
			try {
				s.close();
			}catch(Exception e) {} finally {
				CLIENTS.remove(this);
			}
		}
	}
	
	
}
