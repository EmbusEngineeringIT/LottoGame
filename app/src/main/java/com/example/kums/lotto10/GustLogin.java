package com.example.kums.lotto10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import pojo.UserDetails;

public class GustLogin extends AppCompatActivity implements View.OnClickListener {

    private EditText mobileNumber,verificationLogin;
    private LinearLayout verificationView;
    private TextView signIn;
    public static int viewCounter=0;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth firebaseAuth;
    private String verification=null;
    private PhoneAuthProvider.ForceResendingToken verificationToken;
    private ProgressDialog progressDialog;
    private CircleImageView circleImageView;
    private final int PICKING_IMAGE_REQUEST_CODE=14;
    private Uri filePath;
    private Bitmap bitmap;
    private String imageEncoded;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gust_login);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        initialFunction();

        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    verification=s;
                    verificationToken=forceResendingToken;
                    verificationView.setVisibility(View.VISIBLE);
                    signIn.setText("Verify Code");
                    signIn.setEnabled(true);
                    viewCounter=1;
            }
        };
    }

    public void initialFunction()
    {
        mobileNumber=(EditText)findViewById(R.id.mobile_login);
        verificationLogin=(EditText)findViewById(R.id.verification_login);
        verificationView=(LinearLayout)findViewById(R.id.verification_view);
        signIn=(TextView)findViewById(R.id.number_sign);
        signIn.setOnClickListener(this);
        circleImageView=(CircleImageView)findViewById(R.id.mobile_upload_image_img);
        circleImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == signIn)
        {
            if (viewCounter == 0)
            {
                progressDialog.show();
                mobileNumber.setEnabled(false);
                String phoneNumber=mobileNumber.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS,this,callbacks);
            }
            else
            {
                progressDialog.show();
                signIn.setEnabled(false);
                String verficationText=verificationLogin.getText().toString().trim();
                PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(verification,verficationText);
                signInWithPhoneAuthCredential(phoneAuthCredential);
                Toasty.success(this,"Verification Code"+verficationText.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        if (v == circleImageView)
        {
            chooseImage();
        }
    }

    private void chooseImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICKING_IMAGE_REQUEST_CODE);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                         //   Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            userInformation();
                            gamePage();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                        //    Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKING_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data !=null && data.getData()!=null)
        {
            filePath=data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                circleImageView.setImageBitmap(bitmap);
                encodeBitmapAndSaveToFirebase();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void encodeBitmapAndSaveToFirebase()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setMessage("Image...");
                progressDialog.show();
                progressDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(Void... voids) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] byteArray = baos.toByteArray();
                imageEncoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
               /* DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("image");
                ref.setValue(imageEncoded);*/
                return imageEncoded;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
            }
        }.execute();
    }

    public void userInformation()
    {
    //    final String name=userName.getText().toString().trim();
        final String number=mobileNumber.getText().toString().trim();

        //Toast.makeText(this,"User Information Saved.....",Toast.LENGTH_SHORT).show();
        if(filePath !=null && number!=null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading........");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final UserDetails userDetails=new UserDetails(imageEncoded);
            final FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
            //   Log.d("ImageEncodedValue"," "+imageEncoded);
            databaseReference.child(firebaseUser.getUid()).setValue(userDetails);
            databaseReference.addChildEventListener(new ChildEventListener()
            {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                {
                    //  Toast.makeText(getApplicationContext(), "File Uploaded...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    mobileNumber.getText().clear();
                    finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    //Toasty.success(getApplicationContext(),"Working",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toasty.error(getApplicationContext(),"U Missed Some Field",Toast.LENGTH_SHORT).show();
        }
    }


    public void gamePage()
    {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

}
