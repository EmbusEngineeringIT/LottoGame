package com.example.kums.lotto10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import pojo.UserDetails;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName,userMobileNumber;
    private TextView saveButton,maleGender,femaleGender;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private CircleImageView userImage;
    private final int PICKING_IMAGE_REQUEST_CODE=23;
    private Uri filePath;
    private FirebaseUser firebaseUser;
    private String imageEncoded;
    private  Bitmap bitmap;
    public static boolean GENDER=false;
    public static String gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature( Window.FEATURE_NO_TITLE );

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_profile);
        firebaseAuth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        if (firebaseAuth ==null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
        userImage=(CircleImageView)findViewById(R.id.upload_image_img);
        userImage.setOnClickListener(this);
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        userName=(EditText)findViewById(R.id.user_name_id_txt);
        userMobileNumber=(EditText)findViewById(R.id.save_mobileNumber_id_txt);
        saveButton=(TextView)findViewById(R.id.user_save_profile);
        saveButton.setOnClickListener(this);
        maleGender=(TextView)findViewById(R.id.male_gender);
        maleGender.setOnClickListener(this);
        femaleGender=(TextView)findViewById(R.id.female_gender);
        femaleGender.setOnClickListener(this);
        maleGender.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                maleGender.setBackgroundDrawable(getResources().getDrawable(R.drawable.signup_back));
                return true;
            }
        });

        femaleGender.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                femaleGender.setBackgroundDrawable(getResources().getDrawable(R.drawable.signup_back));
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v==saveButton)
        {
            userInformation();
        }
        if (v==userImage)
        {
            chooseImage();
        }
        if (v==maleGender)
        {
            GENDER=false;
            if (GENDER==false)
            {
                gender=maleGender.getText().toString();
                Toasty.success(getApplicationContext(),"Gender "+gender,Toast.LENGTH_SHORT).show();
                maleGender.setBackgroundDrawable(getResources().getDrawable(R.drawable.gender_back_deselect));
                femaleGender.setBackgroundDrawable(getResources().getDrawable(R.drawable.signup_back));
                GENDER=true;
            }
            else
            {
                maleGender.setBackgroundDrawable(getResources().getDrawable(R.drawable.signup_back));
                GENDER=false;
            }
        }
        if (v==femaleGender)
        {
            GENDER=false;
            if (GENDER==false)
            {
                gender=femaleGender.getText().toString();
                Toasty.success(getApplicationContext(),"Gender "+gender,Toast.LENGTH_SHORT).show();
                femaleGender.setBackgroundDrawable(getResources().getDrawable(R.drawable.gender_back_deselect));
                maleGender.setBackgroundDrawable(getResources().getDrawable(R.drawable.signup_back));
                GENDER=true;
            }
            else
            {
                femaleGender.setBackgroundDrawable(getResources().getDrawable(R.drawable.signup_back));
                GENDER=false;
            }
        }
    }

    private void chooseImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICKING_IMAGE_REQUEST_CODE);
    }

    public void userInformation()
    {
        final String name=userName.getText().toString().trim();
        final String number=userMobileNumber.getText().toString().trim();

        //Toast.makeText(this,"User Information Saved.....",Toast.LENGTH_SHORT).show();
        if(filePath !=null && gender!=null && name !=null && number!=null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading........");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final UserDetails userDetails=new UserDetails(name,number,imageEncoded,gender);
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
                    userMobileNumber.getText().clear();
                    userName.getText().clear();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKING_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data !=null && data.getData()!=null)
        {
            filePath=data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                userImage.setImageBitmap(bitmap);
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

}
