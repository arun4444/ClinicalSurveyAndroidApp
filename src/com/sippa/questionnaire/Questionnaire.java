package com.sippa.questionnaire;

public class Questionnaire 
{
	static public class Section
	{
		public String title;
		public Section[] section;
		public Question[] question;		
	}
	
	
	static public class Question
	{
		public boolean isRequired;
		public String text;
		public Response[] response;
		public String selectedResponse;
	}
		
	static public class Response
	{
		public int id;
		public String text;
	}
	
	public Section[] section;
	public int id;
	public String name;
	public String postURL;
}
