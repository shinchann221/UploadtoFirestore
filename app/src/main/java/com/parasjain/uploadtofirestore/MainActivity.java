package com.parasjain.uploadtofirestore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RequestQueue mQueue;
    EditText text;
    String url="";
    FirebaseFirestore db = FirebaseFirestore.getInstance ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonParse = findViewById(R.id.uploadbtn);
        text = findViewById (R.id.text);
        int i = 0;

        mQueue = Volley.newRequestQueue(this);

        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                url = text.getText().toString();
                jsonParse();
            }
        });
    }

    private void jsonParse() {

        if (url.isEmpty ()) {
            Toast.makeText (this, "Enter File url first", Toast.LENGTH_SHORT).show ();
        } else {
            JsonObjectRequest request = new JsonObjectRequest (Request.Method.GET, url, null,
                    new Response.Listener<JSONObject> () {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray (/*Enter Heading type String to get JSON file*/);

                                for (int i = 0; i < jsonArray.length (); i++) {
                                    JSONObject employee = jsonArray.getJSONObject (i);

                                    //Change these variables to Get Data
                                    String name = employee.getString ("Name");
                                    int price = Integer.parseInt(employee.getString ("Price"));
                                    String image = employee.getString("Image");

                                    //Change this Map to Enter Data according to Variables

                                    Map<String, Object> user = new HashMap<> ();
                                    user.put ("Name", name);
                                    user.put ("Price", price);
                                    user.put("Image",image);
// Add a new document with a generated ID
                                    db.collection (//Add Document Path)
                                            .set (user)
                                            .addOnSuccessListener (new OnSuccessListener<Void> () {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText (MainActivity.this, "UPLOADED DATA", Toast.LENGTH_SHORT).show ();
                                                }
                                            })
                                            .addOnFailureListener (new OnFailureListener () {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w ("upload", "Error adding document", e);
                                                }
                                            });

                                }
                            } catch (JSONException e) {
                                e.printStackTrace ();
                            }
                        }
                    }, new Response.ErrorListener () {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace ();
                }
            });

            mQueue.add (request);
        }
    }
}