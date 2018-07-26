package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.profile;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.Models.Chanel;
import com.developer.hrg.nooadmin.Models.Profile;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.R;
import com.developer.hrg.nooadmin.message_fragments.AudioFragment;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_Fragment extends Fragment implements View.OnClickListener {

    public static final int ADUIO_GALLERY_REQUEST=100;
    public static String CHANEL_ID = "chanel_iD";
    int  chanel_id ;
    TextView tv_count ;
    ImageView iv_back ;
    Admin admin ;
    AdminInfo adminInfo ;
    ProgressBar progressBar ;
    ConstraintLayout constraintLayout ;
    ArrayList<Profile> profiles = new ArrayList<>();
    public Profile_Fragment() {
        // Required empty public constructor
    }

    public static Profile_Fragment getInstance (int chanel_id) {
        Profile_Fragment profile_fragment = new Profile_Fragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CHANEL_ID,chanel_id);
        profile_fragment.setArguments(bundle);
        return  profile_fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            chanel_id=getArguments().getInt(CHANEL_ID);
        }
        adminInfo=new AdminInfo(getActivity());
        admin= adminInfo.getAdmin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile_, container, false);
         iv_back=(ImageView)view.findViewById(R.id.iv_back_profile);
         tv_count=(TextView)view.findViewById(R.id.tv_count_profile);
         constraintLayout=(ConstraintLayout)view.findViewById(R.id.constraint_profile);
        progressBar=(ProgressBar)view.findViewById(R.id.progressbar_profile);

        return view ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ApiInterface apiInterface = Client.getClient().create(ApiInterface.class);
        Call<SimpleResponse> call_profiles = apiInterface.getAllProfiles(chanel_id);
        call_profiles.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {

                if (!response.isSuccessful()) {

                }else {
                    progressBar.setVisibility(View.GONE);
                    tv_count.setText("1 / " + response.body().getProfiles().size());
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view==iv_back) {
            getFragmentManager().popBackStack();
        }
    }
}
