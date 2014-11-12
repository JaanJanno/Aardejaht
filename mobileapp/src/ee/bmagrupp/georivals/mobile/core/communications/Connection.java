package ee.bmagrupp.georivals.mobile.core.communications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class for making a HTTP get request as default. Request runs on separate thread. 
 * Capable of  retrieving a response and a list of cookies in string format. Each object holds 
 * an immutable URL with which to communicate. Request is made after calling 
 * sendRequest() method. Response to the request can be handled in the overridden 
 * handleResponseBody() method. Retrieved cookies can be handled in the overridden
 * handleResponseCookies() method.
 * @author Jaan Janno
 */
 
public abstract class Connection implements Runnable {
	
	private final String DEFAULT_CONTENT_TYPE = "application/json";

	private String urlString;				// URL of the connection destination.
	private String cookie = "";				// Cookie for the request.
	private String parameters = null;
	
	public Connection(String urlString) {
		this.urlString = urlString;
	}
	
	/**
	 * 
	 * @param urlString URL of connection.
	 * @param cookie Cookie sent to the server.
	 */

	public Connection(String urlString, String cookie) {
		this.urlString = urlString;
		this.cookie = cookie;
	}
	
	/**
	 * Starts the thread which makes a request to the URL in urlString
	 * field. Response gets stored in "response" field.
	 * One Connection object can only run this once!
	 */
	
	public void sendRequest() {	
		new Thread(this).start();
	}
	
	/**
	 * Method called by separate thread. Call sendRequest() instead of this!
	 */

	@Override
	public void run() {
		HttpURLConnection connection = null;
		try {
			connection = getConnection(createUrl());
			httpRequest(connection);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}
	
	/**
	 * Add a parameter to the request.
	 * @param key
	 * @param value
	 */
	
	public void addParameter(String key, String value){
		if (parameters == null)
			parameters  =       key + "=" + value;
		else
			parameters += "&" + key + "=" + value;
	}
	
	/*
	 * Generates a query.
	 */

	private String createUrl() {
		if (parameters == null)
			return urlString;
		else
			return urlString + "?" + parameters; 
	}

	/*
	 * Connects to given URL and returns its response string.
	 */

	protected void httpRequest(HttpURLConnection connection) throws Exception {
		handleRequestProperties(connection, false, null);	
		readStream (connection.getInputStream());	
		List<String> cookies = collectResponseCookies(connection);
		handleResponseCookies(cookies);
	}
	
	/*
	 * Collects cookies from header.
	 */
	
	protected List<String> collectResponseCookies(HttpURLConnection connection) {
		List<String> cookies = new ArrayList<String>();
		for(Map.Entry<String, List<String>> headers : connection.getHeaderFields().entrySet()){
			if (headers.getKey() != null && headers.getKey().equals("Set-Cookie")){
				cookies = headers.getValue();
				break;
			}		
		}
		return cookies;
	}
	
	/*
	 * Sets properties for the request.
	 */

	protected void handleRequestProperties(HttpURLConnection connection, boolean output, String requestMethod) throws ProtocolException {
		connection.setDoOutput(output);
		connection.setRequestProperty("Content-Type", DEFAULT_CONTENT_TYPE);
		connection.addRequestProperty("Cookie", cookie);
		if (requestMethod != null)
			connection.setRequestMethod(requestMethod);
	}

	/*
	 * Handles connecting to given URL.
	 */

	private HttpURLConnection getConnection(String urlString) throws Exception {
		URL url = new URL(urlString);
		return (HttpURLConnection) url.openConnection();
	}
	
	/*
	 * Reads input stream incoming from the server.
	 */
	
	protected void readStream(InputStream in) throws Exception {
		String serverResponse = new String();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		for (String line = reader.readLine(); line != null;) {
			serverResponse += '\n' + line;
			line = reader.readLine();
		}
		reader.close();
		handleResponseBody(serverResponse);
	}
	
	/**
	 * Override this to write a request body for the server. Closing the
	 * writer is not required as it is done automatically.
	 * @param writer
	 * @throws IOException
	 */
	
	public abstract void handleResponseBody(String response);
	
	/**
	 * Override to handle the list of cookies retrieved 
	 * from the server in string format.
	 * @param cookies
	 */
	
	public abstract void handleResponseCookies(List<String> cookies);
	
}