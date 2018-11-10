package com.etf.ac.bg.rs.sd120456.v2.authtest;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterLastFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Context mContext;

    private TextView proceedToLoginTV;
    private TextView goBackTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_last_register, container, false);

        proceedToLoginTV = (TextView)view.findViewById(R.id.proceedToLoginTV);
        TextView successRegisteredTV = (TextView) view.findViewById(R.id.successRegisteredTV);
        proceedToLoginTV.setPaintFlags(proceedToLoginTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        goBackTV = (TextView)view.findViewById(R.id.goBackTV);

        if(checkIfUserVerified()){
            successRegisteredTV.setVisibility(View.VISIBLE);
            successRegisteredTV.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.custom_anim));
            proceedToLoginTV.setVisibility(View.VISIBLE);

        }else{
            deleteCurrentUser();
            successRegisteredTV.setText("Email adresa nije verifikovana.Pokušajte ponovo.");
            successRegisteredTV.setVisibility(View.VISIBLE);
            goBackTV.setVisibility(View.VISIBLE);

        }

        proceedToLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToLoginTV.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent,null));
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        goBackTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackTV.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent,null));
                Intent registerActivity = new Intent(getActivity(), RegisterActivity.class);
                startActivity(registerActivity);
            }
        });

        return view;
    }

    private boolean checkIfUserVerified(){

        AuthTestApplicationV2 mApplication = (AuthTestApplicationV2)mContext.getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();
        FirebaseUser user = mAuth.getCurrentUser();
        boolean isVerified = false;
        if(user!=null)
            isVerified = user.isEmailVerified();

        return isVerified;

    }

    public void deleteCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Ponovo.(obrisan)", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }
}
