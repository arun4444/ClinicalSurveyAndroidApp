package com.sippa.questionnaire;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayQuestionnaireActivity extends Activity 
{	
	Questionnaire m_quiz;
	List<ResponsePair> m_quizResponse;
		
	public DisplayQuestionnaireActivity()
	{
		m_quizResponse = new ArrayList<ResponsePair>();		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_display_questionnaire);				
		
		try 
		{
			m_quiz = loadFromFile("quiz.json");
						
	    	LinearLayout contentLayout = new LinearLayout(this);
	    	contentLayout.setOrientation(LinearLayout.VERTICAL);
	    	contentLayout.setGravity(Gravity.TOP);			
	    	setContentView(contentLayout);    	
	    	Config config = new Config(25, 20, 15);
	    	createQuestionnaireView(m_quiz, config, contentLayout);	
	    	
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_questionnaire, menu);
		return true;
	}
*/
	public Questionnaire getQuestionnaire()
	{
		return m_quiz;
	}
	
	public boolean validate()
	{
		for(Iterator<ResponsePair> iter = m_quizResponse.iterator(); iter.hasNext(); )
		{
			ResponsePair pair = iter.next();
			Object response = pair.getResponse();
			Questionnaire.Question question = pair.getQuestion();
			
			if(question.isRequired)
			{
				if(response.getClass().equals( RadioGroup.class ))
				{
					RadioGroup rg = (RadioGroup)response;
					if(rg.getCheckedRadioButtonId() == -1)
						return false;
				}
			}
		}		
		return true;
	}
	
	/**
	 * This Function should be called after validate()
	 * @param filename
	 * @throws Exception 
	 */
	public void writeResponseJSON(String filename) throws Exception
	{
		for(Iterator<ResponsePair> iter = m_quizResponse.iterator(); iter.hasNext(); )
		{
			ResponsePair pair = iter.next();
			Object response = pair.getResponse();
			Questionnaire.Question question = pair.getQuestion();
			
			if(question.isRequired)
			{
				if(response.getClass().equals( RadioGroup.class ))
				{
					RadioGroup rg = (RadioGroup)response;
					question.selectedResponse = String.valueOf(rg.getCheckedRadioButtonId());
				}
			}
		}
		
		Gson gson = new Gson();
		String jsonStr = gson.toJson(m_quiz);
		
		PrintWriter writer = null;
		try 
		{
			writer = new PrintWriter(filename, "UTF-8");
			writer.print(jsonStr);
		} 
		catch (FileNotFoundException e) { throw new Exception("unable to save file."); } 
		catch (UnsupportedEncodingException e) { throw new Exception("unable to save file."); }
		finally
		{
			if(writer != null) writer.close();			 
		}
	}
	
	Questionnaire loadFromFile(String filename) throws IOException
	{
		Gson gson = new Gson();		
		InputStream is = this.getAssets().open(filename);
		Reader reader = new InputStreamReader(is);
		return gson.fromJson(reader, Questionnaire.class); 
	}
	
	void createQuestionnaireView(Questionnaire quiz, Config config, LinearLayout contentLayout)
	{		
    	TextView quizName = new TextView(this);
    	quizName.setText(quiz.name);	    	
    	quizName.setTextSize(config.getTitleFontSize());
    	
		ScrollView scrollView = new ScrollView(this);

    	LinearLayout layout = new LinearLayout(this);
    	layout.setOrientation(LinearLayout.VERTICAL);
    	layout.setGravity(Gravity.TOP);
		layout.addView(quizName);
    	    	    	
    	for(int i=0; i<quiz.section.length; i++)
    		createSectionView(quiz.section[i], config, layout);    	
    	
    	Button submit = new Button(this);
    	submit.setText("Submit");
    	layout.addView(submit);
    	
		scrollView.addView(layout);		
    	contentLayout.addView(scrollView);    	    	
    	
    	final DisplayQuestionnaireActivity activity = this;    	
    	submit.setOnClickListener(new View.OnClickListener() 
    	{
            public void onClick(View v) {
            	
            	if(!activity.validate())
            		Toast.makeText(getApplicationContext(), "ERROR: answer all required questions.", Toast.LENGTH_LONG).show();
            	else
            	{
            		//String appDir = (new ContextWrapper(activity)).getFilesDir().getPath();
            		//activity.writeResponseJSON(appDir+"/response.json");
            		
            		
            		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
            		SharedPreferences.Editor editor = settings.edit();
            	    editor.putString("surveyUID", "1000");
            	    editor.commit();

            		try 
            		{
                	    String filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/bluetooth/mySurveyResponse.doc";
						activity.writeResponseJSON(filename);
						
						//Intent intent = new Intent();
						//intent.setAction("net.techsuite.SIPPA_for_ACEM.LockClinicalSurvey");
						//activity.startActivity(intent);
						
	            		Toast.makeText(getApplicationContext(), "Responses Saved!", Toast.LENGTH_LONG).show();
					} 
            		catch (Exception e) 
					{
	            		//Toast.makeText(getApplicationContext(), "Unable to save response to file.", Toast.LENGTH_LONG).show();
            			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
					}            		
            		
            	}
            }
        });    	
    	
	}

	void createSectionView(Questionnaire.Section section, Config config, ViewGroup contentLayout)
	{
    	TextView title = new TextView(this);
    	title.setText(section.title);	    	
    	title.setTextSize(config.getSectionTitleFontSize());
    	contentLayout.addView(title);    	
    	
    	if(section.question != null)
    	{
    		for(int i=0; i<section.question.length; i++)
    			createQuestionView(section.question[i], config, contentLayout, i);    	    	
    	}
    	
    	if(section.section != null)
    	{
        	for(int i=0; i<section.section.length; i++)
    		createSectionView(section.section[i], config, contentLayout);
    		
    	}
	}
	
	void createQuestionView(Questionnaire.Question question, Config config, ViewGroup contentLayout, int index)
	{
    	TextView text = new TextView(this);
    	text.setText((index+1)+") "+question.text);	    	
    	contentLayout.addView(text);    	
	
    	RadioGroup radioGroup = new RadioGroup(this);
    	for(int i=0; i<question.response.length; i++)
    	{
        	RadioButton radio = new RadioButton(this); 
        	radioGroup.addView(radio);        	
        	radio.setText(question.response[i].text);	    	
        	radio.setId(question.response[i].id);
    	}
    	contentLayout.addView(radioGroup);    	    		
    	m_quizResponse.add( new ResponsePair(question, radioGroup) );
	}
	
}
