package net.ddns.mlsoftlaberge.mlsoft.test;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by mlsoft on 16-05-13.
 */
public class TrycorderActivity extends FragmentActivity {

    private static String TAG="Trycorder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // start the fragment full screen
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(android.R.id.content, new TrycorderFragment(), TAG);
        ft.commit();
    }

}
