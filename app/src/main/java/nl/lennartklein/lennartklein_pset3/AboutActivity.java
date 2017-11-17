package nl.lennartklein.lennartklein_pset3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class AboutActivity extends AppCompatActivity {

    Context mContext;
    BottomNavigationView navigation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        // Set globals
        mContext = this;

        // Set bottom menu
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mainNavigationListener);
        navigation.getMenu().findItem(R.id.navigation_about).setChecked(true);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mainNavigationListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(mContext, MainActivity.class));
                    break;
                case R.id.navigation_order:
                    startActivity(new Intent(mContext, OrderActivity.class));
                    break;
                case R.id.navigation_about:
                    startActivity(new Intent(mContext, AboutActivity.class));
                    break;
            }
            finish();
            return false;
        }
    };
}