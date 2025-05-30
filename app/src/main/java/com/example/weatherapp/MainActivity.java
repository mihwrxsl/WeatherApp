package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView cityNameText, temperatureText, humidityText, descriptionText, windText, pressureText;
    private ImageView weatherIcon;
    private EditText cityNameInput;
    private static final String API_KEY = "a4401f72013d4f03993477eaa85f9c19";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityNameText = findViewById(R.id.cityNameText);
        temperatureText = findViewById(R.id.temperatureText);
        humidityText = findViewById(R.id.humidityText);
        windText= findViewById(R.id.windText);
        descriptionText = findViewById(R.id.descriptionText);
        weatherIcon = findViewById(R.id.weatherIcon);
        pressureText = findViewById(R.id.pressureText);
        Button refreshButton = findViewById(R.id.fetchWeatherButton);
        cityNameInput = findViewById(R.id.cityNameInput);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = cityNameInput.getText().toString();
                if(!cityName.isEmpty())
                {
                    FetchWeatherData(cityName);
                }
                else
                {
                    cityNameInput.setError("Please enter a city name");
                }
            }
        });



        FetchWeatherData("Moscow");
    }

    private void FetchWeatherData(String cityName) {

        String url = "https:api.openweathermap.org/data/2.5/weather?q="+ cityName + "&appid="+ API_KEY + "&units=metric";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->
                {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    try {
                        Response response = client.newCall(request).execute();
                        String result = response.body().string();
                        runOnUiThread(() -> updateUI(result));
                    } catch (IOException e)
                    {
                        e.printStackTrace();;
                    }
                }
        );
    }

    private void updateUI(String result)
    {
        if(result != null)
        {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main =  jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
                double pressure = main.getDouble("pressure");

                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");

                cityNameText.setText(jsonObject.getString("name"));
                temperatureText.setText(String.format("%.0f°", temperature));
                humidityText.setText(String.format("%.0f%%", humidity));
                windText.setText(String.format("%.0f km/h", windSpeed));
                pressureText.setText(String.format("%.0f hPa", pressure));
                descriptionText.setText(description);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}