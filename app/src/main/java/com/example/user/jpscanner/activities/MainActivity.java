package com.example.user.jpscanner.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.example.user.jpscanner.R;
import com.example.user.jpscanner.helper.InputValidation;
import com.example.user.jpscanner.sql.DatabaseHelper;

import me.anwarshahriar.calligrapher.Calligrapher;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity=MainActivity.this;
    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private AppCompatTextView textViewLinkRegister;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this,"American_Typewriter_Regular.ttf",true);
        initViews();
        initListener();
        initObjects();
    }

    private void initObjects() {
        databaseHelper =new DatabaseHelper(activity);
        inputValidation=new InputValidation(activity);
    }

    private void initListener() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }


    private void initViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScroll);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.layoutemail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.layoutpassword);
        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.txtemail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.txtpassword);
        appCompatButtonLogin= (AppCompatButton) findViewById(R.id.buttonlogin);
        textViewLinkRegister= (AppCompatTextView) findViewById(R.id.linkregister);

    }

    @Override
    public void onClick (View v)
    {
        switch (v.getId()){
            case R.id.buttonlogin :
                verifyFromSQLite();
                break;
            case R.id.linkregister :
                Intent intentRegister = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }

    private void verifyFromSQLite() {
        if(!inputValidation.isInputFilled(textInputEditTextEmail,textInputLayoutEmail,"Email Kosong"))
        {
            return;
        }
        if(!inputValidation.isInputEditTextEmail(textInputEditTextEmail,textInputLayoutEmail,"Email Salah"))
        {
            return;
        }
        if(!inputValidation.isInputFilled(textInputEditTextPassword,textInputLayoutPassword,"Pasword Kosong")){
            return;
        }
        if(databaseHelper.checkuser(textInputEditTextEmail.getText().toString().trim()
                ,textInputEditTextPassword.getText().toString().trim())){
            Intent akunIntent = new Intent(activity, UserActivity.class);
            akunIntent.putExtra("Email",textInputEditTextEmail.getText().toString().trim());
            emptyInputEditText();
            startActivity(akunIntent);
        }
        else {
            Snackbar.make(nestedScrollView,"Email atau Password Salah",Snackbar.LENGTH_SHORT).show();
        }
    }

    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }


}
