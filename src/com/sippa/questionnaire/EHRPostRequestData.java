package com.sippa.questionnaire;
/**
 * An object representing the EHR data that will be sent to the Sippa Server.
 * 
 * 
 * @author Touseef Hasan
 *
 */
public class EHRPostRequestData {
	
	static class Section
	{
		Section(String _name)
		{
			name = _name;
		}
		String name;
	}
	
	public String EHR;
    public Section[] section;	
}
