package org.mule.kicks.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

public class SFDCUsersMegeTransformer extends AbstractMessageTransformer {

	private static final String QUERY_COMPANY_A = "usersFromOrgA";
	private static final String QUERY_COMPANY_B = "usersFromOrgB";

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)	throws TransformerException {

		List<Map<String, String>> usersFromOrgAList = message.getInvocationProperty(QUERY_COMPANY_A);
		List<Map<String, String>> usersFromOrgBList = message.getInvocationProperty(QUERY_COMPANY_B);

		List<Map<String, String>> mergedUsersList = new ArrayList<Map<String, String>>();

		for (Map<String, String> userFromA : usersFromOrgAList) {
			
			Map<String, String> userFromB = getUserFromListB(userFromA.get("Email"), usersFromOrgBList);

			Map<String, String> newMergedUser = new HashMap<String, String>();

			newMergedUser.put("Name", userFromA.get("Name"));
			newMergedUser.put("Mail", userFromA.get("Email"));
			newMergedUser.put("IDInA", userFromA.get("Id"));
			newMergedUser.put("UserNameInA", userFromA.get("Username"));

			newMergedUser.put("IDInB", userFromB.get("Id"));
			newMergedUser.put("UserNameInB", userFromB.get("Username"));

			mergedUsersList.add(newMergedUser);
		}

		for (Map<String, String> userFromB : usersFromOrgBList) {

			if (!checkExistingUserInA(userFromB.get("Email"), usersFromOrgAList)) {
				Map<String, String> newMergedUser = new HashMap<String, String>();

				newMergedUser.put("Name", userFromB.get("Name"));
				newMergedUser.put("Mail", userFromB.get("Email"));
				newMergedUser.put("IDInA", "");
				newMergedUser.put("UserNameInA", "");

				newMergedUser.put("IDInB", userFromB.get("Id"));
				newMergedUser.put("UserNameInB", userFromB.get("Username"));

				mergedUsersList.add(newMergedUser);
			}

		}

		return mergedUsersList;
	}

	private Boolean checkExistingUserInA(Object mailFromUserB, List<Map<String, String>> usersFromOrgAList) {
		
		for (Map<String, String> userFromA : usersFromOrgAList) {
			if (userFromA.get("Email").equals(mailFromUserB)) {
				return true;
			}
		}
		return false;
		
	}

	private Map<String, String> getUserFromListB(Object mailFromUserA, List<Map<String, String>> usersFromOrgBList) {

		Map<String, String> resultingUserFromB = new HashMap<String, String>();

		resultingUserFromB.put("Id", "");
		resultingUserFromB.put("Username", "");

		for (Map<String, String> userFromB : usersFromOrgBList) {
			if (userFromB.get("Email").equals((String) mailFromUserA)) {
				resultingUserFromB.put("Id", userFromB.get("Id"));
				resultingUserFromB.put("Username", userFromB.get("Username"));

				return resultingUserFromB;
			}
		}

		return resultingUserFromB;
	}

}
