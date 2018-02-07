package com.example.kums.lotto10;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import fragment.TimerClassFragment;
import pojo.UserAmountDetails;
import pojo.UserDetails;

import static com.example.kums.lotto10.R.id.select_players;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static long totalPlayers=100000;
    private static long totalAmount=1000000;
    private static long balanceAmount,t,ttt,tt,p,p1;
    private static int tenPercenatgePeople;
    private static int sixtyPercenatgePl;
    private FirebaseAuth  firebaseAuth;
    private FirebaseUser firebaseUser;
    /*TextView playerText;
    TextView tenText;
    TextView sixtyText;
    TextView oneText;
    static TextView companyTakeOver;*/
    private Bitmap bitmap;

    private TextView startGameTextView;
    private ImageView logoutUser,tradeImageView;
    private CircleImageView userPhoto;

    public static int onePlayersMoney;
 //   private Button button;
    public static long comapanyAmountOnly;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
   /*     playerText=(TextView)findViewById(R.id.player_txt);
        tenText=(TextView)findViewById(R.id.ten_txt);
        sixtyText=(TextView)findViewById(R.id.sixty_txt);
        oneText=(TextView)findViewById(R.id.one_txt);
        companyTakeOver=(TextView)findViewById(R.id.cmpny_amount);
        button=(Button)findViewById(R.id.select_players);
        button.setOnClickListener(this);
   */     firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        registerUserDetails();
        allView();
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });*/

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

    }

    private void registerUserDetails()
    {
        SharedPreferences sharedPreferences=this.getSharedPreferences(LoginOptions.mypreference,MODE_PRIVATE);
        int value=sharedPreferences.getInt(LoginOptions.SHAR_VALUE,0);
        if (value == 0)
        {
            userDetails();
        }
        else if (value == 1)
        {
            gustUserDetails();
        }
        else if (value == 2 )
        {
            gustUserDetails();
        }

    }

    public void allView()
    {
        tradeImageView=(ImageView)findViewById(R.id.timer_fragment);
        tradeImageView.setOnClickListener(this);
        userPhoto=(CircleImageView)findViewById(R.id.user_photo_prf_nav);
        userPhoto.setOnClickListener(this);
        logoutUser=(ImageView)findViewById(R.id.logout_nav);
        logoutUser.setOnClickListener(this);

        startGameTextView=(TextView)findViewById(R.id.start_game_txt);
        startGameTextView.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {

        if (v == userPhoto)
        {
            SharedPreferences sharedPreferences=this.getSharedPreferences(LoginOptions.mypreference,MODE_PRIVATE);
            int value=sharedPreferences.getInt(LoginOptions.SHAR_VALUE,0);
            if (value == 2 || value == 1)
            {
                Toasty.warning(this,"Functionality is not Finish",Toast.LENGTH_SHORT).show();
            }
            else
            {
                userProfileInformation();
            }

        }

        if (v==logoutUser)
        {
            firebaseAuth.signOut();
            if (firebaseAuth.getCurrentUser()==null)
            {
                startActivity(new Intent(getApplicationContext(),LoginOptions.class));
                finish();
            }
        }

        if(v == tradeImageView)
        {
            android.support.v4.app.Fragment timerClassFragment=new TimerClassFragment();
            android.support.v4.app.FragmentManager fragmentManager=getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.contain_layout,timerClassFragment);
            fragmentTransaction.commit();
        }
        /*if (v==button)
        {
            companyAmount(totalAmount);
            firstTenPercentageAmount(balanceAmount);
            secondNinetyPercentageAmount(balanceAmount);
            remainOnePlayer(balanceAmount);

            List<Integer> integers=new ArrayList<Integer>();
            tenText.setText("");
            sixtyText.setText("");
            oneText.setText("");

            for (int i=0;i<totalPlayers;i++)
            {
                integers.add(i);
            }
            playerText.setText(String.valueOf(integers.size()));

            Collections.shuffle(integers);

          //  Log.d("UserInformation000"," "+integers);

            ArrayList<UserAmountDetails> userAmountDetails=new ArrayList<>();
            for(int j=0;j<p;j++)
            {
                userAmountDetails.add(new UserAmountDetails(integers.get(j),tenPercenatgePeople));
                tenText.append(String.valueOf("ply: "+integers.get(j)+"Mny: "+tenPercenatgePeople));
                tenText.append("\n");
            }

            for (long j=0;j<userAmountDetails.size();j++)
            {
                UserAmountDetails userAmountDetails1=userAmountDetails.get((int) j);
              //  Log.d("UserInformation"," User Id :"+ userAmountDetails1.getId()+"\n"+"User Amount :"+userAmountDetails1.getAmount());
            }
            for(long k=p;k<p1;k++)
            {
                userAmountDetails.add(new UserAmountDetails(integers.get((int) k),sixtyPercenatgePl));
               // Log.d("Players_Selection:", String.valueOf(integers.get(k)));
                sixtyText.append(String.valueOf("Ply: "+integers.get((int) k)+"Mny: "+sixtyPercenatgePl));
                sixtyText.append("\n");
             //   Log.d("UserInformation"," User Id :"+ integers.get(k)+"\n"+"User Amount :"+sixtyPercenatgePl);
            }

            for (long j=p1;j<userAmountDetails.size();j++)
            {
                UserAmountDetails userAmountDetails1=userAmountDetails.get((int) j);
                //Log.d("UserInformation123"," User Id :"+ userAmountDetails1.getId()+"\n"+"User Amount :"+userAmountDetails1.getAmount());
            }

            for(long j=p1;j<p1+1;j++)
            {
                userAmountDetails.add(new UserAmountDetails(integers.get((int) j),onePlayersMoney));
                oneText.append(String.valueOf("Ply: "+integers.get((int) j)+"Mny: "+onePlayersMoney));
                oneText.append("\n");
      //          Log.d("UserInformation12345"," User Id :"+ integers.get(j)+"\n"+"User Amount :"+onePlayersMoney);
               // oneText.setText(String.valueOf("Ply: "+integers.get(j)+"Mny: "+onePlayersMoney));
            }

            for (long j=p1;j<userAmountDetails.size();j++)
            {
                UserAmountDetails userAmountDetails1=userAmountDetails.get((int) j);
            //    Log.d("UserInformation123456"," User Id :"+ userAmountDetails1.getId()+"\n"+"User Amount :"+userAmountDetails1.getAmount());
            }
           // Log.d("Players Selection : ", String.valueOf(integers));

            Toasty.success(this,"U Got A Money",Toast.LENGTH_LONG).show();
        }*/
    }



    private void userProfileInformation()
    {
        final AlertDialog.Builder alertDialog= new AlertDialog.Builder(this);
        View  view=View.inflate(this,R.layout.user_profile_information,null);
        alertDialog.setView(view);
        CircleImageView circleImageView;
        TextView userNameInfo,userMobileNumberInfo,closeInfo;
        userNameInfo=(TextView)view.findViewById(R.id.user_name_info);
        userMobileNumberInfo=(TextView)view.findViewById(R.id.user_mobile_info);
        circleImageView=(CircleImageView)view.findViewById(R.id.user_info_image_img);
        circleImageView.setImageBitmap(bitmap);
        userNameInfo.setText(firebaseUser.getEmail().toString());
        userMobileNumberInfo.setText(firebaseUser.getPhoneNumber().toString());
        closeInfo=(TextView)view.findViewById(R.id.close_user_info);
        closeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog1=alertDialog.create();
                alertDialog1.dismiss();
            }
        });

        alertDialog.show();
    }



    @SuppressLint("StaticFieldLeak")
    public void userDetails()
    {
//        userEmail.setText(firebaseUser.getEmail());
  //      userName.setText(firebaseUser.getDisplayName());
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... strings) {
                String urlDisplay =firebaseUser.getPhotoUrl().toString();
                InputStream in = null;
                try {
                    in = new URL(urlDisplay).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap mIcon11 = BitmapFactory.decodeStream(in);
                return mIcon11;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap)
            {
                super.onPostExecute(bitmap);
                userPhoto.setImageBitmap(bitmap);
            }
        }.execute();
    }


    public void gustUserDetails()
    {
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseDatabase.getReference(FirebaseAuth.getInstance().getUid().toString()+"/"+"imageUrl").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String app=dataSnapshot.getValue(String.class);
                byte[] imageBytes= Base64.decode(app,Base64.NO_WRAP);
                bitmap= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
                userPhoto.setImageBitmap(bitmap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

      /*public static void companyAmount(long amount)
    {
        amount=(int) ((int)(totalAmount)*(0.1));
        balanceAmount=totalAmount-amount;
        comapanyAmountOnly=amount;
        companyTakeOver.setText("Company Amount "+balanceAmount);
        System.out.println("----------------------------------------");
        System.out.println("Company Amount ="+amount);
        System.out.println("----------------------------------------");
        System.out.println("Balance Amount ="+balanceAmount);
    }

    public static void firstTenPercentageAmount(long amount)
    {
        p=(int)((int)((totalPlayers)*(0.1)));
        amount=(int) ((int)(balanceAmount)*(0.2));
        t=amount;
        tenPercenatgePeople=(int)((int)(amount)/p);
        System.out.println("----------------------------------------");
        System.out.println("----------------------------------------");
        System.out.println("First Ten People Total Amount ="+amount);
        System.out.println("----------------------------------------");
        System.out.println("First Ten People Each Get Player Amount ="+tenPercenatgePeople);
    }

    public static void secondNinetyPercentageAmount(long amount)
    {
        long player=totalPlayers-p;
        tt=balanceAmount-t;
        System.out.println(tt);
        p1=((int)((player)*(0.64)));
        int percentage=(int)((player)*(0.64));
        amount=(int) ((int)(balanceAmount-t)*(0.99));
        sixtyPercenatgePl=(int)((int)(amount)/percentage);

        System.out.println("----------------------------------------");
        System.out.println("----------------------------------------");
        System.out.println("Second Sixty Percentage of People Total Amount ="+amount);
        System.out.println("----------------------------------------");
        System.out.println("Second Sixty Percentage Each Player Get="+sixtyPercenatgePl);
        System.out.println("----------------------------------------");
        System.out.println("Percentage ="+percentage);
        int secondPlayerMoney=sixtyPercenatgePl*percentage;
        System.out.println("Second="+secondPlayerMoney);
        ttt=tt-secondPlayerMoney;
    }

    public static void remainOnePlayer(long amount)
    {
        int balanceAA=(int)((int)(ttt)*(0.8));
        long ll=ttt-balanceAA;
        long lll=ll+comapanyAmountOnly;
        companyTakeOver.setText("Company Amount "+lll);
        onePlayersMoney=balanceAA;
    }*/


}
