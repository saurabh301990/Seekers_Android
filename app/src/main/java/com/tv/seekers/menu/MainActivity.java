package com.tv.seekers.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.login.LoginActivity;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by admin1 on 3/11/15.
 */

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;

    public static FragmentDrawer drawerFragment;

    @Bind(R.id.tgl_menu)
    ImageView _tglMenu;

    @Bind(R.id.hdr_title)
    TextView _header;

    @Bind(R.id.hdr_fltr)
    ImageView _rightIcon;

    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;

    // In your activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        ErrorReporter.getInstance().Init(MainActivity.this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        drawerFragment.setDrawerState(false);

        _tglMenu.setVisibility(View.VISIBLE);

        _tglMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerFragment.openDrawer();
            }
        });

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(MainActivity.this));

        /*_rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String frag = (String) _header.getText();
                if (frag.equalsIgnoreCase("Map") || frag.equalsIgnoreCase("List")
                        *//*|| frag.equalsIgnoreCase("Followed")*//*
                        || frag.equalsIgnoreCase("Activity Report")) {
                    startActivity(new Intent(MainActivity.this, FilterActivity.class));
                } else if (frag.equalsIgnoreCase("Draw")) {
                    PlotMapFragment plotMapFragment = new PlotMapFragment();
                    plotMapFragment.saveData(MainActivity.this);
                }
            }
        });*/

        sPref = getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        editor = sPref.edit();
        editor.putString("userLocationType", "");
        editor.putString("LOCATIONID", "");
        editor.commit();


        // display the first navigation drawer view on app launch
        displayView(1, false);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
        displayView(position, true);
    }

    private void displayView(int position, boolean isDrawOption) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new MapView();
                _header.setText("Map");
                Bundle mBundleMapView = new Bundle();
                mBundleMapView.putBoolean("FROMMENU", true);
                fragment.setArguments(mBundleMapView);
                _rightIcon.setVisibility(View.VISIBLE);
                _rightIcon.setImageResource(R.drawable.filtr);
                break;
            case 1:

                fragment = new DemoMapFrag();
                _header.setText("Draw Area");
                _rightIcon.setVisibility(View.VISIBLE);
                _rightIcon.setImageResource(R.mipmap.save);

                break;
            case 2:



               /* fragment = new MyKeyWords();
                _header.setText("My Keywords");
                _rightIcon.setVisibility(View.GONE);
//                _rightIcon.setImageResource(R.mipmap.plus);*/
                fragment = new MyAreasFrag();
                Bundle mBundle = new Bundle();
                mBundle.putBoolean("isDrawOption", isDrawOption);

                fragment.setArguments(mBundle);
//                fragment = new Landing();
                _header.setText("Choose Location");
                _rightIcon.setVisibility(View.GONE);
                break;
            case 3:


                fragment = new Track();
                _header.setText("Saved Profiles");
                _rightIcon.setVisibility(View.VISIBLE);
                _rightIcon.setImageResource(R.mipmap.plus);
                break;
            case 4:

                fragment = new Notification();
                _header.setText("Notifications");
                _rightIcon.setVisibility(View.GONE);

                break;
            case 5:

                fragment = new ActivityReport();
                _header.setText("Activity Report");
                _rightIcon.setVisibility(View.VISIBLE);
                _rightIcon.setImageResource(R.drawable.filtr);
                break;
            case 6:

                fragment = new LegalContent();
                _header.setText("Legal Correspondence");
                _rightIcon.setVisibility(View.GONE);

                break;
            case 7:

                fragment = new MyProfile();
                _header.setText("Profile");
                _rightIcon.setVisibility(View.GONE);
                break;
            case 8:
                fragment = new HelpAndSupport();
                _header.setText("Help & Support");
                _rightIcon.setVisibility(View.GONE);
                break;
            case 9:
                logout();
                break;
           /* case 10:

                *//*fragment = new ;
                _header.setText("");*//*
                break;*/
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

    private void logout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);


        alertDialog.setMessage("Are you sure want to Logout ?");


        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                if (NetworkAvailablity.checkNetworkStatus(MainActivity.this)) {
                    callLogOutWs();
                } else {
                    Constant.showToast(getResources().getString(R.string.internet), MainActivity.this);
                }


            }
        });

        // Setting Positive "Yes" Button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();


    }

    private void callLogOutWs() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";


            @Override
            protected void onPreExecute() {
                Constant.showLoader(MainActivity.this);

            }

            @Override
            protected String doInBackground(String... arg0) {
                if (NetworkAvailablity.checkNetworkStatus(MainActivity.this)) {
                    try {

                        URL url;
                        HttpURLConnection urlConnection = null;


                        try {

                            url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.LOGOUT));
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
                            int responseCode = urlConnection.getResponseCode();

                            if (responseCode == 200) {
                                _responseMain = readStream(urlConnection.getInputStream());
                                System.out.println("Response of LOGOUT : " + _responseMain);

                            } else {
                                Log.v("My area", "Response code:" + responseCode);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (urlConnection != null)
                                urlConnection.disconnect();
                        }


                        //						makeRequest(WebServiceConstants.getMethodUrl(WebServiceConstants.METHOD_UPDATEVENDER), jsonObj.toString());
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", MainActivity.this);
                            }
                        });

                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast("Server Error ", MainActivity.this);
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                Constant.hideLoader();
                if (_responseMain != null && !_responseMain.equalsIgnoreCase("")) {
                    try {
                        JSONObject jo = new JSONObject(_responseMain);
                        int status = jo.getInt("status");
                        if (status == 1) {

                            clearData();

                        } else if (status == 0) {
                            Constant.showToast("Server Error", MainActivity.this);
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(MainActivity.this);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Constant.hideLoader();
                    }
                } else {
                    Constant.hideLoader();
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            _Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
        } else {
            _Task.execute((String[]) null);
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private void clearData() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void changeHeaderText(String msg) {
        _header.setText(msg);
    }
}
