package com.hao.diyviewpracticedemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hao.diyviewpracticedemo.widget.SuperDividerItemDecoration;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> list = new ArrayList<>();
    private Context context = this;

    {
        list.add("画线循迹-Path的使用");
        list.add("雷达图加动画实现");
        list.add("数独");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.rv_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new SuperDividerItemDecoration.Builder(context).setDividerWidth(1).setDividerColor(Color.GRAY).build());
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new ViewHolder(LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewHolder holder1 = (ViewHolder) holder;
                holder1.textView.setText(list.get(position));
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getLayoutPosition()){
                        case 0:
                            startActivity(new Intent(context, PathViewActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(context, RadarViewActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(context, SudukuActivity.class));
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

}
