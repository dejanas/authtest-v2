package com.etf.ac.bg.rs.sd120456.v2.authtest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class RegisterFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    CallbackManager callbackManager;

    private static TabLayout mTabLayout;

    private EditText emailET;
    private EditText passwordET;
    private EditText confirmPassET;

    TextInputLayout emailTIL;
    TextInputLayout passwordTIL;
    TextInputLayout confirmPassTIL;

    private Button saveBtn;

    private FirebaseAuth mAuth;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance(TabLayout tabLayout) {
        RegisterFragment fragment = new RegisterFragment();
        mTabLayout = tabLayout;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        AuthTestApplicationV2 mApplication = (AuthTestApplicationV2)mContext.getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        emailET = (EditText)view.findViewById(R.id.reg_email_ET);
        passwordET = (EditText)view.findViewById(R.id.reg_password_ET);
        confirmPassET = (EditText)view.findViewById(R.id.reg_confirmPassword_ET);

        emailTIL = (TextInputLayout)view.findViewById(R.id.reg_email_TIL);
        passwordTIL = (TextInputLayout)view.findViewById(R.id.reg_password_TIL);
        confirmPassTIL = (TextInputLayout)view.findViewById(R.id.reg_confirmPassword_TIL);

        saveBtn = (Button)view.findViewById(R.id.registerBtn);
        saveBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.registerBtn:
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                String confirmPass = confirmPassET.getText().toString();

                if(validateInputs(email, password, confirmPass))
                    registerUserWithEmailAndPassword(email, password, confirmPass);

        }
    }
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean validateInputs(String email, String password, String confirmPass){
        boolean isValid = true;

        if(!isValidEmail(emailET.getText())) {
            emailET.setError("Email adresa nije validna.");
            isValid = false;
        }
        if(email.equals("")){
            emailET.setError(getString(R.string.email_insert_warning));
            isValid = false;
        }
        if(!password.equals("")){
            if(!confirmPass.equals("")){
                if(!confirmPass.equals(password)){
                    confirmPassET.setText("");
                    confirmPassET.setError(getString(R.string.password_not_match_warning));
                    isValid = false;
                }
            }else{
                confirmPassET.setError(getString(R.string.confirm_password_warning));
                isValid = false;
            }
        }else{
            passwordET.setError(getString(R.string.insert_password_warning));
            isValid = false;
        }
        return isValid;
    }

    public void registerUserWithEmailAndPassword(String email, final String password, String confirmPass){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener((Activity) mContext,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
//                                Toast.makeText( getActivity(), "New user registered(almost)!",
//                                        Toast.LENGTH_SHORT).show();
                                moveToVerification(user.getEmail());

                            } else {
                                String errorMsg = "";
                                if(task.getException()!= null)
                                    errorMsg += task.getException().getMessage();
                                else
                                    errorMsg = "unknown error.";
                                String failedMsg = "Registration failed: " + errorMsg ;
                                Toast.makeText( getActivity(), failedMsg,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }

    public void moveToVerification(String email){

        emailET.setText(email);
        passwordET.setText("");
        confirmPassET.setText("");

        ((RegisterActivity)getActivity()).setRegistered(true);
        getFragmentManager()
                .beginTransaction().replace(R.id.fragmentContainer, new VerificationFragment()).commit();
        TabLayout.Tab tab = mTabLayout.getTabAt(1);
        if(tab!=null)
            tab.select();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }



}
