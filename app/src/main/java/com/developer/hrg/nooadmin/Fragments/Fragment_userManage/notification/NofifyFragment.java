package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.notification;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Fragments.Fragment_userManage.getChanels.Fragment_getAllChanels;
import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.ImageCompression;
import com.developer.hrg.nooadmin.Helper.MyProgress;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NofifyFragment extends Fragment implements View.OnClickListener{
  EditText et_title , et_message  ;
    ImageView iv_gallery , iv_pic ;
    Button btn_send ;
    File file = null ;
    Admin admin ;
    AdminInfo adminInfo ;
    ImageCompression imageCompression;
    public final int GALLERY_REQUEST = 100 ;
    public final int  RESULT_LOAD_IMG_Gallery = 101 ;
    private static final String TAG = NofifyFragment.class.getName();
    public NofifyFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageCompression=new ImageCompression(getActivity());
        adminInfo=new AdminInfo(getActivity());
        admin=adminInfo.getAdmin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nofify, container, false);
        et_title=(EditText)view.findViewById(R.id.et_notify_title);
        et_message=(EditText)view.findViewById(R.id.et_notify_text);
        iv_gallery=(ImageView)view.findViewById(R.id.iv_notify_gallery);
        iv_pic=(ImageView)view.findViewById(R.id.iv_notify_pic);
        btn_send=(Button)view.findViewById(R.id.btn_notify_send);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_send.setOnClickListener(this);
       iv_gallery.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view==btn_send) {
            String title = et_title.getText().toString();
            String message = et_message.getText().toString();
            if (file==null) {
                Toast.makeText(getActivity(), "ابتدا یک عکس انتخاب نمایید", Toast.LENGTH_SHORT).show();
            }else if(title.isEmpty() || message.isEmpty()) {
                Toast.makeText(getActivity(), "عنوان یا متن انتخاب نشده است", Toast.LENGTH_SHORT).show();
            }else {
                JSONObject jsonObject =new JSONObject();
                try {
                    MyProgress.showProgress(getActivity(),"لطفا صبر نمایید");
                    jsonObject.put("message",message);
                    jsonObject.put("title",title);

                    RequestBody reqestBody = RequestBody.create(MediaType.parse("text/pain"),jsonObject.toString());
                    RequestBody req_pic = RequestBody.create(MediaType.parse("image/*") , file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("pic",file.getName(),req_pic);
                    ApiInterface api = Client.getClient().create(ApiInterface.class);
                    Call<SimpleResponse> call = api.notify(admin.getApikey(),part,reqestBody);
                    call.enqueue(new Callback<SimpleResponse>() {
                        @Override
                        public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                            if (!response.isSuccessful()) {

                                try {
                                    Log.e(TAG,response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                MyProgress.cancelProgress();
                                Toast.makeText(getActivity(), "خطا .لطفا دوباره تلاش کنید", Toast.LENGTH_SHORT).show();

                            }else {
                                boolean error = response.body().isError();
                                String message = response.body().getMessage();
                                MyProgress.cancelProgress();
                                if (!error) {
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }else if (view==iv_gallery) {
            if (Build.VERSION.SDK_INT>= 23) {
                askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,GALLERY_REQUEST);
            }else {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMG_Gallery);

            }
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (requestCode==GALLERY_REQUEST){

            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMG_Gallery);


            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {


            case GALLERY_REQUEST:
                if(ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED ) {

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMG_Gallery);


                }else {

                    Toast.makeText(getActivity(),"عدم اجازه به دوربین",Toast.LENGTH_LONG).show();
                }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG_Gallery && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            String    realPath=getRealPathFromURI(getActivity(),selectedImage);
            String filePath = imageCompression.compressImage(realPath);
            file = new File(filePath);
            iv_pic.setImageURI(Uri.parse(filePath));
        }



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
}
