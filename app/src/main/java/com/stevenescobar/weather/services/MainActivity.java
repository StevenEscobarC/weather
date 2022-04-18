package com.stevenescobar.weather.services;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stevenescobar.weather.R;
import com.stevenescobar.weather.services.model.Country;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final String API_KEY = "43f10646f654b95d8def2b1977cc3a2c";
    private WeatherService service = null;
    private TextView labTemp = null;
    private TextView labTempMin = null;
    private TextView labTempMax = null;
    private AutoCompleteTextView txtCountryISOCode = null;
    private EditText txtCityName = null;
    private Button btnGetWeather = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        service = new WeatherService(API_KEY);

        initViews();
        initEvents();
    }

    public void initViews(){
        labTemp = findViewById(R.id.lblTemp);
        labTempMin = findViewById(R.id.lblTempMin);
        labTempMax = findViewById(R.id.lblTempMax);
        txtCityName = findViewById(R.id.txtCityName);
        txtCountryISOCode = (AutoCompleteTextView) findViewById(R.id.txtCountryISOCode);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Country.getCountryNames(Locale.getDefault().getLanguage()));
        txtCountryISOCode.setAdapter(adapter);
        btnGetWeather = findViewById(R.id.button);

    }

    public void initEvents(){
        btnGetWeather.setOnClickListener(view -> {
            String txtCountry = Country.getISOCodeByName(txtCountryISOCode.getText().toString(),Locale.getDefault().getLanguage());
            Log.d("ISO ", txtCountry);
            Log.d("Lenguaje", Locale.getDefault().getLanguage());
            String txtCity = txtCityName.getText().toString();
            getWeatherInfoOnClick(txtCity, txtCountry);
        });
    }


    public void getWeatherInfoOnClick(String txtCity, String txtCountry){
        service.requestWeatherData(txtCity, txtCountry, (isNetworkError,statusCode, root) -> {
            if(!isNetworkError){
                switch(statusCode){
                    case 404:
                        Log.d("Weather", "City not found aa");
                        break;
                    case 200:
                        runOnUiThread(()->{
                            labTemp.setText(String.valueOf(root.getMain().getTemp()));
                            labTempMin.setText(String.valueOf(root.getMain().getTempMin()));
                            labTempMax.setText(String.valueOf(root.getMain().getTempMax()));
                        });
                        break;
                }
            }
        });
    }


}