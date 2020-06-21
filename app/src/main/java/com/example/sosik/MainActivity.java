package com.example.sosik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static boolean activityTop;
    private RecyclerView recyclerView;
    private ArrayList<String> list = new ArrayList<>();
    private Button btnTTS;
    private EditText txtTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeService();
        InitializeView();
        SetListener();

        /*for (int i=0; i<100; i++) {
            list.add(String.format("TEXT %d", i));
        }*/
    }

    // 모션센서 서비스 실행
    public void InitializeService(){
        Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
        startService(serviceIntent);
    }

    public void InitializeView(){
        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = findViewById(R.id.recycler1) ;
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        SimpleTextAdapter adapter = new SimpleTextAdapter(list) ;
        recyclerView.setAdapter(adapter) ;

        btnTTS = (Button) findViewById(R.id.btnTTS);
        txtTTS = (EditText) findViewById(R.id.txtTTS);
    }

    public void SetListener(){
        btnTTS.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnTTS:
                String text;
                text = txtTTS.getText().toString();
                if (text.length() == 0) {
                    return;
                }else{
                    txtTTS.setText("");
                    list.add(String.format(text));
                    recyclerView.getLayoutManager().onRestoreInstanceState(recyclerView.getLayoutManager().onSaveInstanceState());
                    recyclerView.scrollToPosition(recyclerView.getLayoutManager().getItemCount()-1);
                }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        activityTop = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityTop = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityTop = false;
    }

    @Override
    protected void onStop(){
        super.onStop();
        activityTop = false;
    }

    public static boolean isActivityVisible() {
        return activityTop;
    }
}
