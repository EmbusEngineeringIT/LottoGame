package fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kums.lotto10.R;
import com.example.kums.lotto10.TempActivity;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import adapter.TicketAdapter;
import es.dmoral.toasty.Toasty;
import pojo.TicketNumber;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Kums on 1/30/2018.
 */

@SuppressLint("ValidFragment")
public class TimerClassFragment extends Fragment implements View.OnClickListener {
    private Context context;
    CircularProgressBar mProgressBar;
    private TextView txtProgress;
    private TextView payTextView,currencyOne,currencyTwo,currencyThree,currencyFour,currencyFive,currencySix,currencySeven,currencyEight;;
    public static long gametime=82800000;
    private View view;
    private TickerView tickerView;
    public static long result = 0;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private int timerValue;
    public static String currentTimeValue;
    int randomInt;


    @SuppressLint("ValidFragment")
    public TimerClassFragment(Context context)
    {
        this.context=context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.timer_class_fragment, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
       // payPreferences=context.getSharedPreferences(mypreference,Context.MODE_PRIVATE);
       // firebaseDatabase.getReference(firebaseAuth.getUid()).child("pay_status").setValue(PAY_STATUS);
        initial();
        return view;
    }

    private void initial()
    {
        tickerView=(TickerView)view.findViewById(R.id.ticker_currency);
        tickerView.setCharacterList(TickerUtils.getDefaultNumberList());
        //tickerView.setText("0");
        mProgressBar=(CircularProgressBar) view.findViewById(R.id.circularProgressbar);
        txtProgress = (TextView)view.findViewById(R.id.tv);
      /*currencyOne=(TextView)view.findViewById(R.id.currency_val_one);
        currencyTwo=(TextView)view.findViewById(R.id.currency_val_two);
        currencyThree=(TextView)view.findViewById(R.id.currency_val_three);
        currencyFour=(TextView)view.findViewById(R.id.currency_val_four);
        currencyFive=(TextView)view.findViewById(R.id.currency_val_five);
        currencySix=(TextView)view.findViewById(R.id.currency_val_six);
        currencySeven=(TextView)view.findViewById(R.id.currency_val_seven);
        currencyEight=(TextView)view.findViewById(R.id.currency_val_eight);*/
        payTextView=(TextView)view.findViewById(R.id.pay_button_timer);
        payTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v == payTextView)
        {
            gettingTicket();
            //payFunction();
        }
    }

    private void cashFunction()
    {
        firebaseDatabase.getReference().child("Total Cash").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s=dataSnapshot.getValue(String.class);
                if (s == null)
                {
                    String s1="0";
                    firebaseDatabase.getReference().child("Total Cash").setValue(s1);
                    tickerView.setText("" + result);
                }
                else {
                    result =Long.parseLong(s);
                    tickerView.setText("" + result);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void payFunction()
    {
        firebaseDatabase.getReference(FirebaseAuth.getInstance().getUid().toString()+"/"+"pay_status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String s=dataSnapshot.getValue(String.class);
               // boolean pay_type=Boolean.valueOf(s);
                Log.d("PayStatus"," "+s);
                if (s.toString().length()==7)
                {
                    Intent intent=new Intent(getApplicationContext(),TempActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toasty.success(context,"Already Ur Paid", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void gettingTicket()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view=getLayoutInflater().inflate(R.layout.ticket_option,null);
        final RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.common_recycler_view);
        builder.setView(view);
        List<TicketNumber> ticketNumberList=new ArrayList<>() ;
        int i=50001;
        for(int i1=i;i1<=50025;i1++)
        {
            TicketNumber ticketNumber = new TicketNumber(String.valueOf(i1));
            ticketNumberList.add(ticketNumber);
        }

        final TicketAdapter  ticketAdapter=new TicketAdapter(ticketNumberList);
        GridLayoutManager layoutManager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(ticketAdapter);
        TextView blueTextView,redTextView,greenTextView,yellowTextView;
        blueTextView=(TextView)view.findViewById(R.id.blue_ticket);
        redTextView=(TextView)view.findViewById(R.id.red_ticket);
        greenTextView=(TextView)view.findViewById(R.id.green_ticket);
        yellowTextView=(TextView)view.findViewById(R.id.yellow_ticket);


        final LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.recycler_back);

        blueTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                linearLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });

        redTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.success(getApplicationContext(),"Red",Toast.LENGTH_SHORT).show();
                linearLayout.setBackgroundColor(getResources().getColor(R.color.red));
            }
        });
        greenTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.success(getApplicationContext(),"Green",Toast.LENGTH_SHORT).show();
                linearLayout.setBackgroundColor(getResources().getColor(R.color.light_green));
            }
        });

        yellowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.success(getApplicationContext(),"Yellow",Toast.LENGTH_SHORT).show();
                linearLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
            }
        });
        builder.show();
    }


    @Override
    public void onStart()
    {
        super.onStart();
        mProgressBar.setProgress(0);
        serverTime();
        cashFunction();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
     //   serverTime();
    }

    public void timerFunction1()
    {
        String source = currentTimeValue;
        String[] tokens = source.split(":");
        int secondsToMs = Integer.parseInt(tokens[2]) * 1000;
        int minutesToMs = Integer.parseInt(tokens[1]) * 60000;
        int hoursToMs = Integer.parseInt(tokens[0]) * 3600000;
        long total = secondsToMs + minutesToMs + hoursToMs;
        long value=0;
        Calendar rightNow = null;
        int h1 = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            rightNow = Calendar.getInstance();
            h1 = rightNow.get(Calendar.HOUR_OF_DAY);
            Log.d("CurrentHours"," " +h1);
        }
        if(h1>=24 || h1<=9)
        {
            //gametime=32400000;
            value=total-3600000;
            Log.d("CurrentHours"," " +value);
        }
        else
        {
            value=gametime-total;
        }

  /*      txtProgress.setStartDuration(value);
        Calendar rightNow = null;
        int h = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            rightNow = Calendar.getInstance();
            h = rightNow.get(Calendar.HOUR_OF_DAY);
            Log.d("CurrentHours"," " +h);
        }

        if ( h == 10)
        {
            mProgressBar.setProgress(100);
            txtProgress.stop();
        }
        else if (h>=11)
        {
            *//*String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished), TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
            txtProgress.setText(hms);*//*
            txtProgress.start();
            int i=(int)((gametime-value)/(double)gametime*100);
            mProgressBar.setProgress(i);
        }*/

        Log.d("TimerDiffff"," "+total);
        Log.d("TimerDiffff1"," "+value);
     /*   Log.d("TimerDiffff"," "+s);
        long value = 0;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("kk:mm:ss");
        Date date;
        try {
            date=simpleDateFormat.parse(s);
            value=date.getTime();
            Log.d("TimerDiffff"," "+value);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        final long finalValue = value;
        new CountDownTimer(finalValue, 1000) {

            public void onTick(long millisUntilFinished)
            {
                /*int seconds = (int) (millisUntilFinished / 1000) % 60 ;
                int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
                int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
                */

                Calendar rightNow = null;
                int h = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    rightNow = Calendar.getInstance();
                    h = rightNow.get(Calendar.HOUR_OF_DAY);
                    Log.d("CurrentHours"," " +h);
                }

                if ( h == 9)
                {
                    mProgressBar.setProgress(100);
                    txtProgress.setText("Completed");
                }
                else if (h>=10 )
                {
                    final DateFormat simpleDateFormat=new SimpleDateFormat("dd:MM:yyyy");
                    java.util.Calendar calendar1= java.util.Calendar.getInstance();
                    calendar1.add(java.util.Calendar.DAY_OF_YEAR,0);
                    final String currentDate=simpleDateFormat.format(calendar1.getTime());
                    StringBuilder stringBuilder=new StringBuilder();
                    stringBuilder.append(currentDate);
                    stringBuilder.append(" 10:00:00");
                    final String serverTimes=String.valueOf(stringBuilder);
                    firebaseDatabase.getReference().child("Server Date & Time").setValue(serverTimes);
                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished), TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    txtProgress.setText(hms);
                   //
                }

                txtProgress.setText(String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                int i=(int)((gametime- finalValue)/(double)gametime*100);
                //int i= (int) (millisUntilFinished/100);
                mProgressBar.setProgress(i);

            }

            public void onFinish()
            {
                mProgressBar.setProgress(100);
                txtProgress.setText("Completed");
            }
        }.start();
    }

    public void serverTime()
    {
        final DateFormat simpleDateFormat=new SimpleDateFormat("dd:MM:yyyy");
        java.util.Calendar calendar1= java.util.Calendar.getInstance();
        calendar1.add(java.util.Calendar.DAY_OF_YEAR,0);
        final String currentDate=simpleDateFormat.format(calendar1.getTime());
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(currentDate);
        stringBuilder.append(" 10:00:00");
        final String serverTimes=String.valueOf(stringBuilder);
        final DateFormat dateFormat=new SimpleDateFormat("dd:MM:yyyy kk:mm:ss");
        final java.util.Calendar calendar= java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_YEAR,0);
        String s1=dateFormat.format(calendar.getTime());
        StringBuilder stringBuilder1=new StringBuilder();
        stringBuilder1.append(s1);
        final String currentTime=String.valueOf(stringBuilder1);
        Log.d("ServerTimeDiff123"," "+currentTime);

        firebaseDatabase.getReference("Server Date & Time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String s2=dataSnapshot.getValue(String.class);
                if (s2 == null)
                {
                    firebaseDatabase.getReference().child("Server Date & Time").setValue(serverTimes);
                }
                else
                {
                   /*s4=s2;
                   String date=s3;
                   Date date2 = new Date();*/
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MM:yyyy kk:mm:ss");
                    long different;
                    try {
                        Date date1 = simpleDateFormat.parse(serverTimes);
                        Date date2 = simpleDateFormat.parse(currentTime);

                        //Log.d("Date1time"," "+serverTimes);

                        if (date1.getTime() < date2.getTime())
                        {
                            Log.d("Date1time"," "+date1.getTime());
                            Log.d("Date1time1"," "+date2.getTime());
                            different = date2.getTime() - date1.getTime();
                       }
                        else
                        {
                            Log.d("Date1time"," "+date1.getTime());
                            Log.d("Date1time1"," "+date2.getTime());
                            different = date1.getTime() - date2.getTime();
                        }

//                       System.out.println("startDate : " + startDate);
                        //                    System.out.println("endDate : "+ endDate);
                        //                  System.out.println("different : " + different);

                        long secondsInMilli = 1000;
                        long minutesInMilli = secondsInMilli * 60;
                        long hoursInMilli = minutesInMilli * 60;
                        long daysInMilli = hoursInMilli * 24;

                        long elapsedDays = different / daysInMilli;
                        different = different % daysInMilli;

                        long elapsedHours = different / hoursInMilli;
                        different = different % hoursInMilli;

                        long elapsedMinutes = different / minutesInMilli;
                        different = different % minutesInMilli;

                        long elapsedSeconds = different / secondsInMilli;
                        // String time=String.valueOf(elapsedDays+" "+ elapsedHours+" "+ elapsedMinutes+" "+elapsedSeconds);
                        StringBuilder remainTime=new StringBuilder();
                        remainTime.append(elapsedHours);
                        remainTime.append(":");
                        remainTime.append(elapsedMinutes);
                        remainTime.append(":");
                        remainTime.append(elapsedSeconds);
                        currentTimeValue=String.valueOf(remainTime);
                        String s= DateTimeUtils.millisToTime(elapsedHours);

                        Log.d("TimerDif124"," "+currentTimeValue);
                        Log.d("TimerDif123456"," "+s);
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                    timerFunction1();
                   /*
                   SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("dd:MM:yyyy hh:mm:ss");
                   long d=DateTimeUtils.getDateDiff(date2,s4,DateTimeUnits.HOURS);
                   String time=String.valueOf(d);*/
                    // Log.d("ServerTimeDifference1"," "+time);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void currencyAdding() {
        int amount[] = {1, 0};
        int i;
        List<Integer> integers = new ArrayList<Integer>();
        Log.d("Values12345", " " + amount.length);
        for (i = 0; i < amount.length; i++) {
            integers.add(amount[i]);
        }

        int currencyValueOne = Integer.parseInt(currencyOne.getText().toString());
        int currencyValueTwo = Integer.parseInt(currencyTwo.getText().toString());
        int currencyValueThree = Integer.parseInt(currencyThree.getText().toString());
        int currencyValueFour = Integer.parseInt(currencyFour.getText().toString());
        int currencyValueFive = Integer.parseInt(currencyFive.getText().toString());
        int currencyValueSix = Integer.parseInt(currencySix.getText().toString());
        int currencyValueSeven = Integer.parseInt(currencySeven.getText().toString());
        int currencyValueEight = Integer.parseInt(currencyEight.getText().toString());
        if (currencyValueOne == 0) {
            currencyValueOne = integers.get(1) + currencyValueOne;
            currencyOne.setText(String.valueOf(currencyValueOne));
            if (currencyValueTwo < 9) {
                currencyValueTwo = integers.get(0) + currencyValueTwo;
                currencyTwo.setText(String.valueOf(currencyValueTwo));
            } else {
                currencyTwo.setText("0");
                if (currencyValueThree < 9) {
                    currencyValueThree = integers.get(0) + currencyValueThree;
                    currencyThree.setText(String.valueOf(currencyValueThree));
                } else {
                    currencyThree.setText("0");
                    if (currencyValueFour < 9) {
                        currencyValueFour = integers.get(0) + currencyValueFour;
                        currencyFour.setText(String.valueOf(currencyValueFour));
                    } else {
                        currencyFour.setText("0");
                        if (currencyValueFive < 9) {
                            currencyValueFive = integers.get(0) + currencyValueFive;
                            currencyFive.setText(String.valueOf(currencyValueFive));
                        } else
                        {
                            currencyFive.setText("0");
                            if (currencyValueSix < 9) {
                                currencyValueSix = integers.get(0) + currencyValueSix;
                                currencySix.setText(String.valueOf(currencyValueSix));
                            }
                            else
                            {    currencySix.setText("0");
                                if (currencyValueSeven < 9)
                                {
                                    currencyValueSeven = integers.get(0) + currencyValueSeven;
                                    currencySeven.setText(String.valueOf(currencyValueSeven));
                                }
                                else
                                {
                                    currencySeven.setText("0");
                                    if (currencyValueEight < 9)
                                    {
                                        currencyValueEight = integers.get(0) + currencyValueEight;
                                        currencyEight.setText(String.valueOf(currencyValueEight));
                                    }
                                }
                            }

                        }
                    }
                }
            }
            // Toasty.success(context,"Values :"+integers.size(),Toast.LENGTH_SHORT).show();
        }
    }

}
