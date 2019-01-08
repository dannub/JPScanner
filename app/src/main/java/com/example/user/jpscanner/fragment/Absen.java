package com.example.user.jpscanner.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.jpscanner.R;
import com.example.user.jpscanner.adapter.MyArrayAdapter2;
import com.example.user.jpscanner.model.MyDataModel2;
import com.example.user.jpscanner.parcer.JSONparser2;
import com.example.user.jpscanner.sql.DatabaseHelper;
import com.example.user.jpscanner.sql.DatabaseHelper2;
import com.google.zxing.integration.android.IntentIntegrator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Absen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Absen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Absen extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public EditText date;
    public Spinner spinner;
    ProgressDialog dialog;
    boolean param=false;
    TextView bad;
    LinearLayout good;
    DatePickerDialog datePickerDialog;
    public DatabaseHelper databaseHelper;
    public DatabaseHelper2 databaseHelper2;
    public String ip;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    // Creating JSON Parser object
    JSONparser2 jParser = new JSONparser2();
    ArrayList<MyDataModel2> absenList;
    private MyArrayAdapter2 adapter;
    private ListView listView;
    public String url_all_products;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ABSEN = "absens";
    private static final String TAG_NAME = "name";
    private static final String TAG_JENIS = "jenis";
    private static final String TAG_TIME = "time";

    // products JSONArray
    JSONArray absen = null;


    public Absen() {
        // Required empty public constructor

    }
    ArrayList<com.example.user.jpscanner.model.Absen>absens;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Absen.
     */
    // TODO: Rename and change types and number of parameters
    public static Absen newInstance(String param1, String param2) {
        Absen fragment = new Absen();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_absen, container, false);
        spinner = v.findViewById(R.id.pilihan);
        List<String> categories = new ArrayList<String>();
        categories.add("Berangkat");
        categories.add("Pulang");
        categories.add("Lembur");

        listView = (ListView) v.findViewById(R.id.listView);
        databaseHelper =new DatabaseHelper(getActivity());
        databaseHelper2 =new DatabaseHelper2(getActivity());

        ip = databaseHelper.getIP(getActivity().getIntent().getStringExtra("Email"));
        new CekKoneksi().execute();
        url_all_products="http://"+ip+"/absen/get_all_products.php";
        Log.e(ip, "ip : " );
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        int i=categories.indexOf("Berangkat");
        spinner.setSelection(i);
        spinner.setOnItemSelectedListener(new myOnItemSelectedListener());


        date = (EditText) v.findViewById(R.id.date);



        // perform click event on edit text
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog

                datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(year+ "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth );
                                Log.e(date.getText().toString(),"date :");
                                //  Loading products in Background Thread

                                if (param) {
                                    //new LoadAllAbsens().execute();
                                    int temp;
                                    if(spinner.getSelectedItemPosition()==0)
                                    {
                                        temp=1;
                                    }
                                    else if(spinner.getSelectedItemPosition()==1){
                                        temp=2;
                                    }
                                    else{
                                        temp=3;
                                    }
                                    databaseHelper2.UpdateData(ip,date.getText().toString(),temp);
                                    new LoadAllAbsens().execute();

                                } else {
                                    Snackbar.make(view, "Connection Not Available", Snackbar.LENGTH_LONG).show();
                                    new LoadAllAbsens().execute();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {

                /**
                 * Checking Internet Connection
                 */
                if (param) {
                    //new LoadAllAbsens().execute();
                    int temp;
                    if(spinner.getSelectedItemPosition()==0)
                    {
                        temp=1;
                    }
                    else if(spinner.getSelectedItemPosition()==1){
                        temp=2;
                    }
                    else{
                        temp=3;
                    }
                    databaseHelper2.UpdateData(ip,date.getText().toString(),temp);
                    new LoadAllAbsens().execute();

                } else {
                    Snackbar.make(view, "Connection Not Available", Snackbar.LENGTH_LONG).show();
                    new LoadAllAbsens().execute();
                }
            }
        });

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

    public final class FragmentIntentIntegrator extends IntentIntegrator {

        private final Fragment fragment;

        public FragmentIntentIntegrator(Fragment fragment) {
            super(fragment.getActivity());
            this.fragment = fragment;
        }



        @Override
        protected void startActivityForResult(Intent intent, int code) {
            fragment.startActivityForResult(intent, code);
        }
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





    boolean executeCommand(String x){
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 "+x);
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+mExitValue);
            if(mExitValue==0){
                return true;
            }else{
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



    class CekKoneksi extends AsyncTask<Void, Void, Void> {
        boolean b=false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("Tunggu Sebentar...");
            dialog.setMessage("Cek Koneksi");
            dialog.show();


        }
        @Nullable
        @Override
        protected Void doInBackground(Void... params) {
            if(executeCommand(ip)){
                param=true;
                b=true;
            }
            else{
                param=false;
                b=false;

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            dialog.dismiss();
            if(b){
                Snackbar.make(getView().findViewById(R.id.parentlayout), "Network Connected", Snackbar.LENGTH_LONG).show();
            }
            else{
                Snackbar.make(getView().findViewById(R.id.parentlayout), "Not Connected Network", Snackbar.LENGTH_LONG).show();

            }
        }
    }


    public class myOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long arg3)     {
            if(!date.getText().toString().matches("")){
                if (param) {

                    int temp;
                    if(spinner.getSelectedItemPosition()==0)
                    {
                        temp=1;
                    }
                    else if(spinner.getSelectedItemPosition()==1){
                        temp=2;
                    }
                    else{
                        temp=3;
                    }
                    databaseHelper2.UpdateData(ip,date.getText().toString(),temp);
                    new LoadAllAbsens().execute();



                } else {
                    Snackbar.make(view, "Connection Not Available", Snackbar.LENGTH_LONG).show();
                    new LoadAllAbsens().execute();
                }
            }
            else{
                Snackbar.make(view, "Choose Date", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> view) {        }
    }


    class LoadAllAbsens extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */
            absenList = new ArrayList<>();
            /**
             * Binding that List to Adapter
             */
            adapter = new MyArrayAdapter2(getActivity(), absenList);

            /**
             * Getting List and Setting List Adapter
             */
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Snackbar.make(getView().findViewById(R.id.parentlayout), absenList.get(position).getnama() + absenList.get(position).getjenis()+" pada "+ absenList.get(position).gettime(), Snackbar.LENGTH_LONG).show();
                }
            });

            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading Please wait...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            ///List<NameValuePair> params = new ArrayList<NameValuePair>();
            ///params.add(new BasicNameValuePair("tgl", date.getText().toString()));
            int temp;
            if(spinner.getSelectedItemPosition()==0)
            {
                temp=1;
            }
            else if(spinner.getSelectedItemPosition()==1){
                temp=2;
            }
            else{
                temp=3;
            }
            ///params.add(new BasicNameValuePair("jenis",Integer.toString(temp)));

            // getting JSON string from URL
            ///JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

            // Check your log cat for JSON reponse
            ///Log.d("All Products: ", json.toString());

            // Checking for SUCCESS TAG
            ///int success = json.getInt(TAG_SUCCESS);

            ///if (success == 1) {
            // products found
            // Getting Array of Products
            /// absen = json.getJSONArray(TAG_ABSEN);

            absens= (ArrayList<com.example.user.jpscanner.model.Absen>) databaseHelper2.getAllAbsen(date.getText().toString(),temp);
            // looping through All Products
            for (int i = 0; i < absens.size(); i++) {
                 MyDataModel2 model = new MyDataModel2();

                /// JSONObject c = absen.getJSONObject(i);
                /// String jenis;
                 // Storing each json item in variable
                 ///if(c.getInt(TAG_JENIS)==1){
                  ///   jenis = " berangkat";}else
                 ///{
                    /// jenis = " pulang";
                 //}
                 ///String time = c.getString(TAG_TIME);
                 ///String name = c.getString(TAG_NAME);
                 ///int bts=31-3-time.length();
                 ///name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                 ///if(name.length()>bts)
                 ///{
                    /// name=name.substring(0,bts-1)+"...";
                /// }

                 int jenis=absens.get(i).getJenis_Abs();
                String name=absens.get(i).getName_Abs();
                String time=absens.get(i).getTime_Abs();
                String jenis2;
                 if(jenis==1){
                      jenis2 = " berangkat";}
                      else if(jenis==2){
                     jenis2 = " pulang";
                    }
                    else{
                     jenis2 = "lembur";
                 }
                 model.setjenis(jenis2);
                 model.setnama(name);
                 model.settime(time);
                 // creating new HashMap
                 //HashMap<String, String> map = new HashMap<String, String>();

                 // adding each child node to HashMap key => value
                 //map.put(TAG_JENIS, jenis);
                 //map.put(TAG_NAME, name);

                 //map.put(TAG_TIME, time);

                 // adding HashList to ArrayList
                 absenList.add(model);
            }
            //} else {
            // no products found
            // Launch Add New product Activity

            //  Snackbar.make(getView().findViewById(R.id.parentLayout), "No Data Found", Snackbar.LENGTH_LONG).show();
            //}

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            dialog.dismiss();
            // updating UI from Background Thread
            if (absenList.size() > 0) {
                adapter.notifyDataSetChanged();

            } else {
                Snackbar.make(getView().findViewById(R.id.parentlayout), "No Data Found", Snackbar.LENGTH_LONG).show();
            }

        }

    }
}