package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;

    private String stringURLEndPoint = "https://api.openai.com/v1/chat/completions";

    private String stringAPIKey = "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";

    private String stringOutput = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("model","gpt-3.5-turbo");

                    JSONArray jsonArrayMessage = new JSONArray();
                    JSONObject jsonObjectMessage = new JSONObject();

                    jsonObjectMessage.put("role","user");
                    jsonObject.put("content","Write a poem about clouds");
                    jsonArrayMessage.put(jsonObjectMessage);
                    jsonObject.put("messages",jsonArrayMessage);


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, stringURLEndPoint,jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String stringText = null;
                        try {
                            stringText = response.getJSONArray("choices")
                                    .getJSONObject( 0)
                                    .getJSONObject( "message")
                                    .getString( "content");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        stringOutput = stringOutput + stringText;
                        textView.setText(stringOutput);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    public Map<String,String> getHeaders() throws AuthFailureError {

                        Map<String,String> mapHeader = new HashMap<>();
                        mapHeader.put( "Authorization", "Bearer " + stringAPIKey);
                        mapHeader.put( "Content-Type","application/json");

                        return mapHeader;
                    }

                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }

                };


                int intTimeoutPeriod = 60000;
                RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

                jsonObjectRequest.setRetryPolicy(retryPolicy);

                Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);

            }
        });







    }
}