package org.sagebionetworks.jmeter_ncbo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author xschildw
 */
public class AnnotatorService {
	private final String path = "/obs/annotator";
	private final NCBOProviderImpl ncboProvider;
	private Map<String, String> params;
	
	public AnnotatorService(NCBOProviderImpl ncbo) {
		ncboProvider = ncbo;
		params = new HashMap<String, String>();
	}
	
	public AnnotatorService withParam(String paramKey, String paramValue) {
		this.params.put(paramKey, paramValue);
		return this;
	}
	
	public void annotate() {
		this.annotate(this.params);
	}
	
	public void annotate(Map<String, String> params) {
		String resp = ncboProvider.post(path, params);
		System.out.println(params.get("textToAnnotate") + "\t" + resp);
	}
}
