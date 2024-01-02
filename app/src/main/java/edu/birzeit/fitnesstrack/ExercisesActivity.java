package edu.birzeit.fitnesstrack;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExercisesActivity extends AppCompatActivity {

    private ListView listExercises;
    private ArrayAdapter<String> adapter;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        listExercises = findViewById(R.id.listExercises);
        requestQueue = Volley.newRequestQueue(this);

        retrieveExercisesFromAPI();
    }

    private void retrieveExercisesFromAPI() {
        String apiUrl = "https://api-ninjas.com/api/exercises";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<String> exerciseList = parseJsonResponse(response);
                        displayExercises(exerciseList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                "Error while fetching exercise data",
                                Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private ArrayList<String> parseJsonResponse(JSONArray jsonArray) {
        ArrayList<String> exerciseList = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject exerciseObj = jsonArray.getJSONObject(i);
                String exerciseName = exerciseObj.getString("name");
                exerciseList.add(exerciseName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return exerciseList;
    }

    private void displayExercises(ArrayList<String> exercises) {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exercises);
        listExercises.setAdapter(adapter);
    }
}