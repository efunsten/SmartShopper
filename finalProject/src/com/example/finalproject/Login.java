package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.gson.Gson;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Login extends SherlockActivity {
	
	public static ParseUser mCurrentUser;
	private String mUsername = null;
	private String mPassword = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "RCIm6xY3zgCw0sguu3OtbE0e1aIdd7dBjqhnNrQV", "kwjP5rVdVPPBlSf3PljblCeewigUmTUbto0GKwG4");
		
		
		mCurrentUser = ParseUser.getCurrentUser();
		if (mCurrentUser != null) {
			Intent intent = new Intent(this, ItemList.class);
			startActivity(intent);
			finish();
		} else {
			mCurrentUser = new ParseUser();
		    setContentView(R.layout.login_layout);
		}
		
	}
	
	public void loginUser(View view) {
		mUsername = ((EditText) findViewById(R.id.editText_username)).getText().toString();
		mPassword = ((EditText) findViewById(R.id.editText_password)).getText().toString();
		
		if(mUsername != null && mPassword != null) {
			ParseUser.logInInBackground(mUsername, mPassword, new LogInCallback() {
				@Override
				public void done(ParseUser user, ParseException e) {
				    if (user != null) {
				    	mCurrentUser = user;
				    	Intent intent = new Intent(getApplication(), ItemList.class);
						startActivity(intent);
						finish();
				    } else {
				      // Signup failed. Look at the ParseException to see what happened.
				    	Log.d("Error", e.getMessage());
				    }
				  }
				});
		}
	}
	
	public void registerUser(View view) {
		mUsername = ((EditText) findViewById(R.id.editText_username)).getText().toString();
		mPassword = ((EditText) findViewById(R.id.editText_password)).getText().toString();
		
		if(mUsername != null && mPassword != null) {
			mCurrentUser.setUsername(mUsername);
			mCurrentUser.setPassword(mPassword);
			mCurrentUser.signUpInBackground(new SignUpCallback() {
			  public void done(ParseException e) {
			    if (e == null) {
			    	Intent intent = new Intent(getApplication(), ItemList.class);
					startActivity(intent);
					finish();
			    } else {
			      // Sign up didn't succeed. Look at the ParseException
			      // to figure out what went wrong
			    }
			  }
			});
		}
	}
}
