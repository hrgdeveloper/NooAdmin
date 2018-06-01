package com.developer.hrg.nooadmin;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.InternetCheck;
import com.developer.hrg.nooadmin.Helper.MyProgress;
import com.developer.hrg.nooadmin.Helper.MySnack;
import com.developer.hrg.nooadmin.MainActivity.MainActivity;
import com.developer.hrg.nooadmin.Models.SimpleResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Toolbar toolbar ;
    EditText et_username , et_password;
    Button btn_login ;
    CoordinatorLayout coordinatorLayout ;
    AdminInfo adminInfo ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        adminInfo=new AdminInfo(LoginActivity.this);
        if (adminInfo.get_IsLOGGEDIN()) {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinatorLayout=(CoordinatorLayout) findViewById(R.id.coordinate_login);
        toolbar.setTitle("ورود به پنل مدیدیرت");
        getSupportActionBar().setTitle("ورود به پنل مدیدیرت");
        et_username=(EditText)findViewById(R.id.et_username);
        et_password=(EditText)findViewById(R.id.et_password);
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   String username = et_username.getText().toString();
                   String password = et_password.getText().toString();
                if (!InternetCheck.isOnline(LoginActivity.this)) {
                    MySnack.showSnack(coordinatorLayout,"عدم دسترسی به اینترنت");
                }
                else  if (TextUtils.isEmpty(username)) {
                 MySnack.showSnack(coordinatorLayout,"نام کاربری را وارد نمایید");
                    et_username.requestFocus();
                }else if (TextUtils.isEmpty(password)) {
                    MySnack.showSnack(coordinatorLayout,"رمز عبور را وارد نمایید");
                    et_password.requestFocus();
                }else {
                    ApiInterface apiInterface = Client.getClient().create(ApiInterface.class);
                    Call<SimpleResponse> login = apiInterface.login(username,password);
                    MyProgress.showProgress(LoginActivity.this,"در حال بررسی ");
                    login.enqueue(new Callback<SimpleResponse>() {
                        @Override
                        public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {

                            if (!response.isSuccessful()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                    MyProgress.cancelProgress();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                boolean error = response.body().isError();
                                if (error) {
                                    Toast.makeText
                                            (LoginActivity.this,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    MyProgress.cancelProgress();
                                }else {
                                    MyProgress.cancelProgress();
                                    adminInfo.setAdmin(response.body().getAdmin());
                                    adminInfo.set_IsLogged_in(true);
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    finish();



                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<SimpleResponse> call, Throwable t) {
                         MyProgress.cancelProgress();
                            Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });


    }

}
