package com.sippa.questionnaire;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelectQuestionnaireActivity extends Activity 
{
	
	static class BackgroundTask extends AsyncTask<Object, Void, String> 
	{
		private Exception m_exception = null;
		private Context m_context;
		
		public Exception getException()
		{
			return m_exception;
		}
		
		@Override
		protected String doInBackground(Object... arg0) 
		{
			m_context = (Context)arg0[0];
			String postURL = (String)arg0[1];
	    	HttpClient client = new DefaultHttpClient();    	
	        HttpPost post = new HttpPost(postURL);
	        post.setHeader("Content-type", "application/json");

	        try 
	        {
				post.setEntity(new StringEntity( "{\"method\": \"get_session_key\", \"params\": {\"username\": \"admin\", \"password\": \"gunbound\" }, \"id\": 1}"));				
				HttpResponse response = client.execute(post);			
				
				return String.valueOf(response.getStatusLine().getStatusCode());
			} 
	        catch (Exception e) 
			{
	        	m_exception = e;
			}        
			
			return null;
		}
		
		
		@Override
	    protected void onPostExecute(String result) 
	    {
			if(result == null)
	    		Toast.makeText(m_context, m_exception.getMessage(), Toast.LENGTH_LONG).show();				
			else
    		Toast.makeText(m_context, "result: "+result, Toast.LENGTH_LONG).show();
	    }		
	}	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

    	LinearLayout contentLayout = new LinearLayout(this);
    	contentLayout.setOrientation(LinearLayout.VERTICAL);
    	contentLayout.setGravity(Gravity.TOP);			
    	setContentView(contentLayout);    	
    	
    	TextView text = new TextView(this);
    	text.setText("Select a Quiz:");
    	contentLayout.addView(text);

    	BackgroundTask bgTask = new BackgroundTask();
    	bgTask.execute(this, "http://149.4.223.200/clinicaltrialsurvey/index.php/admin/remotecontrol");

	}
		
}
