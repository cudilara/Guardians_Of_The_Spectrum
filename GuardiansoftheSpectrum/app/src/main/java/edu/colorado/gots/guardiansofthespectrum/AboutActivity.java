package edu.colorado.gots.guardiansofthespectrum;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class AboutActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_about);
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
   }
}