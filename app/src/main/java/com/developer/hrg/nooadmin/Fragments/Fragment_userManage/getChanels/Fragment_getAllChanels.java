package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.getChanels;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Fragments.Fragment_userManage.comment.CommentFragment;
import com.developer.hrg.nooadmin.Fragments.Fragment_userManage.profile.Profile_Fragment;
import com.developer.hrg.nooadmin.Helper.AdminData;
import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.ImageCompression;
import com.developer.hrg.nooadmin.Helper.InternetCheck;
import com.developer.hrg.nooadmin.Helper.MyAlert;
import com.developer.hrg.nooadmin.Helper.MyProgress;
import com.developer.hrg.nooadmin.MainActivity.MainActivity;
import com.developer.hrg.nooadmin.Models.Chanel;
import com.developer.hrg.nooadmin.Models.Comment_Read;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.R;
import com.developer.hrg.nooadmin.message_fragments.AudioFragment;
import com.developer.hrg.nooadmin.message_fragments.FileFragment;
import com.developer.hrg.nooadmin.message_fragments.PictureFragment;
import com.developer.hrg.nooadmin.message_fragments.Simple_fragment;
import com.developer.hrg.nooadmin.message_fragments.VideoFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    ImageCompression imageCompression;
    File pic_update = null ;
    File pic_add = null ;
    String last_pic_name = null ;
    int chanel_id;
    int position_list;
    ImageView iv_reload ;
    ArrayList<Comment_Read> comment_reads = new ArrayList<>();
    AdminData adminData ;
    private static Fragment_getAllChanels  instance ;
    private static final String TAG = Fragment_getAllChanels.class.getName();


   // LinearLayoutManager linearLayoutManager ;
    AdminInfo adminInfo ;
    public static final int GALLERY_REQUEST_FOR_UPDATE_PROFILE = 101 ;
    public static final int GALLERY_REQUEST_FOR_ADD_PROFILE= 102 ;
    public Fragment_getAllChanels() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
        adminData=new AdminData(getActivity());
        if (adminData.hasreadData()) {
            comment_reads.addAll(adminData.getAllReads());

        }
        adapter_chanels=new GetChanelsAdapter(getActivity(),chanels,comment_reads);
        adminInfo=new AdminInfo(getActivity());
        imageCompression=new ImageCompression(getActivity());

    }
    public static Fragment_getAllChanels getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_get_all_chanels, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.recycle_chanels);
        iv_reload=(ImageView)view.findViewById(R.id.iv_reload_getchanels);
        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter_chanels);
        adapter_chanels.setMyClickListener(this);
         getChanels();

        iv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChanels();
            }
        });

    }
    public void getChanels(){
        iv_reload.setVisibility(View.GONE);
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
                    if (comment_reads.size() < chanels.size()) {
                        for (int i = comment_reads.size(); i < chanels.size(); i++) {
                            Comment_Read read = new Comment_Read(Integer.valueOf(chanels.get(i).getChanel_id()),0);
                            adminData.addread(read);
                            comment_reads.add(read);
                        }
                    }

                    adapter_chanels.notifyDataSetChanged();

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
                iv_reload.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setToolbarText("مدیریت کانال ها");

    }
    public void updateRead(int position , Comment_Read comment_read) {
        comment_reads.set(position,comment_read);
        adapter_chanels.notifyDataSetChanged();

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
                      openFragment(Simple_fragment.getInstance(chanels.get(position)),false);
                    }else if (item.getItemId()==R.id.menu_message_pic) {

                        openFragment(PictureFragment.getInstance(chanels.get(position)),true);

                    }else if (item.getItemId()==R.id.menu_message_movie) {
                        openFragment(VideoFragment.getInstance(chanels.get(position)),true);
                    }else if (item.getItemId()==R.id.menu_message_audio) {
                        openFragment(AudioFragment.getInstance(chanels.get(position)),true);
                    }else if (item.getItemId()==R.id.menu_message_file) {
                        openFragment(FileFragment.getInstance(chanels.get(position)),true);
                    } else if (item.getItemId()==R.id.menu_profile_update) {
                        if (!InternetCheck.isOnline(getActivity())) {
                            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                        }else {
                            if (Build.VERSION.SDK_INT>= 23) {
                                last_pic_name=chanels.get(position).getThumb();
                                chanel_id = Integer.valueOf(chanels.get(position).getChanel_id());
                                position_list=position;

                                askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,GALLERY_REQUEST_FOR_UPDATE_PROFILE);

                            }else {
                                last_pic_name=chanels.get(position).getThumb();
                                chanel_id = Integer.valueOf(chanels.get(position).getChanel_id());
                                position_list=position;
                                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, GALLERY_REQUEST_FOR_UPDATE_PROFILE);

                            }
                        }

                    }else if (item.getItemId()==R.id.menu_profile_add) {
                        if (Build.VERSION.SDK_INT>= 23) {
                            chanel_id = Integer.valueOf(chanels.get(position).getChanel_id());
                            position_list=position;
                            askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,GALLERY_REQUEST_FOR_ADD_PROFILE);

                        }else {
                            chanel_id = Integer.valueOf(chanels.get(position).getChanel_id());
                            position_list=position;
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, GALLERY_REQUEST_FOR_ADD_PROFILE);

                        }

                    }else if (item.getItemId()==R.id.menu_get_profiles) {
                        openFragment(Profile_Fragment.getInstance(Integer.valueOf( chanels.get(position).getChanel_id())),true);

                    }

                return false;
            }
        });

        popup.show();

    }

    @Override
    public void chanel_long_clicked(final int position, View view) {
        Toast.makeText(getActivity(), chanels.get(position).getCm_count()+" ", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void comment_clicked(int position, View view) {
        openFragment(CommentFragment.getInstance(Integer.valueOf(chanels.get(position).getChanel_id()),chanels.get(position).getName(),position),
                false);

    }

    public void  openFragment( Fragment fragment , boolean container_full) {
      FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
      if (container_full) {
          fragmentTransaction.add(R.id.container_full, fragment);
      }else {
          fragmentTransaction.add(R.id.rootLayout, fragment);
      }

      fragmentTransaction.addToBackStack(null);
      fragmentTransaction.commit();
      ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_FOR_UPDATE_PROFILE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            String    realPath=getRealPathFromURI(getActivity(),selectedImage);
            String filePath = imageCompression.compressImage(realPath);
            pic_update = new File(filePath);
            MyProgress.showProgress(getActivity(),"در حال ارسال ...");
            RequestBody req_lastPicName = RequestBody.create(MediaType.parse("text/plain"),last_pic_name);
            RequestBody req_pic = RequestBody.create(MediaType.parse("image/jpeg") , pic_update);
            MultipartBody.Part part_pic = MultipartBody.Part.createFormData("pic",pic_update.getName(),req_pic);

            ApiInterface api = Client.getClient().create(ApiInterface.class);
            Call<SimpleResponse> profile_call = api.updateChanelPic(adminInfo.getAdmin().getApikey() ,chanel_id, part_pic,req_lastPicName);
            profile_call.enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    if (!response.isSuccessful()) {
                        MyProgress.cancelProgress();
                        Toast.makeText(getActivity(), "خطلایی پیش آمده دوباره تلاش کنید", Toast.LENGTH_SHORT).show();


                    }else {
                        boolean error = response.body().isError();
                        String message = response.body().getMessage();
                        if (!error) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            MyProgress.cancelProgress();
                            Chanel chanel = chanels.get(position_list);
                            chanel.setThumb(response.body().getPic_name());
                            chanels.set(position_list,chanel);
                            adapter_chanels.notifyDataSetChanged();


                        }else {
                            MyProgress.cancelProgress();
                            Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    Toast.makeText(getActivity() , "خطایی پیش آمده لطفا دوباره تلاش کنید", Toast.LENGTH_SHORT).show();
                    MyProgress.cancelProgress();
                }
            });


        }else if (requestCode == GALLERY_REQUEST_FOR_ADD_PROFILE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            String    realPath=getRealPathFromURI(getActivity(),selectedImage);
            String filePath = imageCompression.compressImage(realPath);
            pic_add = new File(filePath);
            MyProgress.showProgress(getActivity(),"در حال ارسال ...");

            RequestBody req_pic = RequestBody.create(MediaType.parse("image/jpeg") , pic_add);
            MultipartBody.Part part_pic = MultipartBody.Part.createFormData("pic",pic_add.getName(),req_pic);

            ApiInterface api = Client.getClient().create(ApiInterface.class);
            Call<SimpleResponse> profile_call = api.addChanelPic(adminInfo.getAdmin().getApikey() ,chanel_id, part_pic);
            profile_call.enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    if (!response.isSuccessful()) {
                        MyProgress.cancelProgress();
                        Toast.makeText(getActivity(), "خطلایی پیش آمده دوباره تلاش کنید", Toast.LENGTH_SHORT).show();


                    }else {
                        boolean error = response.body().isError();
                        String message = response.body().getMessage();
                        if (!error) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            MyProgress.cancelProgress();


                        }else {
                            MyProgress.cancelProgress();
                            Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    Toast.makeText(getActivity() , "خطایی پیش آمده لطفا دوباره تلاش کنید", Toast.LENGTH_SHORT).show();
                    MyProgress.cancelProgress();
                }
            });

        }

    }
    private void askForPermission(String permission, Integer requestCode) {
    if (requestCode==GALLERY_REQUEST_FOR_ADD_PROFILE  ) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQUEST_FOR_ADD_PROFILE);


            }
        }else if (requestCode==GALLERY_REQUEST_FOR_UPDATE_PROFILE) {

        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_FOR_UPDATE_PROFILE);


        }
    }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {




            case GALLERY_REQUEST_FOR_ADD_PROFILE:
                if(ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED ) {

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY_REQUEST_FOR_ADD_PROFILE);

                }else {

                    Toast.makeText(getActivity(),"عدم دسترسی به گالری",Toast.LENGTH_LONG).show();
                }

               break;
            case GALLERY_REQUEST_FOR_UPDATE_PROFILE :

                if(ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED ) {

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY_REQUEST_FOR_UPDATE_PROFILE);

                }else {

                    Toast.makeText(getActivity(),"عدم دسترسی به گالری",Toast.LENGTH_LONG).show();
                }
        }
    }

}
