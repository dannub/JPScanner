package com.example.user.jpscanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.example.user.jpscanner.R;
import com.example.user.jpscanner.helper.InputValidation;
import com.example.user.jpscanner.model.User;
import com.example.user.jpscanner.sql.DatabaseHelper;

import me.anwarshahriar.calligrapher.Calligrapher;

/**
 * Created by User on 09/01/2018.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity=RegisterActivity.this;
    private NestedScrollView nestedScrollView;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutIP;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextIP;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;

    private AppCompatButton appCompatButtonRegister;

    private AppCompatTextView textViewLinkLogin;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this,"American_Typewriter_Regular.ttf",true);
        initViews();
        initListener();
        initObjects();
    }

    private void initObjects() {
        inputValidation= new InputValidation(activity);
        databaseHelper = new DatabaseHelper(activity);
        user =new User();
    }

    private void initListener() {
        appCompatButtonRegister.setOnClickListener(this);
        textViewLinkLogin.setOnClickListener(this);
    }

    private void initViews() {

        nestedScrollView = (NestedScrollView) findViewById(R.id.scrollView);
        textInputLayoutName = (TextInputLayout) findViewById(R.id.layoutnama);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.layoutemail);
        textInputLayoutIP = (TextInputLayout) findViewById(R.id.layoutip);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.layoutpasword);
        textInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.layoutkonpasword);
        textInputEditTextName = (TextInputEditText) findViewById(R.id.txtnama);
        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.txtemail);
        textInputEditTextIP = (TextInputEditText) findViewById(R.id.txtip);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.txtpassword);
        textInputEditTextConfirmPassword = (TextInputEditText) findViewById(R.id.txtkonpassword);
        appCompatButtonRegister= (AppCompatButton)findViewById(R.id.buttonregister);
        textViewLinkLogin= (AppCompatTextView) findViewById(R.id.linklogin);

    }
    @Override
    public void onClick (View v)
    {
        switch (v.getId())
        {
            case R.id.buttonregister :
                postDataToSQLite();
                break;
            case R.id.linklogin :
                finish();
                break;
        }
    }

    private void postDataToSQLite() {
        if(!inputValidation.isInputFilled(textInputEditTextName,textInputLayoutName,"Nama Kosong"))
        {
            return;
        }
        if(!inputValidation.isInputFilled(textInputEditTextEmail,textInputLayoutEmail,"Email Kosong"))
        {
            return;
        }
        if(!inputValidation.isInputEditTextEmail(textInputEditTextEmail,textInputLayoutEmail,"Email Salah"))
        {
            return;
        }
        if(!inputValidation.isInputFilled(textInputEditTextIP,textInputLayoutIP,"IP Kosong"))
        {
            return;
        }
        if(!inputValidation.isInputEditTextIP(textInputEditTextIP,textInputLayoutIP,"IP Salah"))
        {
            return;
        }
        if(!inputValidation.isInputFilled(textInputEditTextPassword,textInputLayoutPassword,"Pasword Kosong")){
            return;
        }
        if(!inputValidation.isInputEditTextMatches("jp123",textInputEditTextConfirmPassword,textInputLayoutConfirmPassword,"Special Password Salah")){
            return;
        }
        if(!databaseHelper.checkuser(textInputEditTextEmail.getText().toString().trim(),textInputEditTextPassword.getText().toString().trim())){
            user.setName(textInputEditTextName.getText().toString().trim());
            user.setEmail(textInputEditTextEmail.getText().toString().trim());
            user.setPassword(textInputEditTextPassword.getText().toString().trim());

            user.setIP(textInputEditTextIP.getText().toString().trim());
            databaseHelper.addUser(user);
            Snackbar.make(nestedScrollView,"Data sukses Ditambah",Snackbar.LENGTH_LONG).show();
             emptyInputEditText();
            Intent akunIntent = new Intent(activity, MainActivity.class);
            startActivity(akunIntent);

        }
        else {
            Snackbar.make(nestedScrollView,"Email atau Password Telah digunakan",Snackbar.LENGTH_LONG).show();
        }
    }

    private void emptyInputEditText() {
        textInputEditTextName.setText(null);
        textInputEditTextConfirmPassword.setText(null);
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
        textInputEditTextIP.setText(null);
    }
}
