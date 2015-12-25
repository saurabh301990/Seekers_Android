package com.tv.seekers.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tv.seekers.R;
import com.tv.seekers.activities.ChangePassword;
import com.tv.seekers.activities.TermsAndConditions;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.gpsservice.GPSTracker;
import com.tv.seekers.uploadimg.ContentType;
import com.tv.seekers.uploadimg.UploadRequest;
import com.tv.seekers.uploadimg.UploadService;
import com.tv.seekers.utils.CircleBitmapDisplayer;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shoeb on 4/11/15.
 */
public class MyProfile extends Fragment {

    private boolean isChecked = false;
    @Bind(R.id.checkbox_terms)
    ImageView checkbox_terms;

    @OnClick(R.id.checkbox_terms)
    public void checkbox_click(View view) {
        if (isChecked) {
            isChecked = false;
            checkbox_terms.setBackgroundResource(R.mipmap.unchecked_box);
        } else {
            isChecked = true;
            checkbox_terms.setBackgroundResource(R.mipmap.checked_box);
        }

    }

    @Bind(R.id.term_tv)
    TextView term_tv;

    @OnClick(R.id.term_tv)
    public void term_tv(View view) {
        startActivity(new Intent(getActivity(), TermsAndConditions.class));
    }

    @Bind(R.id.sav_prof_iv)
    ImageView sav_prof_iv;

    @OnClick(R.id.sav_prof_iv)
    public void sav_prof_iv(View view) {

        if (validData()) {
            editor.putBoolean("ISAGREED", true);
            editor.commit();
            if (isImgChoosed) {
                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                    isImgChoosed = false;
                    callSaveWS();
                } else {
                    Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
                }
            } else {
                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                    callSaveWSOnlyParam();
                } else {
                    Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
                }
            }

        }

    }


    private void callSaveWSOnlyParam() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {
                Constant.showLoader(getActivity());
                builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id)
                        .appendQueryParameter("image", "")
                        .appendQueryParameter("cur_lat", latitude + "")
                        .appendQueryParameter("cur_long", longitude + "")
                        .appendQueryParameter("fullname", name);
            }

            @Override
            protected String doInBackground(String... arg0) {
                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                    try {
                        HttpURLConnection urlConnection;
                        String query = builder.build().getEncodedQuery();
                        //String temp=URLEncoder.encode(uri, "UTF-8");
                        URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.UPDATE_USER_PROF));
                        urlConnection = (HttpURLConnection) ((url.openConnection()));
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        urlConnection.setUseCaches(false);
                        urlConnection.setChunkedStreamingMode(1024);
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setReadTimeout(30 * 1000);

