package org.sagebionetworks.jmeter_ncbo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class JmeterTest extends AbstractJavaSamplerClient {
	private final String JMETER_ARG_KEY_ENDPOINT = "Service endpoint";
	private final String JMETER_ARG_KEY_PORT = "Port";
	private final String JMETER_ARG_KEY_ONTOLOGY_ID = "Ontology id";
	private final String JMETER_ARG_KEY_APIKEY = "Api key";
	
	private List<String> samples;
	private String endpoint;
	private String apiKey;
	private String port;
	private String ontologyId;
	
	@Override
	public Arguments getDefaultParameters() {
		Arguments args = new Arguments();
		args.addArgument(JMETER_ARG_KEY_ENDPOINT, "rest.bioontology.org");
		args.addArgument(JMETER_ARG_KEY_PORT, "80");
		args.addArgument(JMETER_ARG_KEY_ONTOLOGY_ID, "");
		args.addArgument(JMETER_ARG_KEY_APIKEY, "");
		return args;
	}
	
	@Override
	public void setupTest(JavaSamplerContext ctxt) {
		endpoint = ctxt.getParameter(JMETER_ARG_KEY_ENDPOINT);
		port = ctxt.getParameter(JMETER_ARG_KEY_PORT);
		ontologyId = ctxt.getParameter(JMETER_ARG_KEY_ONTOLOGY_ID);
		apiKey = ctxt.getParameter(JMETER_ARG_KEY_APIKEY);
		loadSamples();
		Logger.getLogger(JmeterTest.class.getName()).log(Level.INFO, String.format("setupTest():\tendpoint: %s, port: %s, ontologyId: %s, apiKey: %s\n", endpoint, port, ontologyId, apiKey));

	}

	@Override
	public org.apache.jmeter.samplers.SampleResult runTest(JavaSamplerContext jsc) {
		SampleResult sampleResult = new SampleResult();
		sampleResult.setSuccessful(true);
		
		try {
			NCBOProviderImpl ncboProvider = new NCBOProviderImpl(endpoint, port, apiKey);
			String textToAnnotate = getTextToAnnotate();
			
			AnnotatorService annotatorSvc = new AnnotatorService(ncboProvider);
			Map<String, String> params = new HashMap<String, String>();
			params.put("isVirtualOntologyId", "true");
			params.put("ontologiesToKeepInResult", ontologyId);
			params.put("textToAnnotate", textToAnnotate);
			

			sampleResult.sampleStart();

			annotatorSvc.annotate(params);
		} catch (Exception ex) {
			sampleResult.setSuccessful(false);
			Logger.getLogger(JmeterTest.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			sampleResult.sampleEnd();
		}
		
		return sampleResult;
	}
	
	public void loadSamples() {
		InputStream is = getClass().getResourceAsStream("/ncbo_sample.txt");
		try {
			samples = IOUtils.readLines(is);
		} catch (IOException e) {
			Logger.getLogger(JmeterTest.class.getName()).log(Level.SEVERE, null, e);
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
	
	public void loadConfig() throws IOException {
		Properties props = new Properties();
		InputStream is = getClass().getResourceAsStream("/jmeter_ncbo.properties");
		props.load(is);
		endpoint = props.getProperty("org.sagebionetworks.jmeter_ncbo.endpoint");
		apiKey = props.getProperty("org.sagebionetworks.jmeter_ncbo.apikey");
		is.close();
	}
	
	public String getTextToAnnotate() {
		String s = null;
		Random ran = new Random();
		int i = ran.nextInt(samples.size()-1);
		s = samples.get(i);
		return s;
	}
}
