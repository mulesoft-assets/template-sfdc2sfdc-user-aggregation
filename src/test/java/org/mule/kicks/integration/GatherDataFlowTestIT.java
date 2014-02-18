package org.mule.kicks.integration;

import org.junit.*;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.streaming.ConsumerIterator;
import org.mule.tck.junit4.rule.DynamicPort;

import java.util.Map;
import java.util.Set;

/**
 * The objective of this class is to validate the correct behavior of the flows for this Mule Kick that make calls to external systems.
 * 
 * @author damiansima
 */
public class GatherDataFlowTestIT extends AbstractKickTestCase {
	private static final String USERS_FROM_ORG_A = "usersFromOrgA";
	private static final String USERS_FROM_ORG_B = "usersFromOrgB";

	@Rule
	public DynamicPort port = new DynamicPort("http.port");

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGatherDataFlow() throws Exception {
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
