package com.developer.hrg.nooadmin.message_fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.Config;
import com.developer.hrg.nooadmin.Helper.MyAlert;
import com.developer.hrg.nooadmin.Helper.MyProgress;
import com.developer.hrg.nooadmin.MainActivity.MainActivity;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.Models.Chanel;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Simple_fragment extends Fragment implements View.OnClickListener {

    public static String CHANEL_PARCABLE = "chanel";


  Chanel chanel ;
    EditText et_message ;
    Button btn_send ;
    int type = 1;
    Admin admin ;
    AdminInfo adminInfo ;



    public static Simple_fragment getInstance (Chanel chanel) {
        Simple_fragment simple_fragment = new Simple_fragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHANEL_PARCABLE,chanel);
        simple_fragment.setArguments(bundle);
        return simple_fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          if (getArguments()!=null) {
              chanel=getArguments().getParcelable(CHANEL_PARCABLE);
          }
          adminInfo=new AdminInfo(getActivity());
          admin= adminInfo.getAdmin();
    }

    public Simple_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_simple, container, false);
        et_message=(EditText)view.findViewById(R.id.et_send_message_simple);
        btn_send=(Button)view.findViewById(R.id.btn_send_message_simple);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_send.setOnClickListener(this);
        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              if (charSequence.length()==0) {
                  btn_send.setEnabled(false);
              }else {
                  btn_send.setEnabled(true);
              }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setToolbarText("ایجاد پیام ساده در "+chanel.getName());
    }

    @Override
    public void onClick(View view) {
             if (view==btn_send) {

                 String message = et_message.getText().toString();
                 JSONObject jsonObject = new JSONObject();
                 try {
                     jsonObject.put(Config.MESSAGE_MESSAGE,message);
                     jsonObject.put(Config.MESSAGE_TYPE,type);
                     jsonObject.put(Config.MESSAGE_ADMINID,admin.getAdmin_id());
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
                 MyProgress.showProgress(getActivity(),"در حال ارسال ...");

                 RequestBody req = RequestBody.create(MediaType.parse("text/plain"),jsonObject.toString());

                 ApiInterface api = Client.getClient().create(ApiInterface.class);
                 Call<SimpleResponse> call = api.makeSimpleMessage(admin.getApikey(),Integer.valueOf(chanel.getChanel_id()),req);
                 call.enqueue(new Callback<SimpleResponse>() {
                     @Override
                     public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                         if (!response.isSuccessful()) {
                             try {
                                 MyProgress.cancelProgress();
                                 JSONObject jsonobject = new JSONObject(response.errorBody().string());
                                 String message = jsonobject.getString("message");
                                 Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }

                         }else {
                             boolean error = response.body().isError();
                             String message = response.body().getMessage();
                             if (!error) {
                                 MyProgress.cancelProgress();
                                 Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                 getFragmentManager().popBackStack();
                             }else {
                                 MyProgress.cancelProgress();
                                 Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                             }
                         }
                     }

                     @Override
                     public void onFailure(Call<SimpleResponse> call, Throwable t) {
                         MyProgress.cancelProgress();
                         Toast.makeText(getActivity(), t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                         MyAlert.showAlert(getActivity(),"خطا"," خطای "+ "\n" + t.getMessage().toString() + "\n"+ "لطفا دوباره تلاش کنید");

                     }
                 });
             }
         }
    }
