package org.mule.kicks.transformers;

import java.util.ArrayList;
import java.util.HashMap;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

public class SFDCUsersMegeTransformer extends AbstractMessageTransformer {

	private String QUERY_COMPANY_A = "usersFromOrgA";
	private String QUERY_COMPANY_B = "usersFromOrgB";
	
	private ArrayList<HashMap> usersFromOrgAList; 
	private ArrayList<HashMap> usersFromOrgBList; 
	
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		
		usersFromOrgAList = message.getInvocationProperty(QUERY_COMPANY_A);
		usersFromOrgBList = message.getInvocationProperty(QUERY_COMPANY_B);
		ArrayList<HashMap> mergedUsersList = new ArrayList<HashMap>();
		
		for (HashMap userFromA : usersFromOrgAList )
		{
			HashMap userFromB = getUserFromListB(userFromA.get("Email"));
			
			HashMap newMergedUser = new HashMap();
			
			newMergedUser.put("Name", userFromA.get("Name"));
			newMergedUser.put("Mail", userFromA.get("Email"));
			newMergedUser.put("IDInA", userFromA.get("Id"));
			newMergedUser.put("UserNameInA", userFromA.get("Username"));
			
			newMergedUser.put("IDInB", userFromB.get("Id"));
			newMergedUser.put("UserNameInB", userFromB.get("Username"));
			
			mergedUsersList.add(newMergedUser);
		}
		
		for (HashMap userFromB : usersFromOrgBList )
		{
			
			if (!checkExistingUserInA(userFromB.get("Email")))
			{
				HashMap newMergedUser = new HashMap();
				
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
	
	private Boolean checkExistingUserInA (Object mailFromUserB)
	{
		for (HashMap userFromA : usersFromOrgAList) 
		{
			if (userFromA.get("Email").equals(mailFromUserB))
			{
				return true;
			}
		}
		return false;
	}
	
	private HashMap getUserFromListB (Object mailFromUserA){
		
		HashMap resultingUserFromB = new HashMap();
		
		resultingUserFromB.put("Id", "");
		resultingUserFromB.put("Username", "");
		
		for (HashMap userFromB : usersFromOrgBList) 
		{
			if (userFromB.get("Email").equals((String) mailFromUserA))
			{
				resultingUserFromB.put("Id", userFromB.get("Id"));
				resultingUserFromB.put("Username", userFromB.get("Username"));
				
				return resultingUserFromB;
			}
		}
		
		return resultingUserFromB;
	}

}
