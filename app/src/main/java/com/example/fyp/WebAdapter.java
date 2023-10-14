package com.example.fyp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class WebAdapter extends ArrayAdapter<String> {
    Context context;
    int[] images;
    String[] programName;
    String[] programDescription;
    String[] urls;

    public WebAdapter(Context context, String[] programName, int[] images, String[] programDescription,String[] urls) {
        super(context, R.layout.single_item, R.id.textView1, programName);
        this.context = context;
        this.images = images;
        this.programName = programName;
        this.programDescription = programDescription;
        this.urls = urls;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View singleItem = convertView;
        WebViewHolder holder = null;
        if(singleItem == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.single_item, parent, false);
            holder = new WebViewHolder(singleItem);
            singleItem.setTag(holder);
        }
        else{
            holder = (WebViewHolder) singleItem.getTag();
        }
        holder.itemImage.setImageResource(images[position]);
        holder.webTitle.setText(programName[position]);
        holder.webDescription.setText(programDescription[position]);
        singleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openLinksIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls[position]));
                context.startActivity(openLinksIntent);
            }
        });
        return singleItem;
    }
}
