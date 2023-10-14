package com.example.fyp;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchWeb extends AppCompatActivity
{
    ListView lvWeb;
    String[] webName = {"What Is Anxiety Disorder?","Health Lifestyle Management","Doing What Matters in Times of Stress"
    };
    String[] webDescription ={"When it comes to anxiety, all of us will experience the symptoms at least a few times in our lives. Whether it is the nervous flutter of butterflies in your stomach before speak..."
           ,"Is stress making you frustrated and irritable? Stress relievers can help restore calm and serenity to your chaotic life. You don't have to invest a lot of time or thought into stress relievers..."
    ,"Doing What Matters in Times of Stress: An Illustrated Guide is a stress management guide for coping with adversity. The guide aims to equip people with practical skills to help cope with stress..."
    };
    int[] webImages ={R.drawable.image1,R.drawable.image2,R.drawable.image3};
    String[] urls = {"https://isha.sadhguru.org/us-en/anxiety-attacks/?gclid=CjwKCAjw586hBhBrEiwAQYEnHQf_b9-CbGbhxtfXoA8lbA-0sgeQizTeQKmrpK4nHSOi7bE05yHAJBoCZ9oQAvD_BwE"
    ,"https://www.mayoclinic.org/healthy-lifestyle/stress-management/in-depth/stress-relievers/art-20047257"
    ,"https://www.who.int/publications/i/item/9789240003927?gclid=CjwKCAjw586hBhBrEiwAQYEnHb-csoD4wBswiaYuQmSqVwIKKgXBJUUiF5ayagy-HrekqfDRjGg-HBoCI50QAvD_BwE"
    };

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchweb);
        lvWeb = findViewById(R.id.lvWeb);
        WebAdapter webAdapter = new WebAdapter(this,webName,webImages,webDescription,urls);
        lvWeb.setAdapter(webAdapter);
    }
}
