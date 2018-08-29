package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.userManage;


import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Fragments.Fragment_userManage.getChanels.Fragment_getAllChanels;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.InternetCheck;
import com.developer.hrg.nooadmin.Helper.MyAlert;
import com.developer.hrg.nooadmin.Helper.MyProgress;
import com.developer.hrg.nooadmin.MainActivity.MainActivity;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.Models.User;
import com.developer.hrg.nooadmin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_UserManage extends Fragment implements GetUser_Adapter.MyClickListener,SearchView.OnQueryTextListener {

RecyclerView recyclerView ;
    ArrayList<User> user_list =new ArrayList<>();
    LinearLayoutManager linearLayoutManager ;
    GetUser_Adapter getUser_adapter ;
    Admin admin ;
    ImageView iv_reload ;
    SearchView searchView ;
    private static final String TAG = Fragment_getAllChanels.class.getName();
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
        iv_reload=(ImageView)view.findViewById(R.id.iv_reload_getuser);
        searchView=(SearchView)view.findViewById(R.id.searchView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (!searchView.isIconified()) {
                            searchView.setIconified(true);
                        }else {
                            ((MainActivity)getActivity()).onBackPressed();
                        }


                        return true;
                    }
                }
                return false;
            }
        });

        MyProgress.showProgress(getActivity(),"در حال دریافت لیست کاربران..");
        searchView.setOnQueryTextListener(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(getUser_adapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(8));

         getUsers();
        iv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             getUsers();
            }
        });
    }

    public void getUsers() {
        iv_reload.setVisibility(View.GONE);
        ApiInterface apiInterface = Client.getClient().create(ApiInterface.class);
        Call<SimpleResponse> response = apiInterface.getUsers(admin.getApikey());
        response.enqueue(new Callback<SimpleResponse>() {
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
                    if (error) {
                        MyProgress.cancelProgress();
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        searchView.setVisibility(View.VISIBLE);
                        MyProgress.cancelProgress();
                        user_list.addAll(response.body().getUser_list());
                        getUser_adapter.notifyDataSetChanged();
                        getUser_adapter.copyItems(user_list);

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
                iv_reload.setVisibility(View.VISIBLE);
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
                   Call<SimpleResponse> response = apiInterface.updateUserActive(admin.getApikey(), user_list.get(position).getId(),status);
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
                           MyAlert.showAlert(getActivity(),"خطا"," خطای "+ "\n" + t.getMessage().toString() + "\n"+ "لطفا دوباره تلاش کنید");
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


      }else {
           Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
       }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        getUser_adapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        getUser_adapter.filter(newText);
        return true;
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
