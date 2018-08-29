package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.profile;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Fragments.Fragment_userManage.makeChanel.Fragment_makeChanel;
import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.InternetCheck;
import com.developer.hrg.nooadmin.Helper.MyProgress;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.Models.Chanel;
import com.developer.hrg.nooadmin.Models.Profile;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.R;
import com.developer.hrg.nooadmin.message_fragments.AudioFragment;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
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
    TextView tv_count ,tv_delete ;
    ImageView iv_back   ;
    Admin admin ;
    AdminInfo adminInfo ;
    ProgressBar progressBar ;
    ConstraintLayout constraintLayout ;
    ArrayList<Profile> profiles = new ArrayList<>();
    ViewPager viewPager;
    Adapter_Profile adapter_profile ;
    NetworkChangeReceiver networkChanereciver ;
    boolean firstTime = true;
    private static final String TAG = Profile_Fragment.class.getName();
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
        adapter_profile=new Adapter_Profile(getActivity(),profiles);
        networkChanereciver=new NetworkChangeReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile_, container, false);
         iv_back=(ImageView)view.findViewById(R.id.iv_back_profile);
         tv_count=(TextView)view.findViewById(R.id.tv_count_profile);
        tv_delete=(TextView)view.findViewById(R.id.tv_delete_profile);
        tv_delete.setVisibility(View.GONE);
         constraintLayout=(ConstraintLayout)view.findViewById(R.id.constraint_profile);
        progressBar=(ProgressBar)view.findViewById(R.id.progressbar_profile);
        viewPager=(ViewPager)view.findViewById(R.id.viewpager_profile);

        return view ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager.setAdapter(adapter_profile);
        iv_back.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
         getAllProfiles();

        handeViewpagerChaneLisener(viewPager);

    }
    private void getAllProfiles() {
        ApiInterface apiInterface = Client.getClient().create(ApiInterface.class);
        Call<SimpleResponse> call_profiles = apiInterface.getAllProfiles(chanel_id);
        call_profiles.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {

                if (!response.isSuccessful()) {

                }else {

                    profiles.addAll(response.body().getProfiles());
                    adapter_profile.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    tv_count.setText("1 / " + response.body().getProfiles().size());
                    tv_delete.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkChanereciver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(networkChanereciver);
    }

    public void handeViewpagerChaneLisener(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int current = position+1;
                tv_count.setText(current + " / " + profiles.size());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view==iv_back) {

            getFragmentManager().popBackStack();
        }else if (view==tv_delete) {
            if (viewPager.getCurrentItem()==0) {
                Toast.makeText(getActivity(), "عکس اصلی کانال قابل حذف نیست", Toast.LENGTH_SHORT).show();
            }else {
                ApiInterface apiInterface = Client.getClient().create(ApiInterface.class);
                int photo_id = profiles.get(viewPager.getCurrentItem()).getPhoto_id();
                String photo_name = profiles.get(viewPager.getCurrentItem()).getPhoto();
                Call<SimpleResponse> call_profiles = apiInterface.deleteChanelPhoto(admin.getApikey(),photo_id,photo_name);
                call_profiles.enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                        if (response.isSuccessful()) {
                            if (!response.body().isError()) {
                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                viewPager.removeViewAt(viewPager.getCurrentItem());
//                                profiles.remove(viewPager.getCurrentItem());
                                adapter_profile.notifyDataSetChanged();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        MyProgress.cancelProgress();
                        if (t instanceof SocketTimeoutException){
                            Toast.makeText(getActivity(), R.string.timeout , Toast.LENGTH_SHORT).show();
                        }else if (t instanceof IOException) {
                            Toast.makeText(getActivity(), R.string.no_internet_connection , Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getActivity(), R.string.connection_problem , Toast.LENGTH_SHORT).show();
                            Log.e(TAG,t.getMessage());
                        }
                    }
                });
            }



        }
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (firstTime) {
                firstTime=false;
            }else {
                if (InternetCheck.isOnline(getActivity())) {
                    if (profiles.size()==0) {
                        getAllProfiles();
                    }


                }
            }



        }
    }
}
