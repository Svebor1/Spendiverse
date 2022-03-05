package com.example.spendiverse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        TextView linkoviSlika = findViewById(R.id.linkovi_slike);
        String[] linkovi;
        linkovi=getResources().getStringArray(R.array.linkovi);
        for (int i = 0;i<5; i++){
            String link = linkovi[i];
            linkoviSlika.append(link+"\n");


        }
        String link = "<a href=\"https://docs.google.com/document/d/11NUHVgrftwssezebnh2vltJPckV9ikwInwA-Y7nhjgk/edit?usp=sharing\">Pravila o privatnosti</a>";
        TextView pravilaPrivatnosti = findViewById(R.id.pravila_o_privatnosti);
        pravilaPrivatnosti.setMovementMethod(LinkMovementMethod.getInstance());

        pravilaPrivatnosti.setText((Spanned)Html.fromHtml(link));

        linkoviSlika.setMovementMethod(LinkMovementMethod.getInstance());


    }

}