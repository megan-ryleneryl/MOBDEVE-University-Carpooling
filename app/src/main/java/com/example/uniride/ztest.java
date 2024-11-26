package com.example.uniride;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ztest extends AppCompatActivity {
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView7;
    ImageView imageView8;
    ImageView imageView9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ztest);

        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);

        int hatchbackResourceId = R.drawable.hatchback;
        int sedanResourceId = R.drawable.sedan;
        int suvResourceId = R.drawable.suv;
        int vanResourceId = R.drawable.van;
        int mpvResourceId = R.drawable.mpv;

        Log.d("CodeDebug", "Hatchback: " + String.valueOf(hatchbackResourceId));
        Log.d("CodeDebug", "Sedan: " + String.valueOf(sedanResourceId));
        Log.d("CodeDebug", "Suv: " + String.valueOf(suvResourceId));
        Log.d("CodeDebug", "Van: " + String.valueOf(vanResourceId));
        Log.d("CodeDebug", "Mpv: " + String.valueOf(mpvResourceId));

        imageView9.setImageResource(hatchbackResourceId);
    }
}
