package net.ddns.mlsoftlaberge.mlsoft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import net.ddns.mlsoftlaberge.mlsoft.main.MainFragment;
import net.ddns.mlsoftlaberge.mlsoft.contacts.ContactsListFragment;
import net.ddns.mlsoftlaberge.mlsoft.contacts.ContactAdminFragment;
import net.ddns.mlsoftlaberge.mlsoft.contacts.ContactsBudgetFragment;
import net.ddns.mlsoftlaberge.mlsoft.test.IronActivity;
import net.ddns.mlsoftlaberge.mlsoft.test.TrycorderActivity;
import net.ddns.mlsoftlaberge.mlsoft.test.TrycorderFragment;

import net.ddns.mlsoftlaberge.mlsoft.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity
        implements ContactsListFragment.OnContactsInteractionListener,
                    NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    // =====================================================================================
    // preferences values loaded at start
    private boolean tabbedMode;
    private String displayLanguage;
    private String defaultFragment;

    // =====================================================================================
    // fragments holders to keep them in memory
    private MainFragment mainFragment = null;
    private ContactsListFragment contactslistFragment = null;
    private ContactAdminFragment contactadminFragment = null;
    private ContactsBudgetFragment contactsbudgetFragment = null;
    private TrycorderFragment trycorderFragment = null;

    private int currentfragment = 0;
    private Uri currentcontacturi = null;


    // =====================================================================================
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    // the tab layout holder
    private TabLayout tabLayout;

    // will host fragment content when in drawer mode
    private LinearLayout linLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //if (BuildConfig.DEBUG) {
        //    Utils.enableStrictMode();
        //}
        super.onCreate(savedInstanceState);

        // initialize defaults preferences once
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // read the needed preferences for this module
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        displayLanguage = sharedPref.getString("pref_key_display_language", "");
        defaultFragment = sharedPref.getString("pref_key_default_fragment", "");
        tabbedMode = sharedPref.getBoolean("pref_key_tabbed_mode", false);

        if (tabbedMode) {
            // load the initial screen in tabbed mode
            setContentView(R.layout.activity_main_tab);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);


        } else {
            // load the initial screen in drawer mode
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // initialize the drawer to switch between modules
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            // initialize the navigation menu
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            linLayout = (LinearLayout) findViewById(R.id.main_content);
        }

        // prepare a floating button, but hide it for later purpose
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);

    }

    @Override
    public void onStart() {
        super.onStart();
        autostart();
    }

    public void autostart() {
        // initiate the first menu item as auto-selected depending on settings
        if(tabbedMode) {
            if (defaultFragment.equals("Main")) {
                mViewPager.setCurrentItem(0);
            } else if (defaultFragment.equals("Contacts")) {
                mViewPager.setCurrentItem(1);
            } else if (defaultFragment.equals("Person")) {
                mViewPager.setCurrentItem(2);
            } else if (defaultFragment.equals("Budget")) {
                mViewPager.setCurrentItem(3);
            } else if (defaultFragment.equals("Trycorder")) {
                mViewPager.setCurrentItem(4);
            } else if (defaultFragment.equals("TrycorderAct")) {
                mViewPager.setCurrentItem(0);
                trycorderactivity();
            } else if (defaultFragment.equals("Iron")) {
                mViewPager.setCurrentItem(0);
                ironactivity();
            } else if (defaultFragment.equals("Settings")) {
                mViewPager.setCurrentItem(0);
                settingsactivity();
            } else {
                mViewPager.setCurrentItem(0);
            }
            hideSoftKeyboard(mViewPager);
        } else {
            if (defaultFragment.equals("Main")) {
                mainfragment();
            } else if (defaultFragment.equals("Contacts")) {
                contactslistfragment();
            } else if (defaultFragment.equals("Person")) {
                contactadminfragment(null);
            } else if (defaultFragment.equals("Budget")) {
                contactsbudgetfragment();
            } else if (defaultFragment.equals("Trycorder")) {
                trycorderfragment();
            } else if (defaultFragment.equals("TrycorderAct")) {
                mainfragment();
                trycorderactivity();
            } else if (defaultFragment.equals("Iron")) {
                mainfragment();
                ironactivity();
            } else if (defaultFragment.equals("Settings")) {
                mainfragment();
                settingsactivity();
            } else {
                mainfragment();
            }
            hideSoftKeyboard(linLayout);
        }
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    // =====================================================================================
    // section for tabbed mode
    // =====================================================================================

    private int pagePosition=0;

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container,position,object);
            pagePosition=position;
            //if(pagePosition==2) {
            //    if (contactadminFragment != null)
            //        contactadminFragment.setContact(currentcontacturi);
            //}
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0) {
                mainFragment = new MainFragment();
                return mainFragment;
            } else if(position==1) {
                contactslistFragment = new ContactsListFragment();
                return contactslistFragment;
            } else if(position==2) {
                contactadminFragment = ContactAdminFragment.newInstance(currentcontacturi);
                return contactadminFragment;
            } else if(position==3) {
                contactsbudgetFragment = new ContactsBudgetFragment();
                return contactsbudgetFragment;
            } else if(position==4) {
                trycorderFragment = new TrycorderFragment();
                return trycorderFragment;
            }
            mainFragment = new MainFragment();
            return mainFragment;
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Main";
                case 1:
                    return "Contacts";
                case 2:
                    return "Person";
                case 3:
                    return "Budget";
                case 4:
                    return "Trek";
            }
            return null;
        }
    }

    // =====================================================================================
    // section common for drawer and tabbed modes
    // =====================================================================================

    @Override
    public void onBackPressed() {
        if(tabbedMode) {
            int tabno = mViewPager.getCurrentItem();
            if(tabno==2) {
                mViewPager.setCurrentItem(1);
            } else {
                super.onBackPressed();
            }
        } else {
            // to do only in drawer mode
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (currentfragment == 2) {
                    contactslistfragment();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            settingsactivity();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_iron) {
            ironactivity();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_trycorder) {
            trycorderactivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // =====================================================================================
    // settings activity incorporation in the display
    public void settingsactivity() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    // =====================================================================================
    // settings activity incorporation in the display
    public void ironactivity() {
        Intent i = new Intent(this, IronActivity.class);
        startActivity(i);
    }

    // =====================================================================================
    // trycorder activity incorporation in the display
    public void trycorderactivity() {
        Intent i = new Intent(this, TrycorderActivity.class);
        startActivity(i);
    }

    // =====================================================================================
    // section for navigation with a drawer
    // =====================================================================================

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // choose the fragment to instantiate and connect to main content screen
        if (id == R.id.nav_main) {
            mainfragment();
        } else if (id == R.id.nav_contacts) {
            contactslistfragment();
        } else if (id == R.id.nav_person) {
            contactadminfragment(currentcontacturi);
        } else if (id == R.id.nav_budget) {
            contactsbudgetfragment();
        } else if (id == R.id.nav_iron) {
            trycorderfragment();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        // close the drawer to see the selected fragment
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // =====================================================================================
    // second fragment incorporation in the display
    public void mainfragment() {
        //setTitle("Main Fragment");

        if (mainFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            mainFragment = new MainFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            mainFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, mainFragment).commit();
        currentfragment = 0;
    }

    // =====================================================================================
    // contacts fragment incorporation in the display
    public void contactslistfragment() {
        //setTitle("Contacts List");
        if (contactslistFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            contactslistFragment = new ContactsListFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            contactslistFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, contactslistFragment).commit();
        currentfragment = 1;
    }

    // ================================== ContactsList ========================================
    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact has been selected.
     *
     * @param contactUri The contact Uri to the selected contact.
     */
    @Override
    public void onContactSelected(Uri contactUri) {
        currentcontacturi=contactUri;
        if(tabbedMode) {
            mViewPager.setCurrentItem(2);
            if(contactadminFragment!=null) contactadminFragment.setContact(currentcontacturi);
        } else {
            contactadminfragment(contactUri);
        }
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact is no longer selected.
     */
    @Override
    public void onSelectionCleared() {

    }

    @Override
    public boolean onSearchRequested() {
        // Don't allow another search if this activity instance is already showing
        // search results. Only used pre-HC.
        return super.onSearchRequested();
    }


    // =====================================================================================
    // contact administration page
    public void contactadminfragment(Uri contactUri) {
        //setTitle("Contact Detail");
        if (contactUri != null) currentcontacturi = contactUri;
        // create fragment if necessary and switch to it
        contactadminFragment = ContactAdminFragment.newInstance(currentcontacturi);
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        //contactadminFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, contactadminFragment, TAG).commit();
        currentfragment = 2;
    }

    // =====================================================================================
    // contacts budget totalized together
    public void contactsbudgetfragment() {
        //setTitle("Contacts Budget Fragment");

        if (contactsbudgetFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            contactsbudgetFragment = new ContactsBudgetFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            contactsbudgetFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, contactsbudgetFragment).commit();
        currentfragment = 3;
    }

    // =====================================================================================
    // second fragment incorporation in the display
    public void trycorderfragment() {
        //setTitle("Iron Fragment");

        if (trycorderFragment == null) {
            // Create a new Fragment to be placed in the activity layout
            trycorderFragment = new TrycorderFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            trycorderFragment.setArguments(getIntent().getExtras());
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, trycorderFragment).commit();
        currentfragment = 4;
    }


}
