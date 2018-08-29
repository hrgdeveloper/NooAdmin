package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.newadmin;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Fragments.Fragment_userManage.getChanels.Fragment_getAllChanels;
import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.MyProgress;
import com.developer.hrg.nooadmin.Helper.MySnack;
import com.developer.hrg.nooadmin.LoginActivity;
import com.developer.hrg.nooadmin.MainActivity.MainActivity;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewAdminFragment extends Fragment {
    Button btn_register ;
    EditText et_username , et_password;
    CoordinatorLayout coordinatorLayout ;
    AdminInfo adminInfo ;
    Admin admin;
    private static final String TAG = NewAdminFragment.class.getName();

    public NewAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminInfo=new AdminInfo(getActivity());
        admin=adminInfo.getAdmin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_new_admin, container, false);
        et_username=(EditText)view.findViewById(R.id.et_username_newAdmin);
        et_password=(EditText)view.findViewById(R.id.et_password_newAdmin);

        btn_register=(Button)view.findViewById(R.id.btn_register_newAdmin);
        coordinatorLayout=(CoordinatorLayout)view.findViewById(R.id.coordinate_newAdmin);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        et_username.setFilters(new InputFilter[]{getUsernameFilter()});
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (  et_username.getText().toString().length()<3) {
                    MySnack.showSnack(coordinatorLayout,"نام کاربری باید دارای حداقل 4 حرف باشد ");
                }else if (et_password.getText().toString().length() < 6 ) {
                    MySnack.showSnack(coordinatorLayout,"رمز عبور باید دارای حداقل 6 حرف باشد");
                }else {
                    String username = et_username.getText().toString();
                    String password = et_password.getText().toString();
                    ApiInterface apiInterface = Client.getClient().create(ApiInterface.class);
                    Call<SimpleResponse> register = apiInterface.createAdmin(admin.getApikey(),username,password);
                    MyProgress.showProgress(getActivity(),"در حال بررسی ");
                    register.enqueue(new Callback<SimpleResponse>() {
                        @Override
                        public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {

                            if (!response.isSuccessful()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
                                            (getActivity(),response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    MyProgress.cancelProgress();
                                }else {
                                    MyProgress.cancelProgress();
                                    Toast.makeText
                                            (getActivity(),response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                  getFragmentManager().popBackStack();

                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<SimpleResponse> call, Throwable t) {
                            if (t instanceof SocketTimeoutException){
                                Toast.makeText(getActivity(), R.string.timeout , Toast.LENGTH_SHORT).show();
                            }else if (t instanceof IOException) {
                                Toast.makeText(getActivity(), R.string.no_internet_connection , Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(), R.string.connection_problem , Toast.LENGTH_SHORT).show();
                                Log.e(TAG,t.getMessage());
                            }
                            MyProgress.cancelProgress();
                        }
                    });
                }


            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setToolbarText("ساخت مدیریت ");
    }

    public InputFilter getUsernameFilter() {
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        return filter;
    }
}
