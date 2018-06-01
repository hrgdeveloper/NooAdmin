package com.developer.hrg.nooadmin.Fragments.Fragment_userManage;


import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.InternetCheck;
import com.developer.hrg.nooadmin.MainActivity.MainActivity;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.Models.User;
import com.developer.hrg.nooadmin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_UserManage extends Fragment implements GetUser_Adapter.MyClickListener {

RecyclerView recyclerView ;
    ArrayList<User> user_list =new ArrayList<>();
    LinearLayoutManager linearLayoutManager ;
    GetUser_Adapter getUser_adapter ;
    Admin admin ;
    public Fragment_UserManage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_manage, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerview_usermanage);
         getUser_adapter=new GetUser_Adapter(getActivity(),user_list);
        getUser_adapter.setMyClickListener(this);
         linearLayoutManager=new LinearLayoutManager(getActivity());
         admin= ((MainActivity)getActivity()).getAdmin();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(getUser_adapter);
      recyclerView.addItemDecoration(new SpacesItemDecoration(8));
        ApiInterface apiInterface = Client.getClient().create(ApiInterface.class);
        Call<SimpleResponse> response = apiInterface.getUsers(admin.getApikey());
        response.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (!response.isSuccessful()) {
                    try {
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
                    if (error) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                    user_list.addAll(response.body().getUser_list());
                        getUser_adapter.notifyDataSetChanged();
                    }
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
        ((MainActivity)getActivity()).setToolbarText("مدیریت کاربران");
    }

    @Override
    public void iv_active_clicked(final int position, View view) {
          int active = user_list.get(position).getActive();
           final int status = active==1 ? 0 : 1;
       if (InternetCheck.isOnline(getActivity())) {
           AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
           if (active==1) {
               alert.setMessage("آیا مایل به غیر فعال کردن کاربر میباشید");
           }else {
               alert.setMessage("آیا مایل به  فعال کردن کاربر میباشید");
           }
           alert.setPositiveButton("بلی", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   ApiInterface apiInterface = Client.getClient().create(ApiInterface.class);
                   Call<SimpleResponse> response = apiInterface.updateUserActive(admin.getApikey(), status,user_list.get(position).getId());
                   response.enqueue(new Callback<SimpleResponse>() {
                       @Override
                       public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                           if (!response.isSuccessful()) {
                               try {
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
                               if (!error) {
                                   user_list.get(position).setActive(status);
                                   getUser_adapter.notifyDataSetChanged();
                                   Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }else {
                                   Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }
                       }

                       @Override
                       public void onFailure(Call<SimpleResponse> call, Throwable t) {

                       }
                   });

               }
           });
           alert.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
               }
           });
           alert.show();


       }

    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if(parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }
}
