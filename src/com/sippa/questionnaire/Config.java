package com.sippa.questionnaire;

public class Config
{
	public Config( float titleFontSize, float sectionTitleFontSize, float subsectionTitleFontSize)
	{
		m_titleFontSize = titleFontSize;
		m_sectionTitleFontSize = sectionTitleFontSize;
		m_subsectionTitleFontSize = subsectionTitleFontSize;		
	}
	
	public float getTitleFontSize()
	{
		return m_titleFontSize;		
	}

	public float getSectionTitleFontSize()
	{
		return m_sectionTitleFontSize;
	}	
	
	public float getSubSectionTitleFontSize()
	{
		return m_subsectionTitleFontSize;
	}
	
	float m_titleFontSize;
	float m_sectionTitleFontSize;
	float m_subsectionTitleFontSize;
}
