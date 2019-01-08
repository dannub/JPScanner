package com.example.user.jpscanner.helper;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by User on 09/01/2018.
 */

public class InputValidation {
    private Context context;
    public InputValidation(Context context)
    {
        this.context=context;
    }
    public boolean isInputFilled(TextInputEditText inputEditText, TextInputLayout inputLayout, String message){
        String value=inputEditText.getText().toString().trim();
        if(value.isEmpty()){
            inputLayout.setError(message);
            hideKeyboardFrom(inputEditText);
            return false;
        }
        else
        {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }
    public  boolean isInputEditTextEmail(TextInputEditText inputEditText,TextInputLayout inputLayout,String message){
        String value= inputEditText.getText().toString().trim();
        if(value.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
            inputLayout.setError(message);
            hideKeyboardFrom(inputEditText);
            return false;
        }
        else
        {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }
    public  boolean isInputEditTextIP(TextInputEditText inputEditText,TextInputLayout inputLayout,String message){
        String value= inputEditText.getText().toString().trim();
        final Pattern IP_ADDRESS
                = Pattern.compile(
                "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                        + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                        + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                        + "|[1-9][0-9]|[0-9]))");
        Matcher matcher = IP_ADDRESS.matcher(value);

        if(value.isEmpty()||!matcher.matches()){
            inputLayout.setError(message);
            hideKeyboardFrom(inputEditText);
            return false;
        }
        else
        {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }
    public boolean isInputEditTextMatches(String value1,TextInputEditText inputEditText2,TextInputLayout inputLayout,String message)
    {
        String value2=inputEditText2.getText().toString().trim();
        if(!value1.contentEquals(value2))
        {
            inputLayout.setError(message);
            hideKeyboardFrom(inputEditText2);
            return false;
        }
        else
        {
            inputLayout.setErrorEnabled(false);
        }
        return true;
    }
    private void hideKeyboardFrom(View view){
        InputMethodManager imm=(InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
}
