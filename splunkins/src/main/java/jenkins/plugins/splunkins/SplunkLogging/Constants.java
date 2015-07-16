package jenkins.plugins.splunkins.SplunkLogging;

import java.io.File;

import jenkins.model.Jenkins;

public class Constants {
	public static final String TESTCASE = "testcase";
	public static final String TESTSUITE = "testsuite";
	
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String SCHEME = "scheme";

	
    public static final String httpInputTokenEndpointPath = "/services/data/inputs/http";
    public static final String httpInputCreateEndpoint = "/servicesNS/admin/search/data/inputs/http/http";
    
    public static final String xsdPath  = Jenkins.getInstance().getPluginManager().getPlugin("splunkins").baseResourceURL.getPath() + File.separator + "junit.xsd";
    public static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";
    
    public static final String LOGGING_TEMPLATE = "logging_template.properties";
    public static final String LOGGING_PROPERTIES = "logging.properties";
	

}
