package com.example.asuper.cryptowatcher;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    TextView mainText;
    TextView priceUsd;
    TextView priceEur;

    TextView percent_change_1h;
    TextView percent_change_24h;
    TextView percent_change_7d;

    TextView percent_change_1h_text;
    TextView percent_change_24h_text;
    TextView percent_change_7d_text;

    TextView changeMarket;

    private ProgressBar spinner;

    private Spinner curencySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        Button ethereum = (Button) findViewById(R.id.ethereum);

        mainText = (TextView) findViewById(R.id.mainText);
        priceUsd = (TextView) findViewById(R.id.price_usd);
        priceEur = (TextView) findViewById(R.id.price_eur);

        percent_change_1h = (TextView) findViewById(R.id.percent_change_1h);
        percent_change_24h = (TextView) findViewById(R.id.percent_change_24h);
        percent_change_7d = (TextView) findViewById(R.id.percent_change_7d);

        percent_change_1h_text = (TextView) findViewById(R.id.percent_change_1h_text);
        percent_change_24h_text = (TextView) findViewById(R.id.percent_change_24h_text);
        percent_change_7d_text = (TextView) findViewById(R.id.percent_change_7d_text);

        changeMarket = (TextView) findViewById(R.id.change_market);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);

        curencySpinner = (Spinner) findViewById(R.id.currency_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        curencySpinner.setAdapter(adapter);

        curencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    return;
                else {
                    String selectedCrypto = (String) parent.getItemAtPosition(position);
                    AsyncRunner runner = new AsyncRunner();
                    runner.execute(selectedCrypto);
                    Log.w(selectedCrypto, "done");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncRunner runner = new AsyncRunner();
                runner.execute("bitcoin");
            }
        });

        ethereum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncRunner runner = new AsyncRunner();
                runner.execute("ethereum");
            }
        });
    }

    private class AsyncRunner extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {

            Request request = new Request.Builder()
                    .url("https://api.coinmarketcap.com/v1/ticker/" + strings[0] + "/?convert=EUR")
                    .build();

            String gotResponse = "";
            String curencyname = "";
            String currencyPriceinUsd = "";
            String currencyPriceinEur = "";

            String percent_change_1h = "";
            String percent_change_24h = "";
            String percent_change_7d = "";


            String[] currencyStats = new String[15];

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                //Headers responseHeaders = response.headers();
                gotResponse = response.body().string();
                JSONArray jsonArray = new JSONArray(gotResponse);
                JSONObject simpleObject = (JSONObject) jsonArray.get(0);
                curencyname = simpleObject.getString("name");
                currencyPriceinUsd = simpleObject.getString("price_usd");
                currencyPriceinEur = simpleObject.getString("price_eur");
                percent_change_1h = simpleObject.getString("percent_change_1h");
                percent_change_24h = simpleObject.getString("percent_change_24h");
                percent_change_7d = simpleObject.getString("percent_change_7d");

                currencyStats[0] = curencyname;
                currencyStats[1] = currencyPriceinUsd + " USD";
                currencyStats[2] = currencyPriceinEur + " EUR";
                currencyStats[3] = percent_change_1h;
                currencyStats[4] = percent_change_24h;
                currencyStats[5] = percent_change_7d;

            }
            catch (Exception e) {
                e.printStackTrace();
                currencyStats[0] = "error";
            }
            return currencyStats;
        }

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String[] result) {
            spinner.setVisibility(View.GONE);
            if (result[0] == "error") {
                mainText.setText("Connection Error. Please connect to interent and try later.");
            }
            else {
                mainText.setText(result[0]);
                priceUsd.setText(result[1]);
                priceEur.setText(result[2]);


                percent_change_1h.setText(result[3]);
                percent_change_24h.setText(result[4]);
                percent_change_7d.setText(result[5]);

                setPercentColor(percent_change_1h, result[3]);
                setPercentColor(percent_change_24h, result[4]);
                setPercentColor(percent_change_7d, result[5]);

                percent_change_1h_text.setText("1 hour");
                percent_change_24h_text.setText("24 hours");
                percent_change_7d_text.setText("7 days");

                changeMarket.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setPercentColor(TextView view, String result) {
        if (Double.parseDouble(result) < 0){
            view.setTextColor(Color.RED);
        }
        else {
            view.setTextColor(Color.GREEN);
        }
    }
}
