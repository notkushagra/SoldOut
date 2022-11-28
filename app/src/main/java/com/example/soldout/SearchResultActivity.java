package com.example.soldout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

public class SearchResultActivity extends AppCompatActivity {

    SearchView searchBar;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);



        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnClickListener(new handleSearch());

    }


    public class handleSearch implements View.OnClickListener {

        @Override
        public void onClick(View view) {


        }
    }
}