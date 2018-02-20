package com.example.kums.lotto10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import fragment.TimerClassFragment;
import paytm.Api;
import paytm.Checksum;
import paytm.Constants;
import paytm.Paytm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static fragment.TimerClassFragment.result;

public class TempActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    int amount=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        generateCheckSum();
        finish();
    }

    public  void generateCheckSum() {

        //getting the tax amount first.
        String txnAmount = String.valueOf(amount);

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                Constants.M_ID,
                Constants.CHANNEL_ID,
                txnAmount,
                Constants.WEBSITE,
                Constants.CALLBACK_URL,
                Constants.INDUSTRY_TYPE_ID
        );

        //creating a call object from the apiService
        Call<Checksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                paytm.getCallBackUrl(),
                paytm.getIndustryTypeId()
        );

        call.enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(Call<Checksum> call, Response<Checksum> response) {

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
            }

            @Override
            public void onFailure(Call<Checksum> call, Throwable t) {

            }
        });
    }

    public void initializePaytmPayment(String checksumHash, Paytm paytm) {

        //getting paytm service
        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for produkoction
        //PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", Constants.M_ID);
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
        paramMap.put("CHECKSUMHASH", checksumHash);
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());

        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(this, true, true, this);
    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Toasty.success(this,"Transaction Success", Toast.LENGTH_LONG).show();
        Log.d("PayStatus ","Woriking Success");
        //TimerClassFragment.result=result+amount;
    }

    @Override
    public void networkNotAvailable() {
        Toasty.success(this,"Network Not Available", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Toasty.success(this,"Client Authentication Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Toasty.success(this,"Some Error Found", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Toasty.success(this,"Error Loading Web Page", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toasty.success(this,"Back Pressed Cancel", Toast.LENGTH_LONG).show();
        TimerClassFragment.result=result+amount;
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        String value="Paid";
        firebaseDatabase.getReference(FirebaseAuth.getInstance().getUid().toString()).child("pay_status").setValue(value);
        long cash=TimerClassFragment.result;
        String c=String.valueOf(cash);
        firebaseDatabase.getReference().child("Total Cash").setValue(c);
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Toasty.success(this,"Transaction Cancel", Toast.LENGTH_LONG).show();
    }



}
