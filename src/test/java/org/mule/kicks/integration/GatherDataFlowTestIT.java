package org.mule.kicks.integration;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.config.MuleProperties;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.tck.junit4.FunctionalTestCase;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Mule Kick that make calls to external systems.
 * 
 * @author damiansima
 */
public class GatherDataFlowTestIT extends FunctionalTestCase {
	private static final String USERS_FROM_ORG_A = "usersFromOrgA";
	private static final String USERS_FROM_ORG_B = "usersFromOrgB";

	@BeforeClass
	public static void beforeClass() {
		System.setProperty("mule.env", "test");
	}

	@AfterClass
	public static void afterClass() {
		System.getProperties().remove("mule.env");
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Override
	protected String getConfigResources() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream("./src/main/app/mule-deploy.properties"));
			return props.getProperty("config.resources");
		} catch (Exception e) {
			throw new IllegalStateException(
					"Could not find mule-deploy.properties nor mule-config.xml file on classpath. Please add any of those files or override the getConfigResources() method to provide the resources by your own");
		}
	}

	@Override
	protected Properties getStartUpProperties() {
		Properties properties = new Properties(super.getStartUpProperties());

		String pathToResource = "./mappings";
		File graphFile = new File(pathToResource);

		properties.put(MuleProperties.APP_HOME_DIRECTORY_PROPERTY, graphFile.getAbsolutePath());

		return properties;
	}

	@Test
	public void testGatherDataFlow() throws Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("gatherDataFlow");
		flow.initialise();

		MuleEvent event = flow.process(getTestEvent("", MessageExchangePattern.REQUEST_RESPONSE));
		Set<String> flowVariables = event.getFlowVariableNames();

		Assert.assertTrue("The variable usersFromOrgA is missing.", flowVariables.contains(USERS_FROM_ORG_A));
		Assert.assertTrue("The variable usersFromOrgB is missing.", flowVariables.contains(USERS_FROM_ORG_B));

		List<Map<String, String>> usersFromOrgA = event.getFlowVariable(USERS_FROM_ORG_A);
		List<Map<String, String>> usersFromOrgB = event.getFlowVariable(USERS_FROM_ORG_B);

		Assert.assertFalse("There should be users in the variable usersFromOrgA.", usersFromOrgA.isEmpty());
		Assert.assertFalse("There should be users in the variable usersFromOrgB.", usersFromOrgB.isEmpty());

	}

	private SubflowInterceptingChainLifecycleWrapper getSubFlow(String flowName) {
		return (SubflowInterceptingChainLifecycleWrapper) muleContext.getRegistry().lookupObject(flowName);
	}

}
