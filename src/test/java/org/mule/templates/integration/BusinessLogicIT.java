/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.tck.junit4.rule.DynamicPort;

import com.google.common.collect.Lists;

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

	@Test
	public void testGatherDataFlow() throws Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("gatherDataFlow");
		flow.setMuleContext(muleContext);
		flow.initialise();
		flow.start();
		MuleEvent event = flow.process(getTestEvent("", MessageExchangePattern.REQUEST_RESPONSE));
		Iterator<Map<String, String>> list = (Iterator<Map<String, String>>)event.getMessage().getPayload();
		Assert.assertTrue("There should be users from source A or source B.", Lists.newArrayList(list).size() != 0);
	}

	@Test
	public void testFormatOutputFlow() throws Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("gatherDataFlow");
		flow.setMuleContext(muleContext);
		flow.initialise();
		flow.start();
		MuleEvent event = flow.process(getTestEvent("", MessageExchangePattern.REQUEST_RESPONSE));

		flow = getSubFlow("formatOutputFlow");
		flow.setMuleContext(muleContext);
		flow.initialise();
		flow.start();
		event = flow.process(event);

		Assert.assertTrue("The payload should not be null.", event.getMessage().getPayload() != null);
	}
}
