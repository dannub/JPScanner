package com.example.user.jpscanner.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;

import com.example.user.jpscanner.CustomTypefaceSpan;
import com.example.user.jpscanner.fragment.Absen;
import com.example.user.jpscanner.fragment.Dashboard;
import com.example.user.jpscanner.model.User;
import com.example.user.jpscanner.parcer.JSONparser2;
import com.example.user.jpscanner.sql.DatabaseHelper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.user.jpscanner.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import me.anwarshahriar.calligrapher.Calligrapher;

/**
 * Created by User on 09/01/2018.
 */

public class UserActivity extends AppCompatActivity{
    private TextView textViewName;
    String scannedData;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public String urlX="https://script.google.com/macros/s/AKfycbw8v-BARGXPK0kdGKbCFnuMiLiZOo9pyn_q7QBXfBlR-wIAkjJU/exec";
    private  NavigationView navigationView;
    private View HeaderView;
    private Fragment fragment;
    private DatabaseHelper databaseHelper;
    private ImageView imageView;
    private ProgressDialog pDialog;
    JSONparser2 jsonParser = new JSONparser2();
    private static final String TAG_SUCCESS = "success";
    int absenb;

    TextView scant,status;
    LinearLayout linearLayout;
    String ip,url_create_product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            try {
                fragment = (Fragment) Dashboard.class.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }


            setContentView(R.layout.activity_user);
        absenb=0;
        databaseHelper =new DatabaseHelper(this);
        ip = databaseHelper.getIP(getIntent().getStringExtra("Email"));
        url_create_product="http://"+ip+"/absen/create_absen.php";

        linearLayout=(LinearLayout)findViewById(R.id.lscan);

        setTitle("Dashboard");
            drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

            actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.Open,R.string.Close);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            navigationView = (NavigationView)findViewById(R.id.navi);
            HeaderView=navigationView.getHeaderView(0);
        status=(TextView)findViewById(R.id.status);
        imageView = (ImageView) findViewById(R.id.scan1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator   integrator = new IntentIntegrator(UserActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setBeepEnabled(false);
                integrator.setCameraId(0);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        scant=(TextView)findViewById(R.id.scant);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setupDrawerContent(navigationView);
            textViewName=(TextView) HeaderView.findViewById(R.id.textemail);
        textViewName.setTypeface(Typeface.createFromAsset(getAssets(),"American_Typewriter_Regular.ttf"));
        new CekKoneksi().execute();
        String nameFromIntent = getIntent().getStringExtra("Email");
            if(nameFromIntent.length()>33)
            {
                nameFromIntent=nameFromIntent.substring(0,32)+"...";
            }
            textViewName.setText(nameFromIntent);
            final Activity activity = this;
            databaseHelper =new DatabaseHelper(activity);
            textViewName=(TextView) HeaderView.findViewById(R.id.text2);
        textViewName.setTypeface(Typeface.createFromAsset(getAssets(),"American_Typewriter_Regular.ttf"));
         nameFromIntent = databaseHelper.getNama(getIntent().getStringExtra("Email")).toUpperCase();
            if(nameFromIntent.length()>20)
            {
                nameFromIntent=nameFromIntent.substring(0,19)+"...";
            }
            textViewName.setText(nameFromIntent);
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this,"American_Typewriter_Regular.ttf",true);

        Menu m = navigationView.getMenu();

        Typeface tf1 = Typeface.createFromAsset(getAssets(), "American_Typewriter_Regular.ttf");

        for (int i=0;i<m.size();i++) {

            MenuItem mi = m.getItem(i);

            SpannableString s = new SpannableString(mi.getTitle());
            s.setSpan(new CustomTypefaceSpan("", tf1), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mi.setTitle(s);

        }


        //time
        Thread myThread = null;

        Runnable myRunnableThread = new CountDownRunner();
        myThread= new Thread(myRunnableThread);
        myThread.start();
        linearLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null) {
            scannedData = result.getContents();
            if (scannedData != null) {
                Log.e(scannedData, "scannedData: ");
                // Here we need to handle scanned data...
                //new SendRequest().execute();
                if(absenb==0)
                {
                    new CreateNewAbsen().execute();
                }
                else if(absenb==1) {
                    new CreateNewAbsen2().execute();
                }
                else {
                    new CreateNewAbsen3().execute();
                }

            }else {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    //dowork

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    TextView txtCurrentTime= (TextView)findViewById(R.id.time);
                    TextView txtCurrentDay= (TextView)findViewById(R.id.tgl);

                    Date dt = new Date();
                    int hours = dt.getHours();
                    int minutes = dt.getMinutes();
                    int seconds = dt.getSeconds();

                    String curTime = String.format("%02d",hours) + ":" + String.format("%02d",minutes) + ":" + String.format("%02d",seconds) ;

                    txtCurrentTime.setText(curTime);
                    String date = dt.toLocaleString();
                    String[] ary = date.split(" ");
                    date=ary[0]+" "+ary[1]+" "+ary[2];
                    txtCurrentDay.setText(date);

                        if((dt.getHours()==6&&dt.getMinutes()>30)||(dt.getHours()>=7&&dt.getHours()<8)) {
                            scant.setText("Scan Berangkat");
                            linearLayout.setVisibility(View.VISIBLE);
                            absenb=0;

                            //.setVisibility(View.GONE);
                        }
                        else if((dt.getHours()==17&&dt.getMinutes()>30)||(dt.getHours()>=18&&dt.getHours()<19)) {
                            scant.setText("Scan Pulang");

                            linearLayout.setVisibility(View.VISIBLE);
                            absenb=1;
                            //scanBtn.setVisibility(View.VISIBLE);
                        }
                        else if((dt.getHours()==19&&dt.getMinutes()>23)||(dt.getHours()>=20&&dt.getHours()<22)) {
                            scant.setText("Scan Lembur");

                            linearLayout.setVisibility(View.VISIBLE);
                            absenb=2;
                            //scanBtn.setVisibility(View.VISIBLE);
                        }
                        else{
                            absenb=0;
                            linearLayout.setVisibility(View.GONE);
                            //scanBtn.setVisibility(View.GONE);
                        }


                }catch (Exception e) {}
            }
        });
    }

    boolean executeCommand(String x){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 "+x);
            int mExitValue = mIpAddrProcess.waitFor();

            if(mExitValue==0){
                runtime.runFinalization();
                mIpAddrProcess.getInputStream();
                mIpAddrProcess.getOutputStream();
                mIpAddrProcess.getErrorStream();
                mIpAddrProcess.exitValue();
                mIpAddrProcess.destroy();
                return true;
            }else{
                runtime.runFinalization();
                mIpAddrProcess.getInputStream();
                mIpAddrProcess.getOutputStream();
                mIpAddrProcess.getErrorStream();
                mIpAddrProcess.exitValue();
                mIpAddrProcess.destroy();
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    class CountDownRunner implements Runnable{
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    doWork();
                    Thread.sleep(1000); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }

    //enddowork


    public void selectItemDrawer(MenuItem item)
    {
        Fragment fragment=null;
        Class fragmentclass = Dashboard.class;
        switch (item.getItemId())
        {
            case R.id.db_btn:
                fragmentclass= Dashboard.class;

                break;
            case R.id.absen :

                fragmentclass= Absen.class;

                break;
            default:
                fragmentclass= Dashboard.class;
                finish();
           break;

        }
        try {
            fragment =(Fragment) fragmentclass.newInstance();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        FragmentManager fragmentManager =getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flayout,fragment).commit();
        item.setChecked(true);
        setTitle(item.getTitle());
        drawerLayout.closeDrawers();
    }
    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                selectItemDrawer(item);
                return true;
            }
        });
    }


    class CreateNewAbsen  extends AsyncTask<String , String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserActivity.this);
            pDialog.setMessage("Tunggu Sebentar..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df2 = new SimpleDateFormat("HH:mm:ss");

            String date = df.format(Calendar.getInstance().getTime());
            String time = df2.format(Calendar.getInstance().getTime());


            params.add(new BasicNameValuePair("id", scannedData));
            params.add(new BasicNameValuePair("tgl", date));
            params.add(new BasicNameValuePair("time", time));
            params.add(new BasicNameValuePair("jenis", "1"));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,"POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    Snackbar.make(findViewById(R.id.flayout), "Absen sukses", Snackbar.LENGTH_LONG).show();
                    // closing this screen

                } else {
                    // failed to create product
                    Snackbar.make(findViewById(R.id.flayout), "Anda Sudah Absen", Snackbar.LENGTH_LONG).show();
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }



            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }


    }

    class CreateNewAbsen2  extends AsyncTask<String , String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserActivity.this);
            pDialog.setMessage("Tunggu Sebentar..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df2 = new SimpleDateFormat("HH:mm:ss");

            String date = df.format(Calendar.getInstance().getTime());
            String time = df2.format(Calendar.getInstance().getTime());


            params.add(new BasicNameValuePair("id", scannedData));
            params.add(new BasicNameValuePair("jenis", "2"));
            params.add(new BasicNameValuePair("tgl", date));
            params.add(new BasicNameValuePair("time", time));

            Log.d("params : ", params.toString());

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);

            // check log cat fro response
            //Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    Snackbar.make(findViewById(R.id.flayout), "Absen sukses", Snackbar.LENGTH_LONG).show();
                    // closing this screen

                } else {
                    // failed to create product
                    Snackbar.make(findViewById(R.id.flayout), "Anda Sudah Absen", Snackbar.LENGTH_LONG).show();
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }



            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }


    }

    class CreateNewAbsen3  extends AsyncTask<String , String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserActivity.this);
            pDialog.setMessage("Tunggu Sebentar..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df2 = new SimpleDateFormat("HH:mm:ss");

            String date = df.format(Calendar.getInstance().getTime());
            String time = df2.format(Calendar.getInstance().getTime());


            params.add(new BasicNameValuePair("id", scannedData));
            params.add(new BasicNameValuePair("jenis", "3"));
            params.add(new BasicNameValuePair("tgl", date));
            params.add(new BasicNameValuePair("time", time));

            Log.d("params : ", params.toString());

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);

            // check log cat fro response
            //Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    Snackbar.make(findViewById(R.id.flayout), "Absen sukses", Snackbar.LENGTH_LONG).show();
                    // closing this screen

                } else {
                    // failed to create product
                    Snackbar.make(findViewById(R.id.flayout), "Anda Sudah Absen", Snackbar.LENGTH_LONG).show();
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }



            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }


    }

    class CekKoneksi extends AsyncTask<Void, Void, Void> {
        int bool=0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */
            pDialog = new ProgressDialog(UserActivity.this);
            pDialog.setTitle("Tunggu Sebentar...");
            pDialog.setMessage("Cek Koneksi");
            pDialog.show();


        }
        protected Void doInBackground(Void... params) {
            if(executeCommand(ip)) {
                bool=1;
            }else
            {
                bool=0;
            }
                return null;
        }

        protected void onPostExecute(Void aVoid) {
            pDialog.dismiss();
            if(bool==1){
                linearLayout.setVisibility(View.VISIBLE);
                status.setTextColor(getResources().getColor(R.color.connect));
                status.setText("Connected");
            }
            else{
                linearLayout.setVisibility(View.GONE);
                status.setTextColor(getResources().getColor(R.color.disconnect));
                status.setText("Disconnect");
            }
        }
    }

}
