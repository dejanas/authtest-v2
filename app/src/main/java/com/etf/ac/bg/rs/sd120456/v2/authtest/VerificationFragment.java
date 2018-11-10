package com.etf.ac.bg.rs.sd120456.v2.authtest;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationFragment extends Fragment {

    private Context mContext;
    private FirebaseAuth mAuth;

    private TextView confirmedTV;
    private TextView sendAgainTV;
    private Button posaljiBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_verification, container, false);

        AuthTestApplicationV2 mApplication = (AuthTestApplicationV2)getActivity().getApplicationContext();
        mAuth = mApplication.getFirebaseAuth();

        TextView currentEmailTV = (TextView) view.findViewById(R.id.currentAddressTV);
        confirmedTV = (TextView)view.findViewById(R.id.confirmedAddressTV);
        confirmedTV.setPaintFlags(confirmedTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        sendAgainTV = (TextView)view.findViewById(R.id.sendAgainTV);
        sendAgainTV.setPaintFlags(sendAgainTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        posaljiBtn = (Button)view.findViewById(R.id.posaljiBtn);

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null){
            currentEmailTV.setText("Verifikacioni email će biti poslat na adresu: " + user.getEmail());
        }

        posaljiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
                posaljiBtn.setVisibility(View.GONE);
                confirmedTV.setVisibility(View.VISIBLE);
                sendAgainTV.setVisibility(View.VISIBLE);
             }
            });

        sendAgainTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAgainTV.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                sendVerificationEmail();
            }
        });

        confirmedTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmedTV.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                final FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

                AuthTestApplicationV2 mApplication = (AuthTestApplicationV2)mContext.getApplicationContext();
                mAuth = mApplication.getFirebaseAuth();
                final FirebaseUser user = mAuth.getCurrentUser();
                if(user!=null)
                    user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(getActivity(), "Reload successful.", Toast.LENGTH_SHORT).show();
                                ft .replace(R.id.fragmentContainer, new RegisterLastFragment()).commit();
                            }else{
                                user.delete();
                            }
                        }
                    });
            }
        });

        return view;
    }

    private void sendVerificationEmail(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener((Activity) mContext, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),
                                        "Verification email sent.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(),
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
