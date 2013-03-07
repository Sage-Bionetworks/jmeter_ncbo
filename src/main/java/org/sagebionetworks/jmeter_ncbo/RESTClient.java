package org.sagebionetworks.jmeter_ncbo;

import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;


/**
 *
 * @author xschildw
 */
public class RESTClient {
	
	private static PoolingClientConnectionManager connMgr = new PoolingClientConnectionManager();
	
	public void shutdown() {
		connMgr.shutdown();
	}
	
	public static HttpResponse get(final URI uri) {
		if (null == uri) {
			throw new IllegalArgumentException("uri cannot be null");
		}
		final HttpClient client = new DefaultHttpClient(connMgr);
		HttpRequestBase getMethod = new HttpGet(uri);
		HttpResponse resp = null;
		try {
			resp = client.execute(getMethod);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return resp;
	}
	
	public static HttpResponse post(final URI uri, final StringEntity entity) {
		if (null == uri || null == entity) {
			throw new IllegalArgumentException("uri and entity cannot be null");
		}
		HttpResponse resp = null;
		try {
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(entity);
			final HttpClient client = new DefaultHttpClient(connMgr);
			resp = client.execute(httpPost);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return resp;
	}
	
//	public static HttpResponse put(final URI uri, final String payload) throws UnsupportedEncodingException, IOException {
//		final HttpClient client = new DefaultHttpClient(connMgr);
//		HttpPut httpPut = new HttpPut(uri);
//		httpPut.addHeader("Accept", "application/json");
//		httpPut.addHeader("Content-Type", "application/json");
//		StringEntity entity = null;
//		if (null != payload) {
//			entity = new StringEntity(payload, "UTF-8");
//			entity.setContentType("application/json");
//			httpPut.setEntity(entity);
//		}
//		HttpResponse resp = client.execute(httpPut);
//		return resp;
//	}
//	
//	public static HttpResponse delete(final URI uri) throws IOException {
//		final HttpClient httpClient = new DefaultHttpClient(connMgr);
//		HttpDelete httpDelete = new HttpDelete(uri);
//		httpDelete.addHeader("Accept","text/html, image/jpeg, *; q=.2, */*; q=.2");
//		HttpResponse resp = httpClient.execute(httpDelete);
//		return resp;
//	}
}
