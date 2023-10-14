package com.example.fyp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WebViewHolder {
    ImageView itemImage;
    TextView webTitle;
    TextView webDescription;

    WebViewHolder(View view) {
        itemImage = view.findViewById(R.id.imageView);
        webTitle = view.findViewById(R.id.textView1);
        webDescription = view.findViewById(R.id.textView2);
    }
}
