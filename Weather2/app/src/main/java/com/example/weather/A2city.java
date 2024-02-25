package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.weather.databinding.ActivityA2cityBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class A2city extends AppCompatActivity {

    private ActivityA2cityBinding binding;

    public boolean x=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityA2cityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        String city1=getIntent().getStringExtra("city1");
        String weather1=getIntent().getStringExtra("weather1");
        binding.userFild1.setText(city1);
        x=true;
        binding.tVanswer1.setText(weather1);
       // if(!city1.equals("")) new GetURL().execute("https://api.openweathermap.org/data/2.5/weather?q="+binding.userFild1.getText().toString()+"&appid=cde6ce1422ba67d4a8db763ee58d254a&units=metric&lang=ru");
        //binding.tVanswer1.setText(weather1.trim());


        binding.button2.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   x=false;
                                                   if (binding.userFild2.getText().toString().trim().equals(""))
                                                       Toast.makeText(A2city.this, "Введите город", Toast.LENGTH_LONG).show();
                                                   else {
                                                       String url="https://api.openweathermap.org/data/2.5/weather?q="+binding.userFild2.getText().toString().trim()+"&appid=cde6ce1422ba67d4a8db763ee58d254a&units=metric&lang=ru";
                                                       new GetURL().execute(url);
                                                   }
                                               }
                                           }
        );

        binding.button1.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   x=true;
                                                   if (binding.userFild1.getText().toString().trim().equals(""))
                                                       Toast.makeText(A2city.this, "Введите город", Toast.LENGTH_LONG).show();
                                                   else {
                                                       String url="https://api.openweathermap.org/data/2.5/weather?q="+binding.userFild1.getText().toString().trim()+"&appid=cde6ce1422ba67d4a8db763ee58d254a&units=metric&lang=ru";
                                                       new GetURL().execute(url);
                                                   }
                                               }
                                           }
        );

        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(A2city.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class GetURL extends AsyncTask<String, String, String> {

        protected void onPreExecute(){
            super.onPreExecute();
            if(!x) binding.tVanswer2.setText("Загрузка данных...");
            else binding.tVanswer1.setText("Загрузка данных...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try{
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = null;

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(result==null){
                if(!x) binding.tVanswer2.setText("Неверно введён город");
                else binding.tVanswer1.setText("Неверно введён город");
                return;
            }

            try{
                JSONObject obj = new JSONObject(result);

                if(!x) {
                    if(obj.getJSONObject("wind").has("gust"))
                        binding.tVanswer2.setText("Температура " + obj.getJSONObject("main").getDouble("temp") + "\n" + "Ощущается как "
                                + obj.getJSONObject("main").getDouble("feels_like") + "\n" + "Ветер "
                                + obj.getJSONObject("wind").getDouble("speed") + "\n" + " Порывы до "
                                + obj.getJSONObject("wind").getDouble("gust")+ "\n" +obj.getString("name"));
                    else
                        binding.tVanswer2.setText("Температура " + obj.getJSONObject("main").getDouble("temp") + "\n" + "Ощущается как "
                                + obj.getJSONObject("main").getDouble("feels_like") + "\n" + "Ветер "
                                + obj.getJSONObject("wind").getDouble("speed"));
                }
                else {
                    if (obj.getJSONObject("wind").has("gust"))
                        binding.tVanswer1.setText("Температура " + obj.getJSONObject("main").getDouble("temp") + "\n" + "Ощущается как "
                                + obj.getJSONObject("main").getDouble("feels_like") + "\n" + "Ветер "
                                + obj.getJSONObject("wind").getDouble("speed") + "\n" + " Порывы до "
                                + obj.getJSONObject("wind").getDouble("gust") + "\n" + obj.get("name"));
                    else
                        binding.tVanswer1.setText("Температура " + obj.getJSONObject("main").getDouble("temp") + "\n" + "Ощущается как "
                                + obj.getJSONObject("main").getDouble("feels_like") + "\n" + "Ветер "
                                + obj.getJSONObject("wind").getDouble("speed"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if(!x)
                    binding.tVanswer2.setText("Упс, есть неполадки");
                else binding.tVanswer1.setText("Ошибочка вышла");

            }
        }
    }
}
//                String x = obj.getString("cod");
//                if(x=="404") binding.answer.setText("404");
// binding.answer.setText("name"+obj.get("message"));
//                if(obj.getDouble("cod")==404) {
//                    binding.answer.setText("Неверно введён город!");
//                }