package com.sippa.questionnaire;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayQuestionnaireActivity extends Activity 
{	
	/**
	 * 
	 * EHRDataExtractionBGTask: Posts a JSON with EHR file content and list of sections to external 
	 * server and retrieves a JSON with section data. Then pre-fills questions in the survey with  
	 * EHR information.
	 */
	
	static class EHRDataExtractionBGTask extends AsyncTask<String, Void, String> 
	{
		private Exception m_exception = null;
		private Context m_context;
		private String m_json;
		private List<ResponsePair> m_quizResponse;
		
		EHRDataExtractionBGTask(Context context, String ehrContent, List<ResponsePair> quizResponse)
		{
			m_context = context;			
			m_quizResponse = quizResponse;
			
	    	HashSet<String> ccdSectionNames = new HashSet<String>();
	    	for(Iterator<ResponsePair> iter = m_quizResponse.iterator(); iter.hasNext();)
	    	{
	    		String ccdSection = iter.next().m_question.ccdSection;
	    		if(ccdSection != null && !ccdSection.equals(""))
	    			ccdSectionNames.add(ccdSection);
	    	}
			
			EHRPostRequestData ehrData = new EHRPostRequestData();
			ehrData.EHR = ehrContent;
			ehrData.section = new EHRPostRequestData.Section[ccdSectionNames.size()];
			int index=0;
			for(Iterator<String> iter = ccdSectionNames.iterator(); iter.hasNext(); )
			{
				ehrData.section[index++] = new EHRPostRequestData.Section(iter.next());
			}						
						
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			m_json =  gson.toJson(ehrData);			
		}		
		
		public Exception getException()
		{
			return m_exception;
		}

		static public String readFile( String file ) throws IOException 
		{
		    BufferedReader reader = new BufferedReader( new FileReader (file));
		    String         line = null;
		    StringBuilder  stringBuilder = new StringBuilder();

		    while( ( line = reader.readLine() ) != null ) {
		        stringBuilder.append( line );
		    }
		    reader.close();
		    return stringBuilder.toString();
		}		
		
		@Override
		protected String doInBackground(String... arg0) 
		{			
	        try 
	        {
				String postURL = arg0[0];
		    	HttpClient client = new DefaultHttpClient();    	
		        HttpPost post = new HttpPost(postURL);
		        post.setHeader("Content-type", "application/json");
		        
				post.setEntity(new StringEntity( m_json ));				
				HttpResponse response = client.execute(post);			

				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode != 200)
					throw new Exception("Http Status Code: "+statusCode);
				
				return EntityUtils.toString(response.getEntity());
			} 
	        catch(Exception ex)
	        {
	        	m_exception = ex;
	        }
			return "";
		}
		
		
		@Override
	    protected void onPostExecute(String result) 
	    {
    		if(m_exception != null || result==null || "".equals(result))
    		{
        		Toast.makeText(m_context, "Failed to get EHR data.", Toast.LENGTH_LONG).show();				
    			return;
    		}
    			    		
	    	Gson gson = new Gson();
			String[] sectionStringArray = result.split("\\r?\\n");
			String responseJson = "{";
			for(int i=0; i<sectionStringArray.length; i++)
			{
				if(sectionStringArray[i].length()<3)continue;				
				responseJson += sectionStringArray[i].substring(1, sectionStringArray[i].length()-1);
				if(i!= sectionStringArray.length-1)
					responseJson += ",";					
			}
			responseJson += "}";
    		
	    	EHRPostResponseData ehrResponse = gson.fromJson(responseJson, EHRPostResponseData.class);
	    	HashMap<String, String> ccdSectionInfo = new HashMap<String, String>();
	    	
			Field[] sectionFields = EHRPostResponseData.class.getDeclaredFields();
			for(int i=0; i<sectionFields.length; i++)
			{
				try 
				{
					String sectionName = sectionFields[i].getName();
					Object sectionData = sectionFields[i].get(ehrResponse);
					if(sectionData != null)
						ccdSectionInfo.put(sectionName.toLowerCase(Locale.getDefault()), sectionData.toString());															
				} 
				catch (IllegalArgumentException e) {} 
				catch (IllegalAccessException e) {}
			}	    	
	    		    		    	
	    	for(Iterator<ResponsePair> iter = m_quizResponse.iterator(); iter.hasNext(); )
	    	{
	    		ResponsePair pair = iter.next();
	    		if(pair.m_question.ccdSection != null)
	    		{
	    			if(!pair.m_response.getClass().equals(EditText.class))
	    				continue;
	    			
    				String sectionStr = ccdSectionInfo.get(pair.m_question.ccdSection);    				    				
    				if(sectionStr != null)
    					((EditText)pair.m_response).setText(sectionStr);	    				    		
	    		}
	    	}
    		Toast.makeText(m_context, "Successfully prefilled questioned with EHR Information", Toast.LENGTH_LONG).show();					
		}				
	}	
	
	
	/**
	 * DisplayQuestionnaireActivity
	 */	
	
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
			m_quiz = loadFromFile("quiz_long.json");

	    	LinearLayout contentLayout = new LinearLayout(this);
	    	contentLayout.setOrientation(LinearLayout.VERTICAL);
	    	contentLayout.setGravity(Gravity.TOP);			
	    	setContentView(contentLayout);    	
	    	Config config = new Config(25, 20, 15);
	    	createQuestionnaireView(m_quiz, config, contentLayout);	
	    	
			String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bluetooth/myEHRsample.doc";
			String fileContent = EHRDataExtractionBGTask.readFile(filename);			
			String postURL = "http://149.4.223.206:8080/SIPPAHEALTHServlet/SectionSendDown";

	    	EHRDataExtractionBGTask bgTask = new EHRDataExtractionBGTask(this, fileContent, m_quizResponse);
			bgTask.execute(postURL);
		}
		catch (IOException e) 
		{
    		//Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();				
		}
		
	}
		
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
				else if(response.getClass().equals( EditText.class ))
				{
					EditText et = (EditText)response;
					if("".equals(et.getText().toString()))
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
				else if(response.getClass().equals(EditText.class))
				{
					EditText et = (EditText)response;
					question.selectedResponse = et.getText().toString();
					
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
    	LinearLayout questionLayout = new LinearLayout(this);
    	questionLayout.setOrientation(LinearLayout.VERTICAL);
    	questionLayout.setGravity(Gravity.TOP);
    	contentLayout.addView(questionLayout);    	

    	
    	TextView text = new TextView(this);
    	text.setText((index+1)+") "+question.text);	    	
    	questionLayout.addView(text);    	
    	
    	View responseView = null;
	
    	if(question.type.equals("radio"))
    	{
    		responseView = new RadioGroup(this);
    		RadioGroup radioGroup = (RadioGroup)responseView;
    		for(int i=0; i<question.response.length; i++)
    		{    		
    			RadioButton radio = new RadioButton(this); 
    			radioGroup.addView(radio);        	
    			radio.setText(question.response[i].text);	    	
    			radio.setId(question.response[i].id);
    		}
    	}
    	else if(question.type.equals("freetext"))
    	{
    		responseView = new EditText(this);
    		EditText editText = (EditText)responseView;
    		editText.setSingleLine(false);
    		editText.setLines(5);    		
    	}
    	
    	if(responseView != null)
    	{
    		questionLayout.addView(responseView);
	    	m_quizResponse.add( new ResponsePair(question, responseView) );
    	}
	}
	
}
