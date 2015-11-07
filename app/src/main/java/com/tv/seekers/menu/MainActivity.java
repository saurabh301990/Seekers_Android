package com.tv.seekers.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.seekers.R;
import com.tv.seekers.activities.FilterActivity;
import com.tv.seekers.constant.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by admin1 on 3/11/15.
 */

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;

    private FragmentDrawer drawerFragment;

    @Bind(R.id.tgl_menu)
    ImageView _tglMenu;

    @Bind(R.id.hdr_title)
    TextView _header;

    @Bind(R.id.hdr_fltr)
    ImageView _rightIcon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        _tglMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerFragment.openDrawer();
            }
        });


        _rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String frag = (String) _header.getText();
                if (frag.equalsIgnoreCase("Map")||frag.equalsIgnoreCase("List")
                        ||frag.equalsIgnoreCase("Followed")
                        ||frag.equalsIgnoreCase("Activity Report")) {
                    startActivity(new Intent(MainActivity.this, FilterActivity.class));
                } else if (frag.equalsIgnoreCase("Draw")) {
                    PlotMapFragment plotMapFragment = new PlotMapFragment();
                    plotMapFragment.saveData(MainActivity.this);
                }
            }
        });

        // display the first navigation drawer view on app launch
        displayView(3);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new MapView();
                _header.setText("Map");
                _rightIcon.setVisibility(View.VISIBLE);
                _rightIcon.setImageResource(R.drawable.filtr);
                break;
            case 1:
                fragment = new Track();
                _header.setText("Followed");
                _rightIcon.setVisibility(View.GONE);
//                _rightIcon.setImageResource(R.mipmap.plus);
                break;
            case 2:
                fragment = new MyKeyWords();
                _header.setText("My Keywords");
                _rightIcon.setVisibility(View.GONE);
//                _rightIcon.setImageResource(R.mipmap.plus);
                break;
            case 3:
                fragment = new Landing();
                _header.setText("My Locations");
                _rightIcon.setVisibility(View.GONE);
                break;
            case 4:
                fragment = new DemoMapFrag();
                _header.setText("Draw");
                _rightIcon.setVisibility(View.VISIBLE);
                _rightIcon.setImageResource(R.mipmap.save);
                break;
            case 5:
                fragment = new ActivityReport();
                _header.setText("Activity Report");
                _rightIcon.setVisibility(View.VISIBLE);
                _rightIcon.setImageResource(R.drawable.filtr);
                break;
            case 6:
                fragment = new Notification();
                _header.setText("Notifications");
                _rightIcon.setVisibility(View.GONE);
                break;
            case 7:
                fragment = new MyProfile();
                _header.setText("Profile");
                _rightIcon.setVisibility(View.GONE);
                break;
            case 8:
                fragment = new LegalContent();
                _header.setText("Legal Content");
                _rightIcon.setVisibility(View.GONE);
                break;
            case 9:
                fragment = new HelpAndSupport();
                _header.setText("Help & Support");
                _rightIcon.setVisibility(View.GONE);
                break;
            case 10:

                /*fragment = new ;
                _header.setText("");*/
                break;
            default:
                break;
        }

        Constant.setFont(this, _header, 0);
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
            // getSupportActionBar().set

        }
    }

    public void changeHeaderText(String msg) {
        _header.setText(msg);
    }
}
