package org.sagebionetworks.jmeter_ncbo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author xschildw
 */
public class NCBOProviderImpl {
	private final String host;
	private final String port;
	private final String apiKey;
	
	public NCBOProviderImpl(String host, String port, String apiKey) {
		this.host = host;
		this.port = port;
		this.apiKey = apiKey;
	}
	
	public HttpResponse get(String path, Map<String, String> params) {
		HttpResponse resp = null;
		try {
			URIBuilder builder = new URIBuilder();
			builder.setScheme("http");
			builder.setHost(this.host);
			if (null != port) {
				builder.setPort(Integer.parseInt(port)); // This needs to be a parameter (only used for AMI)
			}
			builder.setPath(path);
			if (null != params) {
				for (Map.Entry<String, String> e : params.entrySet()) {
					builder.addParameter(e.getKey(), e.getValue());
				}
			}
			builder.addParameter("apikey", apiKey);
			URI uri = builder.build();
			resp = RESTClient.get(uri);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return resp;
	}
	
	public String post(String path, Map<String, String> params) {
		if (null == path || null == params) {
			throw new IllegalArgumentException("path and params cannot be null");
		}
		HttpResponse resp = null;
		String respContent = null;
		try {
			URIBuilder builder = new URIBuilder();
			builder.setScheme("http");
			builder.setHost(this.host);
			if (null != port) {
				builder.setPort(Integer.parseInt(port)); // This needs to be a parameter (only used for AMI)
			}
			builder.setPath(path);
			params.put("apikey", apiKey);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> e : params.entrySet()) {
				nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps);
			URI uri = builder.build();
			resp = RESTClient.post(uri, entity);
			System.out.println("Response code:\t" + resp.getStatusLine().getStatusCode());
			HttpEntity respEntity = resp.getEntity();
			if (null != entity) {
				respContent = EntityUtils.toString(respEntity);
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return respContent;
	}
}
