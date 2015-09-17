package com.traveljar.memories.video;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.traveljar.memories.R;
import com.traveljar.memories.models.MakeRequest;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MakeVideoRequest extends AppCompatActivity {

    protected static final String TAG = "<MakeVideoRequest>";
    ListView audioListView;
    EditText etVideoLen;
    CharSequence[] songs=  new CharSequence[3];
    private static final int ID_ACTION_ITEM_NEXT = 0;
    MakeVideoRequestAdapter  makeVideoRequestAdapter;
    ArrayList<MakeRequest> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_video_request);
        songs =  getResources().getStringArray(R.array.video_length_items);
        setUpToolBar();
        etVideoLen= (EditText) findViewById(R.id.et_video_len);
        etVideoLen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

// TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(MakeVideoRequest.this);
                builder.setTitle("Select video length").setItems(songs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
                        etVideoLen.setText(songs[which]);
                    }
                });
                builder.show();
            }
        });
        setMakeVideoRequestData();
        audioListView = (ListView) findViewById(R.id.make_video_request);


        makeVideoRequestAdapter = new MakeVideoRequestAdapter(this,data);
        audioListView.setAdapter(makeVideoRequestAdapter);
    }


    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Make Video");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView submit = (TextView)toolbar.findViewById(R.id.action_done);
        submit.setText("Submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "submit button clicked");
                /*Intent i = new Intent(getApplicationContext(), CurrentJourneyBaseActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);*/
                if(validateForm())
                    generateTimecapsule();
            }
        });
    }

    public boolean validateForm() {
        if(etVideoLen.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please choose video length", Toast.LENGTH_LONG).show();
            return false;
        }

        for(int i=0;i<data.size();i++)
        {
            if(data.get(i).isChecked())
            {
                Toast.makeText(getApplicationContext(), "Audio selected", Toast.LENGTH_LONG).show();
                return true;
            }
            else {
                Toast.makeText(getApplicationContext(), "Please select audio", Toast.LENGTH_LONG).show();
                return false;
            }

        }
        return true;
    }

    public void generateTimecapsule() {
        String uploadRequestTag = "TIMECAPSULE_GENERATE";
        Map<String, String> params = new HashMap<>();

        // put the parameters here
        //params.put("note[updated_at]", String.valueOf(note.getUpdatedAt()));
        Log.d(TAG, "in GENERATE TIMECAPSULE METHOD...............//////////////////.............");
        String url = Constants.URL_TIMECAPSULE_GENERATE + "?api_key=" + TJPreferences.getApiKey(getApplicationContext())
                + "&j_id=" + TJPreferences.getActiveJourneyId(getApplicationContext());
        Log.d(TAG, url);
        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.GET, url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "got response...................");
                        Log.d(TAG, "response from server = " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "no response.....................");
                Log.d(TAG, "error in generating timecapsule video" + error);
            }
        });
        AppController.getInstance().addToRequestQueue(uploadRequest, uploadRequestTag);
   /*     Intent i = new Intent(getApplicationContext(), CurrentJourneyBaseActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);*/
        finish();
    }


   public void setMakeVideoRequestData() {
       data = new ArrayList<MakeRequest>();
       MakeRequest dto1 = new MakeRequest();
       dto1.setName("ABCD 2- Bezubaan");
       dto1.setAudioResource(R.raw.song1);
       data.add(dto1);

       MakeRequest dto2 = new MakeRequest();
       dto2.setName("Jai Ho");
       dto2.setAudioResource(R.raw.song2);
       data.add(dto2);

       MakeRequest dto3 = new MakeRequest();
       dto3.setName("Chak de");
       dto3.setAudioResource(R.raw.song3);
       data.add(dto3);

   }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

}
