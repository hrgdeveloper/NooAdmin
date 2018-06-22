package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.getChanels;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.MainActivity.MainActivity;
import com.developer.hrg.nooadmin.Models.Chanel;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.R;
import com.developer.hrg.nooadmin.message_fragments.Simple_fragment;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_getAllChanels extends Fragment implements GetChanelsAdapter.MyClickListener {
  RecyclerView recyclerView;
    ArrayList<Chanel> chanels = new ArrayList<>();
    GetChanelsAdapter adapter_chanels ;
   // LinearLayoutManager linearLayoutManager ;
    AdminInfo adminInfo ;

    public Fragment_getAllChanels() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter_chanels=new GetChanelsAdapter(getActivity(),chanels);
     //   linearLayoutManager=new LinearLayoutManager(getActivity());
        adminInfo=new AdminInfo(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_get_all_chanels, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.recycle_chanels);
        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter_chanels);
        adapter_chanels.setMyClickListener(this);
        ApiInterface api = Client.getClient().create(ApiInterface.class);
        Call<SimpleResponse> call = api.getAllChanels(adminInfo.getAdmin().getApikey());
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (!response.isSuccessful()) {
                    try {
                        Toast.makeText(getActivity(), response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    chanels.addAll(response.body().getChanels());
                    adapter_chanels.notifyDataSetChanged();

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
        ((MainActivity)getActivity()).setToolbarText("مدیریت کانال ها");
    }

    @Override
    public void chanel_clicked(final int position, View view) {

        final PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.getMenuInflater().inflate(R.menu.menu_chanel_list,popup.getMenu());
        popup.setGravity(Gravity.RIGHT);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId()==R.id.menu_message_simple) {
                      openFragment(Simple_fragment.getInstance(chanels.get(position)));
                    }

                return false;
            }
        });

        popup.show();

    }

    @Override
    public void chanel_long_clicked(final int position, View view) {


    }

  public void  openFragment( Fragment fragment) {
      FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
      fragmentTransaction.add(R.id.rootLayout, fragment);
      fragmentTransaction.addToBackStack(null);
      fragmentTransaction.commit();
      ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
