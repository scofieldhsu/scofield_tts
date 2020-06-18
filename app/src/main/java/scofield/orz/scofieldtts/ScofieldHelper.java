package scofield.orz.scofieldtts;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ScofieldHelper {
	Activity mActivity;
	final static int REQUEST_EXTERNAL_STORAGE = 87;
	final static String mTtsFilename = "scofield_tts.mp3";

	final static String mUtteranceSpeech = "speech";
	final static String mUtteranceRecord = "record";

	TextToSpeech tts;

	EditText editText;
	Button speech;
	Button record;

	TextView textView;//scofield
	Button chooseFile;//scofield
	public static final int PICKFILE_RESULT_CODE = 1;//scofield
	private Uri fileUri;//scofield
	private String filePath;//scofield
	Spinner spnPrefer;//scofield
	TextView tts_status;//scofield

	String ttsString = "Number one.\n" +
				"Look at the picture marked number 1 in your test book.\n" +
				"A. Some people are passing by some shops.\n" +
				"B. Some people are working in a restaurant.\n" +
				"C. Some people are walking near a train.\n" +
				"D. Some people are standing by a table.\n";

	//scofield
	public void _onActivityResult(int requestCode, int resultCode, Intent data) {
Log.e("scofield", "_onActivityResult " );

		if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK){
			Uri uri = data.getData();

			String filename = uri.getLastPathSegment();

			String s = "filename: "+ filename;
			textView.setText(s);

			readUri(uri);
		}
	}

	List<String> readUri(Uri uri)
	{
Log.e("scofield", "readUri " );
		List<String> records = new ArrayList<String>();
		try
		{
			InputStream in = mActivity.getContentResolver().openInputStream(uri);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line;
			String ttsString = "";
			while ((line = reader.readLine()) != null)
			{
				records.add(line);
				ttsString += line + "\n";
				//Log.d("scofield", "line "+line);
			}
			reader.close();

			editText.setText(ttsString);

			return records;
		}
		catch (Exception e)
		{
			Log.e("scofield", "readUri Exception: "+e);
			e.printStackTrace();
			return null;
		}
	}

	// no CA:Canada
	//final String[] Country = new String[] {"AU (AUSTRALIA)", "CA", "GB (United Kingdom)", "US (AMERICA)"};
	final String[] Country = new String[] {"AU (AUSTRALIA)", "IN (INDIA)", "GB (United Kingdom)", "US (AMERICA)"};
	int CountryIndex = 0;
	Set<Voice> AUset=new HashSet<>();
	//Set<Voice> CAset=new HashSet<>();
	Set<Voice> INset=new HashSet<>();
	Set<Voice> UKset=new HashSet<>();
	Set<Voice> USset=new HashSet<>();

	Spinner.OnItemSelectedListener spnPreferListener = new Spinner.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			CountryIndex = position;
			//Log.e("scofield", "OnItemSelectedListener onItemSelected: "+CountryIndex);

			/*
						Log.e("scofield", "au set size " + AUset.size());
						for (Voice v: AUset) {
							Log.e("scofield", "v " + v.getName());
							textView.setText(v.getName());
							tts.setVoice(v);
							tts.speak(editText.getText().toString(),
									TextToSpeech.QUEUE_ADD, null, "speech");
						}
						 */

			if (position == 0) {
				//Voice v = AUset.iterator().next();
				//if (v!=null)
					//tts.setVoice(v);

				if (!AUset.isEmpty())
					tts.setVoice(AUset.iterator().next());

				/*for (Voice v: AUset) {
					Log.e("scofield", "v " + v.getName());
					//textView.setText(v.getName());
					tts.setVoice(v);
					tts.speak(editText.getText().toString(),
						TextToSpeech.QUEUE_ADD, null, mUtteranceSpeech);
				}*/
			}
			else if (position == 1) {
				if (!INset.isEmpty())
					tts.setVoice(INset.iterator().next());

				/*for (Voice v: INset) {
					Log.e("scofield", "v " + v.getName());
					//textView.setText(v.getName());
					tts.setVoice(v);
					tts.speak(editText.getText().toString(),
						TextToSpeech.QUEUE_ADD, null, mUtteranceSpeech);
				}*/
			}
			else if (position == 2) {
				if (!UKset.isEmpty())
					tts.setVoice(UKset.iterator().next());
			}
			else if (position == 3) {
				if (!USset.isEmpty())
					tts.setVoice(USset.iterator().next());
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};
	void setupSpinner() {
		spnPrefer = mActivity.findViewById(R.id.spnPrefer);
		//String[] Country = new String[] {"AU", "CA", "UK", "US"};
		ArrayAdapter<String> adapterBalls = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, Country);
		adapterBalls.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnPrefer.setAdapter(adapterBalls);
		spnPrefer.setOnItemSelectedListener(spnPreferListener);
	}

	void configureVoices() {
		//Voice vo1 = tts.getDefaultVoice();
		//Log.e("scofield", "vo1 " + vo1);
		//Voice vo2 = tts.getVoice();
		//Log.e("scofield", "vo2 " + vo2);

		Set<Voice> voice_set = tts.getVoices();
		for (Voice v: voice_set) {
			//Log.e("scofield", "v " + v);
			if (!v.isNetworkConnectionRequired()) {

				if (v.getLocale().getLanguage().equals("en")) {
					//Log.e("scofield", "v " + v.getLocale().getCountry());
							//Log.e("scofield", "v " + v);

					if (v.getLocale().getCountry().equals("AU")) {
						//Log.e("scofield", "AU v " + v);
						AUset.add(v);
					}
					/*else if (v.getLocale().getCountry().equals("CA")) {
						Log.e("scofield", "CA v " + v.getLocale().getCountry());
						CAset.add(v);
					}*/
					else if (v.getLocale().getCountry().equals("IN")) {
						//Log.e("scofield", "IN v " + v);
						INset.add(v);
					}
					else if (v.getLocale().getCountry().equals("GB")) {
						//Log.e("scofield", "UK v " + v);
						UKset.add(v);
					}
					else if (v.getLocale().getCountry().equals("US")) {
						//Log.e("scofield", "US v " + v.getLocale().getCountry());
						USset.add(v);
					}

				}
				//Log.e("scofield", "v " + v.getLocale().getCountry());
			}
		}

		Log.d("scofield", "size of AU set "+AUset.size());
		Log.d("scofield", "size of IN set "+INset.size());
		Log.d("scofield", "size of UK set "+UKset.size());
		Log.d("scofield", "size of US set "+USset.size());

		//float pitch = (float) 1.0;
		//tts.setPitch(pitch);

		//float speechRate = (float) 0.8;
		//tts.setSpeechRate (speechRate);

		//Set<Locale> sl = tts.getAvailableLanguages();
		//for (Locale l0: sl)
			//Log.d("scofield", "l0 "+l0);
	}

	void configureUI() {
		setupSpinner();

		textView = mActivity.findViewById(R.id.textView);

		tts_status = mActivity.findViewById(R.id.TtsStatus);

		editText = mActivity.findViewById(R.id.txt);

		editText.setText(ttsString);

		chooseFile = mActivity.findViewById(R.id.chooseFile);
        View.OnClickListener onClickListener_chooseFile = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e("scofield", "choose file " );
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                //chooseFile.setType("*/*");
                chooseFile.setType("text/plain");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                mActivity.startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        };
        chooseFile.setOnClickListener(onClickListener_chooseFile);

		speech = mActivity.findViewById(R.id.speech);
        View.OnClickListener onClickListener_speech = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("scofield", "use voice " + tts.getVoice());

                tts.speak(editText.getText().toString(),
                        TextToSpeech.QUEUE_ADD, null, mUtteranceSpeech);
            }
        };
        speech.setOnClickListener(onClickListener_speech);

        record = mActivity.findViewById(R.id.record);
        View.OnClickListener onClickListener_record = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                synthesizeToFile(editText.getText().toString());
            }
        };
        record.setOnClickListener(onClickListener_record);
	}

	TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener() {
        @Override
		public void onInit(int status) {
			if (status == TextToSpeech.SUCCESS) {
				Toast.makeText(mActivity, "Initiation of TextToSpeech is successful",
					Toast.LENGTH_SHORT).show();

				tts.setOnUtteranceProgressListener(mUtteranceProgressListener);

				configureVoices();

				configureUI();
			}
			else {
				Toast.makeText(mActivity, "Initiation of TextToSpeech is not successful",
					Toast.LENGTH_SHORT).show();
			}
		}
	};

	public UtteranceProgressListener mUtteranceProgressListener = new UtteranceProgressListener() {
		@Override
		public void onStart(String utteranceId) {
			//Log.d("scofield", "onStart() id:"+utteranceId);
			if (utteranceId.equals(mUtteranceSpeech))
				tts_status.setText("Tts speech started");
			else if (utteranceId.equals(mUtteranceSpeech))
				tts_status.setText("Tts record started");
		}

		@Override
		public void onDone(String utteranceId) {
			//Log.d("scofield", "onDone() id:"+utteranceId);
			if (utteranceId.equals(mUtteranceSpeech))
				tts_status.setText("Tts speech is done");
			else if (utteranceId.equals(mUtteranceSpeech))
				tts_status.setText("Tts record is done");
		}

		@Override
		public void onError(String utteranceId) {
			Log.e("scofield", "onError() id:"+utteranceId);
			tts_status.setText("error!");
		}
	};

	public ScofieldHelper(Activity activity) {
		mActivity = activity;

		tts = new TextToSpeech(mActivity, mInitListener);

		checkStoragePermission();
	}

	public void checkStoragePermission() {
		if (mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			Log.d("scofield", "not WRITE_EXTERNAL_STORAGE ");

			ActivityCompat.requestPermissions(mActivity,
					new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
		}
		else {
Log.d("scofield", "got WRITE_EXTERNAL_STORAGE ");

		}

		if (mActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			Log.d("scofield", "not READ_EXTERNAL_STORAGE ");

			//ActivityCompat.requestPermissions(mActivity,
					//new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
		}
		else {
Log.d("scofield", "got READ_EXTERNAL_STORAGE ");

		}
	}

	public void synthesizeToFile(String ttsString) {
		String envPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		Log.d("scofield", "envPath " + envPath);
		//File downlodDir = Environment.;// or DIRECTORY_PICTURES;
		//Environment.getExternalStoragePublicDirectory(envPath2);// .getAbsolutePath();
		//Log.d("scofield", "envPath2 " + downlodDir.getPath());

		String destFileName = envPath + "/" + mTtsFilename;
		File fileTTS = new File(destFileName);

		// Synthesizes the given text to a file using the specified parameters.
		int sr = tts.synthesizeToFile(ttsString, null,
				fileTTS, mUtteranceRecord);
		//Log.d("scofield", "synthesize returns = " + sr);

		if (fileTTS.exists()) {
			Log.d("scofield", "successfully created fileTTS");
		}
		else {
			Log.d("scofield", "failed while creating fileTTS");
		}
		Uri fileUri= Uri.fromFile(fileTTS);
		Log.d("scofield", "successfully created uri link: " + fileUri.getPath());

		Toast.makeText(mActivity, "Tts voice is recorded successfully at "+ fileUri.getPath(),
				Toast.LENGTH_SHORT).show();
	}
}

