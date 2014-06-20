package org.mule.templates.integration;

import org.junit.*;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.streaming.ConsumerIterator;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.templates.builders.SfdcObjectBuilder;

import com.sforce.soap.partner.SaveResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The objective of this class is to validate the correct behavior of the flows for this Mule Template that make calls to external systems.
 * 
 * @author damiansima
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {
	private static final String USERS_FROM_ORG_A = "usersFromOrgA";
	private static final String USERS_FROM_ORG_B = "usersFromOrgB";

	protected static final String TEMPLATE_NAME = "user-aggregation";

	@Rule
	public DynamicPort port = new DynamicPort("http.port");

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGatherDataFlow() throws Exception {

		// This template does not create Users before and after since SalesForce
		// limits the creation of them in Sandbox.
		// As SalesForce Sandboxes are based on real instances, is assumed that
		// it will always have Customers so this test is not dealing with the
		// process to ensure that are Users available

		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("gatherDataFlow");
		flow.initialise();

		MuleEvent event = flow.process(getTestEvent("", MessageExchangePattern.REQUEST_RESPONSE));
		Set<String> flowVariables = event.getFlowVariableNames();

		Assert.assertTrue("The variable usersFromOrgA is missing.", flowVariables.contains(USERS_FROM_ORG_A));
		Assert.assertTrue("The variable usersFromOrgB is missing.", flowVariables.contains(USERS_FROM_ORG_B));

		ConsumerIterator<Map<String, String>> usersFromOrgA = event.getFlowVariable(USERS_FROM_ORG_A);
		ConsumerIterator<Map<String, String>> usersFromOrgB = event.getFlowVariable(USERS_FROM_ORG_B);

		Assert.assertTrue("There should be users in the variable usersFromOrgA.", usersFromOrgA.size() != 0);
		Assert.assertTrue("There should be users in the variable usersFromOrgB.", usersFromOrgB.size() != 0);
	}

}
