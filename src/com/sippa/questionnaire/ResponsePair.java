package com.sippa.questionnaire;

import com.sippa.questionnaire.Questionnaire.Question;

public class ResponsePair
{
	public ResponsePair(Question question, Object response)
	{
		m_question = question;
		m_response = response;		
	}
	
	public Question getQuestion() 
	{
		return m_question;
	}
	
	public Object getResponse()
	{
		return m_response;
	}
	
	Question m_question;
	Object m_response;
}
