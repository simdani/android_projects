package com.example.asuper.retrofit_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.asuper.retrofit_example.api.model.GithubRepo;
import com.example.asuper.retrofit_example.api.service.GithubClient;
import com.example.asuper.retrofit_example.ui.GithubRepoAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button randomUserButton;
    private EditText getUsernameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.repos);
        randomUserButton = (Button) findViewById(R.id.randomUser);
        getUsernameView = (EditText) findViewById(R.id.enterUsername);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        final GithubClient client = retrofit.create(GithubClient.class);

        randomUserButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                getUserRepos(client, getUsernameView.getText().toString());
            }
        });
    }

    private void getUserRepos(GithubClient client, String username) {
        Call<List<GithubRepo>> call = client.reposForUser(username);

        call.enqueue(new Callback<List<GithubRepo>>() {
            @Override
            public void onResponse(Call<List<GithubRepo>> call, Response<List<GithubRepo>> response) {
                List<GithubRepo> repos = response.body();
                if (repos != null) {
                    listView.setAdapter(new GithubRepoAdapter(MainActivity.this, repos));
                } else {
                    listView.setAdapter(null);
                    Toast.makeText(MainActivity.this, "No repos found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GithubRepo>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
