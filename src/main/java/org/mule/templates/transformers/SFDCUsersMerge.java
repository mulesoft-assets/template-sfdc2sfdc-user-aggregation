package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.google.common.collect.Lists;

/**
 * This transformer will take to list as input and create a third one that will be the merge of the previous two. The identity of an element of the list is
 * defined by its email.
 * 
 * @author
 */
public class SFDCUsersMerge extends AbstractMessageTransformer {

	private static final String QUERY_COMPANY_A = "usersFromOrgA";
	private static final String QUERY_COMPANY_B = "usersFromOrgB";

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		List<Map<String, String>> mergedUsersList = mergeList(getUsersList(message, QUERY_COMPANY_A), getUsersList(message, QUERY_COMPANY_B));

		return mergedUsersList;
	}

	private List<Map<String, String>> getUsersList(MuleMessage message, String propertyName) {
		Iterator<Map<String, String>> iterator = message.getInvocationProperty(propertyName);
		return Lists.newArrayList(iterator);
	}

	/**
	 * The method will merge the accounts from the two lists creating a new one.
	 * 
	 * @param usersFromOrgA
	 *            users from organization A
	 * @param usersFromOrgB
	 *            users from organization B
	 * @return a list with the merged content of the to input lists
	 */
	private List<Map<String, String>> mergeList(List<Map<String, String>> usersFromOrgA, List<Map<String, String>> usersFromOrgB) {
		List<Map<String, String>> mergedUsersList = new ArrayList<Map<String, String>>();

		// Put all users from A in the merged mergedUsersList
		for (Map<String, String> userFromA : usersFromOrgA) {
			Map<String, String> mergedUser = createMergedUser(userFromA);
			mergedUser.put("IDInA", userFromA.get("Id"));
			mergedUser.put("UserNameInA", userFromA.get("Username"));
			mergedUsersList.add(mergedUser);
		}

		// Add the new users from B and update the exiting ones
		for (Map<String, String> usersFromB : usersFromOrgB) {
			Map<String, String> userFromA = findUserInList(usersFromB.get("Email"), mergedUsersList);
			if (userFromA != null) {
				userFromA.put("IDInB", usersFromB.get("Id"));
				userFromA.put("UserNameInB", usersFromB.get("Username"));
			} else {
				Map<String, String> mergedAccount = createMergedUser(usersFromB);
				mergedAccount.put("IDInB", usersFromB.get("Id"));
				mergedAccount.put("UserNameInB", usersFromB.get("Username"));
				mergedUsersList.add(mergedAccount);
			}

		}
		return mergedUsersList;
	}

	private Map<String, String> createMergedUser(Map<String, String> user) {
		Map<String, String> mergedUser = new HashMap<String, String>();
		mergedUser.put("Email", user.get("Email"));
		mergedUser.put("Name", user.get("Name"));
		mergedUser.put("IDInA", "");
		mergedUser.put("UserNameInA", "");
		mergedUser.put("IDInB", "");
		mergedUser.put("UserNameInB", "");
		return mergedUser;
	}

	private Map<String, String> findUserInList(String accountName, List<Map<String, String>> orgList) {
		for (Map<String, String> account : orgList) {
			if (account.get("Email")
						.equals(accountName)) {
				return account;
			}
		}
		return null;
	}
}
