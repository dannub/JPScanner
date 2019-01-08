package com.example.user.jpscanner.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.Runnable;

import com.example.user.jpscanner.R;
import com.example.user.jpscanner.activities.UserActivity;
import com.example.user.jpscanner.parcer.JSONparser2;
import com.example.user.jpscanner.sql.DatabaseHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.anwarshahriar.calligrapher.Calligrapher;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Dashboard.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String scannedData;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public TextView txtCurrentDate,txtCurrentTime;
    private OnFragmentInteractionListener mListener;
    private DatabaseHelper databaseHelper;
    private ImageView imageView;
    TextView scant,status2;
    private ProgressDialog pDialog;
    JSONparser2 jsonParser = new JSONparser2();
    private static final String TAG_SUCCESS = "success";
    public int absenb=0;
    String ip,url_create_product;
    LinearLayout linearLayout;
    public Dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Dashboard newInstance(String param1, String param2) {
        Dashboard fragment = new Dashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //time
        Thread myThread = null;

        Runnable myRunnableThread = new CountDownRunner();
        myThread= new Thread(myRunnableThread);
        myThread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        absenb=0;
        Calligrapher calligrapher = new Calligrapher(this.getActivity());
        calligrapher.setFont(this.getActivity(),"American_Typewriter_Regular.ttf",true);
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        status2=(TextView)v.findViewById(R.id.status2);
        txtCurrentDate= v.findViewById(R.id.tgl2);

        //Helper
        databaseHelper =new DatabaseHelper(getActivity());
        ip = databaseHelper.getIP(getActivity().getIntent().getStringExtra("Email"));
        new CekKoneksi().execute();
        System.out.println("ip : "+ip);
        url_create_product="http://"+ip+"/absen/create_absen.php";
        txtCurrentTime=v.findViewById(R.id.time2);
        imageView =  (ImageView) v.findViewById(R.id.scan1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setBeepEnabled(false);
                integrator.setCameraId(0);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        linearLayout=(LinearLayout)v.findViewById(R.id.lscand);

        scant=(TextView)v.findViewById(R.id.scantd);
        linearLayout.setVisibility(View.GONE);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null) {
            scannedData = result.getContents();
            if (scannedData != null ) {
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


    //dowork

    public void doWork() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try{
                     Date dt = new Date();
                    int hours = dt.getHours();
                    int minutes = dt.getMinutes();
                    int seconds = dt.getSeconds();
                    String date = dt.toLocaleString();
                    String[] ary = date.split(" ");
                    date=ary[0]+" "+ary[1]+" "+ary[2];
                    String curTime = String.format("%02d",hours) + ":" + String.format("%02d",minutes) + ":" + String.format("%02d",seconds) ;
                    txtCurrentTime.setText(curTime);
                    txtCurrentDate.setText(date);
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

    class CreateNewAbsen  extends AsyncTask<String , String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
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
            JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    Snackbar.make(getView().findViewById(R.id.dlayout), "Absen sukses", Snackbar.LENGTH_LONG).show();
                    // closing this screen

                } else {
                    // failed to create product
                    Snackbar.make(getView().findViewById(R.id.dlayout), "Anda Sudah Absen", Snackbar.LENGTH_LONG).show();
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
            pDialog = new ProgressDialog(getActivity());
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

                    Snackbar.make(getView().findViewById(R.id.dlayout), "Absen sukses", Snackbar.LENGTH_LONG).show();
                    // closing this screen

                } else {
                    // failed to create product
                    Snackbar.make(getView().findViewById(R.id.dlayout), "Anda Sudah Absen", Snackbar.LENGTH_LONG).show();
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
            pDialog = new ProgressDialog(getActivity());
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

                    Snackbar.make(getView().findViewById(R.id.dlayout), "Absen sukses", Snackbar.LENGTH_LONG).show();
                    // closing this screen

                } else {
                    // failed to create product
                    Snackbar.make(getView().findViewById(R.id.dlayout), "Anda Sudah Absen", Snackbar.LENGTH_LONG).show();
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


    boolean executeCommand(String x){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();

        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 "+x);
            int mExitValue = mIpAddrProcess.waitFor();
            mIpAddrProcess.getInputStream().close();
            mIpAddrProcess.getOutputStream().close();
            mIpAddrProcess.getErrorStream().close();
            System.out.println(" mExitValue "+mExitValue);
            if(mExitValue==0){
                mIpAddrProcess.getInputStream().close();
                mIpAddrProcess.getOutputStream().close();
                mIpAddrProcess.getErrorStream().close();
                return true;
            }else{
                mIpAddrProcess.getInputStream().close();
                mIpAddrProcess.getOutputStream().close();
                mIpAddrProcess.getErrorStream().close();
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;
    }


    class CekKoneksi extends AsyncTask<Void, Void, Void> {
        int bool=0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */
            pDialog = new ProgressDialog(getActivity());
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
            System.out.println("bool : "+bool);
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            pDialog.dismiss();
            if(bool==1){
                linearLayout.setVisibility(View.VISIBLE);
                status2.setTextColor(getResources().getColor(R.color.connect));
                status2.setText("Connected");
            }
            else{
                linearLayout.setVisibility(View.GONE);
                status2.setTextColor(getResources().getColor(R.color.disconnect));
                status2.setText("Disconnect");
            }
        }
    }

}
