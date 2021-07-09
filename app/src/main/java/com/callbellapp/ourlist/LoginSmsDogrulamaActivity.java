package com.callbellapp.ourlist;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginSmsDogrulamaActivity extends AppCompatActivity {
    private String TELNU;
    private TextView textViewDogrulamaMetni;
    private Button buttonSıngIn;
    private ProgressBar progressBar;
    private EditText editTextCode;
    private Resources res;

    private String verificationID;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sms_dogrulama);

        textViewDogrulamaMetni = findViewById(R.id.textViewDogrulamaMetni);
        buttonSıngIn = findViewById(R.id.buttonSıngIn);
        progressBar = findViewById(R.id.progressBar);
        editTextCode = findViewById(R.id.editTextCode);

        res = getResources();

        mAuth = FirebaseAuth.getInstance();
        TELNU =(String) getIntent().getSerializableExtra("telNu");
        TELNU = "+"+TELNU;


        textViewDogrulamaMetni.setText(TELNU+res.getString(R.string.numarali_telefongonderilen_kodu_giriniz));

        dogrulamaKoduGonderme(TELNU);

        buttonSıngIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = editTextCode.getText().toString().trim();

                if (code.isEmpty() || code.length()<6){
                    Toast.makeText(LoginSmsDogrulamaActivity.this,res.getString( R.string.kodu_giriniz), Toast.LENGTH_SHORT).show();
                    return;
                }
                verifyCode(code);
            }
        });

    }

    private void verifyCode (String code){
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,code);
            mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        personelOlusturmaWeb(mAuth.getUid(),TELNU,mAuth.getCurrentUser().getDisplayName());
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(this, res.getString(R.string.login_cod_hatası), Toast.LENGTH_LONG).show();
        }
    }



    private void dogrulamaKoduGonderme(String TEL){
        progressBar.setVisibility(View.VISIBLE);
        /*PhoneAuthProvider.getInstance().verifyPhoneNumber(
                TEL,                // Phone number to verify112
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
*/
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //Toast.makeText(LoginSmsDogrulamaActivity.this, s+"----"+forceResendingToken, Toast.LENGTH_LONG).show();

            verificationID = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            String code2 = phoneAuthCredential.getProvider();

            if (code != null){
                editTextCode.setText(code);
                verifyCode(code);

            }

            //Toast.makeText(LoginSmsDogrulamaActivity.this, code+"----"+code2, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginSmsDogrulamaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    public void personelOlusturmaWeb(final String UID, final String tel, final String name){
        String url = "https://callbellapp.xyz/project/ourlist_tablo1_personel/insert_table1.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("response", response);
                String[] cevap = response.split(":");
                //Toast.makeText(getApplicationContext(), "PersonelID : "+cevap[1]+" - "+kayitBasarili +" web "+ email2,Toast.LENGTH_SHORT).show();
                //Toast.makeText(LoginKayitActivity.this, response, Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                //finish();

                Intent intent = new Intent(LoginSmsDogrulamaActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), res.getString(R.string.kayit_hatali_lutfen_kontrol_ediniz),Toast.LENGTH_LONG).show();
                //Log.e("response", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("uid",UID);
                params.put("tel",tel);
                return params;
            }
        };
        Volley.newRequestQueue(LoginSmsDogrulamaActivity.this).add(stringRequest);
    }


}