//                        urlConnection.setRequestProperty("Content-Type","application/json");
                        urlConnection.connect();

                        //Write
                        OutputStream outputStream = urlConnection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        writer.write(query);
                        writer.close();
                        outputStream.close();

                        //Read
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            //System.out.println("Uploading............");
                            sb.append(line);
                        }
                        bufferedReader.close();
                        _responseMain = sb.toString();
                        System.out.println("Response of Update Profile : " + _responseMain);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast("Server Error ", getActivity());
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
                        JSONObject _JsonObject = new JSONObject(_responseMain);
                        int status = _JsonObject.getInt("status");
                        if (status == 1) {
                            Constant.showToast(_JsonObject.getString("message"), getActivity());
                            name_et.setFocusable(false);
                            name_et.setFocusableInTouchMode(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Constant.hideLoader();
                        Constant.showToast("Server Error ", getActivity());

                    }
                } else {
                    Constant.hideLoader();
                    Constant.showToast("Server Error ", getActivity());
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            _Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
        } else {
            _Task.execute((String[]) null);
        }
    }

    private boolean validData() {
        name = name_et.getText().toString().trim();
        boolean isSuccess = false;
        if (name == null || name.equalsIgnoreCase("")) {
            isSuccess = false;
            Constant.showToast("Please enter your name.", getActivity());
        } else if (!isChecked) {
            isSuccess = false;
            Constant.showToast(getActivity().getResources().getString(R.string.pleaseAgreeTermsText), getActivity());
        } else {
            isSuccess = true;
        }
        return isSuccess;
    }


    private void callSaveWS() {


        System.out.println("SD CARD : " + fileToUploadPath);


        final UploadRequest request = new UploadRequest(getActivity(),
                UUID.randomUUID().toString()
                , WebServiceConstants.getMethodUrl(WebServiceConstants.UPDATE_USER_PROF));

        //and parameters

        request.addParameter("user_id", user_id);
        request.addParameter("cur_lat", latitude + "");
        request.addParameter("cur_long", longitude + "");
        request.addParameter("fullname", name);

        request.addFileToUpload(fileToUploadPath, "image", System.currentTimeMillis() + ".jpeg", ContentType.APPLICATION_OCTET_STREAM);


        System.err.println("request of Upload img : " + request.toString());
        request.setNotificationConfig(R.mipmap.app_icon, getString(R.string.app_name),
                getString(R.string.uploading), getString(R.string.upload_success),
                getString(R.string.upload_error), false);

        try {
            UploadService.startUpload(request);
            Constant.showToast(getString(R.string.uploading), getActivity());
        } catch (Exception exc) {
            Constant.showToast("Malformed upload request. ", getActivity());
        }
    }


    @Bind(R.id.user_img_iv)
    ImageView user_img_iv;

    @OnClick(R.id.user_img_iv)
    public void img_click(View view) {
        selectCam();
    }

    private static final int RESULT_LOAD_IMG = 1;
    private static final int TAKE_PHOTO_CODE = 0;
    File sdImageMainDirectory;
    Uri outputFileUri;

    private void selectCam() {
        final CharSequence[] options = {"Image From Camera", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Select Photo" + "!");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Image From Camera")) {

                    File root = new File(Environment
                            .getExternalStorageDirectory()
                            + File.separator + "SeekersImages" + File.separator);
                    root.mkdirs();

                    sdImageMainDirectory = new File(root, "SeekersProfilePicture_" + System.currentTimeMillis() + ".png");

                    outputFileUri = Uri.fromFile(sdImageMainDirectory);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

                } else if (options[item].equals("Choose From Gallery")) {
//                    Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                    // Create intent to Open Image applications like Gallery, Google Photos
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }

        });

        builder.show();
    }

    @Bind(R.id.nameInfo_tv)
    TextView nameInfo_tv;

    @Bind(R.id.name_et)
    EditText name_et;
    private boolean isEditOn = false;

    @Bind(R.id.emailInfo_tv)
    TextView emailInfo_tv;

    @Bind(R.id.email_et)
    EditText email_et;

    @Bind(R.id.userNameInfo_tv)
    TextView userNameInfo_tv;

    @Bind(R.id.username_et)
    EditText username_et;


    @Bind(R.id.main_layout)
    LinearLayout main_layout;
    @Bind(R.id.change_pswrd_btn)
    ImageView change_pswrd_btn;

    @OnClick(R.id.change_pswrd_btn)
    public void change_pswrd_btn(View view) {
        Intent intent = new Intent(getActivity(), ChangePassword.class);
        intent.putExtra("IMG_URL", imageURL);
        startActivity(intent);

    }

    @Bind(R.id.map_view)
    RelativeLayout map_view;


    @Bind(R.id.progress_bar)
    ProgressBar progress_bar;

    //map Related
    private GoogleMap googleMap;
    CameraPosition cameraPosition;
    private SupportMapFragment fragment;
    Marker marker;
    private double latitude = 0.0;
    private double longitude = 0.0;
    LatLng _latLong;
    GPSTracker gps;

    private String user_id = "";
    private String name = "";
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;
    private DisplayImageOptions options;
    com.nostra13.universalimageloader.core.ImageLoader imageLoaderNew;
    private String imageURL = "";
    private String fileToUploadPath = "";
    private boolean isImgChoosed = false;
    private boolean ISAGREED = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile_screen, container, false);

        ButterKnife.bind(this, view);
        setFont();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
        imageLoaderNew = com.nostra13.universalimageloader.core.ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.profile_pic)
                .showImageForEmptyUri(R.drawable.profile_pic)
                .showImageOnFail(R.drawable.profile_pic)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer())
                        //				.displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();


        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        editor = sPref.edit();
        user_id = sPref.getString("id", "");
        ISAGREED = sPref.getBoolean("ISAGREED", false);
        if (ISAGREED) {
            isChecked = true;
            checkbox_terms.setBackgroundResource(R.mipmap.checked_box);

        } else {
            isChecked = false;
            checkbox_terms.setBackgroundResource(R.mipmap.unchecked_box);
        }


        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callGetMyProfile();
        } else {
            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
        }

        main_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Constant.hideKeyBoard(getActivity());
                return false;
            }
        });

        ImageView menu;
        menu = (ImageView) getActivity().findViewById(R.id.tgl_menu);
        menu.setVisibility(View.VISIBLE);
        MainActivity.drawerFragment.setDrawerState(true);


        name_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (name_et.getRight() - name_et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

//                        Constant.showToast("Clicked on Edit ICON.", getActivity());
                        name_et.setFocusable(true);
                        name_et.setFocusableInTouchMode(true);
                        isEditOn = true;
                        return true;
                    } else {
                        if (!isEditOn) {
                            name_et.setFocusable(false);
                            name_et.setFocusableInTouchMode(false);
                        }

                    }
                }
                return false;
            }
        });
        return view;
    }

    private void setFont() {
        Constant.setFont(getActivity(), nameInfo_tv, 0);
        Constant.setFont(getActivity(), name_et, 0);
        Constant.setFont(getActivity(), emailInfo_tv, 0);
        Constant.setFont(getActivity(), email_et, 0);
        Constant.setFont(getActivity(), userNameInfo_tv, 0);
        Constant.setFont(getActivity(), username_et, 0);
        Constant.setFont(getActivity(), term_tv, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();
    }


    private void callGetMyProfile() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {
                Constant.showLoader(getActivity());
                builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id);
            }

            @Override
            protected String doInBackground(String... arg0) {
                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                    try {
                        HttpURLConnection urlConnection;
                        String query = builder.build().getEncodedQuery();
                        //String temp=URLEncoder.encode(uri, "UTF-8");
                        URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_USER_PROFILE));
                        urlConnection = (HttpURLConnection) ((url.openConnection()));
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        urlConnection.setUseCaches(false);
                        urlConnection.setChunkedStreamingMode(1024);
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setReadTimeout(30 * 1000);
                        urlConnection.connect();

                        //Write
                        OutputStream outputStream = urlConnection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        writer.write(query);
                        writer.close();
                        outputStream.close();

                        //Read
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                        String line = null;
                        StringBuilder sb = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            //System.out.println("Uploading............");
                            sb.append(line);
                        }
                        bufferedReader.close();
                        _responseMain = sb.toString();
                        System.out.println("Response of My Profile : " + _responseMain);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast("Server Error ", getActivity());
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
                        JSONObject _JsonObject = new JSONObject(_responseMain);
                        int status = _JsonObject.getInt("status");
                        if (status == 1) {
                            JSONObject user_details = _JsonObject.getJSONObject("user_details");
                            String id = user_details.getString("id");
                            String username = user_details.getString("username");
                            String firstname = user_details.getString("firstname");
                            String lastname = user_details.getString("lastname");
                            String fullname = user_details.getString("fullname");
                            String email = user_details.getString("email");
                            String password = user_details.getString("password");
                            String role_type = user_details.getString("role_type");
                            String gender = user_details.getString("gender");
                            String mobile_no = user_details.getString("mobile_no");
                            String lat = user_details.getString("lat");
                            String _long = user_details.getString("long");
                            String address = user_details.getString("address");
                            imageURL = user_details.getString("image");
                            imageURL = WebServiceConstants.IMAGE_URL + imageURL;

                            email_et.setText(email);
                            name_et.setText(fullname);
                            username_et.setText(username);

                            imageLoaderNew.displayImage(imageURL, user_img_iv,
                                    options, new SimpleImageLoadingListener() {
                                        @Override
                                        public void onLoadingStarted(String imageUri, View view) {
                                            progress_bar.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                            progress_bar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            progress_bar.setVisibility(View.GONE);
                                        }
                                    });

                            if (_long != null && !_long.equalsIgnoreCase("")
                                    && lat != null && !lat.equalsIgnoreCase("")) {
                                latitude = Double.parseDouble(lat);
                                longitude = Double.parseDouble(_long);
                                showMap();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Constant.showToast("Server Error ", getActivity());
                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error ", getActivity());
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_view);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_view, fragment).commit();
        }
    }

    private void initMap() {
        gps = new GPSTracker(getActivity());
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            if (!String.valueOf(latitude).equalsIgnoreCase("0.0") &&
                    !String.valueOf(longitude).equalsIgnoreCase("0.0")) {

                try {
                    // Loading map
                    if (googleMap == null) {
                        googleMap = fragment.getMap();
                        googleMap.setMyLocationEnabled(true);

                        showMap();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                gps.showSettingsAlert();
            }
        } else {
            gps.showSettingsAlert();
        }
    }

    private void showMap() {
        _latLong = new LatLng(latitude, longitude);
        cameraPosition = new CameraPosition.Builder().target(_latLong)
                .zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
      /*  if (marker != null) {
            marker.remove();
        }
        marker = googleMap.addMarker(new MarkerOptions()
                .position(_latLong).icon(BitmapDescriptorFactory.
                        defaultMarker(BitmapDescriptorFactory.HUE_RED)));*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult Called For My Profile");
//        Constant.showToast("onActivityResult Called", getActivity());
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK
                    && null != data) {
                // Get the Image from data
                isImgChoosed = true;
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                fileToUploadPath = cursor.getString(columnIndex);
                System.out.println("File Path of Img From Gallery : " + fileToUploadPath);


                imageLoaderNew.displayImage("file://" + fileToUploadPath, user_img_iv,
                        options,
                        null);

                cursor.close();


            } else if (requestCode == TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
                // Get the Image from data
//                Constant.showToast("onActivityResult Called Success", getActivity());
                isImgChoosed = true;
                /*Uri selectedImage = outputFileUri;
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);*/
//                fileToUploadPath = cursor.getString(columnIndex);
                fileToUploadPath = outputFileUri.toString();

                fileToUploadPath = fileToUploadPath.replace("file://", "");
//                        "file://" + /storage/emulated/0/Rotate/11242015174423.jpg
                System.out.println("File Path of Img From Camera : " + fileToUploadPath);
                imageLoaderNew.displayImage("file://" + fileToUploadPath, user_img_iv,
                        options,
                        null);

//                cursor.close();
            } else {
                isImgChoosed = false;
            }
        } catch (Exception e) {
            isImgChoosed = false;

            Constant.showToast("Something went wrong" , getActivity());
        }


    }
}
