package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.weather.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String gpsLat = null;
    private String gpsLon = null;
    private String gpsCity = null;

    public static final byte set = 1;
    public static final byte gps = 2;

    private boolean vibor_gps = false;

    private LocationManager locationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.btbtbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.userFild.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.tip, Toast.LENGTH_LONG).show();
                else {
                    String city = binding.userFild.getText().toString().trim();
                    //String key="cde6ce1422ba67d4a8db763ee58d254a";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=cde6ce1422ba67d4a8db763ee58d254a&units=metric&lang=ru";
                    new GetURL().execute(url);
                }
            }
        }
        );
        binding.bCityadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //между какими активити связь
                Intent intent = new Intent(MainActivity.this, A2city.class);
                //ключ и получаемое значение
                intent.putExtra("city1", binding.userFild.getText().toString());
                //String url="https://api.openweathermap.org/data/2.5/weather?q="+binding.userFild.getText().toString()+"&appid=cde6ce1422ba67d4a8db763ee58d254a&units=metric&lang=ru";
                intent.putExtra("weather1", binding.answer.getText().toString());
                startActivity(intent);
                finish();
            }
        });

        registerForContextMenu(binding.buttonGps);
       // ContextMenu menu = ;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        binding.buttonGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // menu.add(0,1,0,"По сети");

                String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + gpsLat + "&lon=" + gpsLon + "&appid=cde6ce1422ba67d4a8db763ee58d254a&units=metric&lang=ru";
                new GetURL().execute(url);
                //binding.userFild.setText(gpsCity);
            }
        });
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,v,menuInfo);
        menu.add(0, set, 0, "По сети");
        menu.add(0, gps, 0, "По GPS");

    }

    public  boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case set:
                vibor_gps = true;
                break;
            case gps:
                vibor_gps = false;
                break;
            default:
                return super.onContextItemSelected(item);
        }
       return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (!vibor_gps) locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 1, locationListener);
        else locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 1,
                locationListener);
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            if (provider.equals(LocationManager.GPS_PROVIDER)) {
//              .setText("Status: " + String.valueOf(status));
//            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
//                .setText("Status: " + String.valueOf(status));
//            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        gpsLat = formatLocationLat(location);
        gpsLon = formatLocationLon(location);
//        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
//            gpsLat = formatLocationLat(location);
//            gpsLon = formatLocationLon(location);
//        } else if (location.getProvider().equals(
//                LocationManager.NETWORK_PROVIDER)) {
//            gpsLat = formatLocationLat(location);
//            gpsLon = formatLocationLon(location);
//        }
    }

    private String formatLocationLat(Location location) {
        if (location == null)
            return "";
        return String.format(
                "%1$.4f",
                location.getLatitude());
    }

    private String formatLocationLon(Location location) {
        if (location == null)
            return "";
        return String.format(
                "%1$.4f",
                 location.getLongitude());
    }

    private void checkEnabled() {
//        tvEnabledGPS.setText("Enabled: "
//                + locationManager
//                .isProviderEnabled(LocationManager.GPS_PROVIDER));
//        tvEnabledNet.setText("Enabled: "
//                + locationManager
//                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

//    public void onClickLocationSettings(View view) {
//        startActivity(new Intent(
//                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//    };

    private class GetURL extends AsyncTask<String, String, String> {

        protected void onPreExecute() {//запрос данных по адресу, ожидание ответа
            super.onPreExecute();
            binding.answer.setText("Загрузка данных...");

        }


        @Override//JSON объект
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();//соединение
                connection.connect();//подсоединились

                InputStream stream = connection.getInputStream();// получение инфы с адреса
                reader = new BufferedReader(new InputStreamReader(stream));//в строчной форме закинули считанный поток

                StringBuffer buffer = new StringBuffer();
                String line = null;

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                // throw new RuntimeException(e);
            } catch (IOException e) {
                //String s="ff";
                // throw new RuntimeException(e);
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
        protected  void onPostExecute(String result){
            super.onPostExecute(result);
            if(result==null) {
                binding.answer.setText("Неверно введён город");
                return;
            }
            String s=null;

            try {
                JSONObject obj = new JSONObject(result);
//                String x = obj.getString("cod");
//                if(x=="404") binding.answer.setText("404");
                // binding.answer.setText("name"+obj.get("message"));
//                if(obj.getDouble("cod")==404) {
//                    binding.answer.setText("Неверно введён город!");
//                }
//                else
                binding.answer.setText(null);
                gpsCity = obj.getString("name");
                if(obj.getJSONObject("wind").has("gust"))
                    binding.answer.setText("Температура " + obj.getJSONObject("main").getDouble("temp")+"\n"+"Ощущается как "
                            +obj.getJSONObject("main").getDouble("feels_like")+"\n"+"Ветер "
                            +obj.getJSONObject("wind").getDouble("speed")+ "\n" +" Порывы до "
                            +obj.getJSONObject("wind").getDouble("gust")+"\n"+obj.getJSONObject("coord").getDouble("lon")+"\n"+obj.getJSONObject("coord").getDouble("lat")+ "\n" + obj.get("name"));
                else binding.answer.setText("Температура " + obj.getJSONObject("main").getDouble("temp")+"\n"+"Ощущается как "
                        +obj.getJSONObject("main").getDouble("feels_like")+"\n"+"Ветер "
                        +obj.getJSONObject("wind").getDouble("speed")+ "\n" + obj.get("name"));
            } catch (JSONException e) {
                e.printStackTrace();
                binding.answer.setText("Упс, сервер заболел");
            }
        }
    }
}