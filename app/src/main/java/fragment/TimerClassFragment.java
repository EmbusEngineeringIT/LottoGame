package fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bcgdv.asia.lib.ticktock.TickTockView;
import com.example.kums.lotto10.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

import static android.provider.Settings.System.DATE_FORMAT;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.google.android.gms.internal.zzahn.runOnUiThread;

/**
 * Created by Kums on 1/30/2018.
 */

public class TimerClassFragment extends Fragment implements View.OnClickListener {

    private Context context;
    CircularProgressBar mProgressBar;
    private TextView txtProgress,currencyOne,currencyTwo,currencyThree,currencyFour,currencyFive,currencySix,currencySeven,currencyEight,payTextView;
    public static final long gametime=82800000;
    private View view;
    private TickerView tickerView;
    public static int result = 0;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    public static int timerValue;
    @SuppressLint("ValidFragment")
    public TimerClassFragment()
    {

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
        initial();
        return view;
    }

    private void initial()
    {
        tickerView=(TickerView)view.findViewById(R.id.ticker_currency);
        tickerView.setCharacterList(TickerUtils.getDefaultNumberList());
        tickerView.setText("0000000");
        mProgressBar=(CircularProgressBar) view.findViewById(R.id.circularProgressbar);
        txtProgress = (TextView)view.findViewById(R.id.tv);
      /*  currencyOne=(TextView)view.findViewById(R.id.currency_val_one);
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
         //   currencyAdding();
            //setRandomText();
            result=result+10;
            tickerView.setText("" + result);
        }
    }



    @Override
    public void onStart()
        {
        super.onStart();
        mProgressBar.setProgress(0);
        timerFunction1();
    }

    public void timerFunction1()
    {

        if (timerValue !=0)
        {
            firebaseDatabase.getReference("GlobalValue").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    timerValue=dataSnapshot.getValue(int.class);
                    //  Log.d("GlobalValue"," "+timerValue);
                    //    Toasty.success(getApplicationContext(),"Global Value"+s,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else
        {
            new CountDownTimer(timerValue, 1000) {

                public void onTick(long millisUntilFinished) {
                    txtProgress.setText(String.format("%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), // The change is in this line
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    int i=(int)((gametime-millisUntilFinished)/(double)gametime*100);
                    mProgressBar.setProgress(i);
                    //  Toasty.success(getContext(),"Progress Value "+i, Toast.LENGTH_SHORT).show();
                }

                public void onFinish()
                {
                    mProgressBar.setProgress(100);
                    txtProgress.setText("Completed");
                }
            }.start();
        }
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
