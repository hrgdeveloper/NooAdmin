package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.makeChanel;


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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.ImageCompression;
import com.developer.hrg.nooadmin.Helper.InternetCheck;
import com.developer.hrg.nooadmin.Helper.MyAlert;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_makeChanel extends Fragment implements View.OnClickListener {
  TextView tv_sabt ;
    ImageView iv_pic_image , iv_back ;
    EditText et_name , et_desc ;
    public final int GALLERY_REQUEST = 100 ;
    public final int  RESULT_LOAD_IMG_Gallery = 101 ;
    ImageCompression imageCompression;
    File picFile = null ;
    AdminInfo adminIngo ;
    Admin admin ;

    public Fragment_makeChanel() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adminIngo=new AdminInfo(getActivity());
        admin=adminIngo.getAdmin();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_make_chanel, container, false);
        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar_mChanel);
        tv_sabt=(TextView)toolbar.findViewById(R.id.tv_mChenl_sabt);
        iv_back=(ImageView)toolbar.findViewById(R.id.iv_mChanel_back);
        iv_pic_image=(ImageView)view.findViewById(R.id.iv_chanel_photo);
        et_name=(EditText)view.findViewById(R.id.et_chanel_name);
        et_desc=(EditText)view.findViewById(R.id.et_chanel_desc);
        imageCompression=new ImageCompression(getActivity());
        return view ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iv_back.setOnClickListener(this);
        iv_pic_image.setOnClickListener(this);
        tv_sabt.setOnClickListener(this);
        et_name.requestFocus();
    }

    @Override
    public void onClick(View view) {
        if (view==iv_back) {
            getFragmentManager().popBackStack();
        }else if (view==iv_pic_image) {
            if (Build.VERSION.SDK_INT>= 23) {
               askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,GALLERY_REQUEST);
            }else {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMG_Gallery);

            }

        }else if (view==tv_sabt) {

            String name = et_name.getText().toString();
            String desc = et_desc.getText().toString();
            if (name.length()==0) {
                Toast.makeText(getActivity(), "لطفا نام کانال را انتخاب نمایید", Toast.LENGTH_SHORT).show();

            }else if (desc.length()==0) {
                Toast.makeText(getActivity(),"لطفا توضیحات کانال را وارد نمایید",Toast.LENGTH_LONG).show();
            }else if (picFile==null) {
                Toast.makeText(getActivity(),"لطفا یک تصویر برای کانال انتخاب نمایید",Toast.LENGTH_LONG).show();
            }else if (!InternetCheck.isOnline(getActivity())) {
                Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            }else {
                JSONObject jsonobject = new JSONObject();
                try {
                    jsonobject.put("name",name);
                    jsonobject.put("description",desc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody req_details = RequestBody.create(MediaType.parse("text/plain"),jsonobject.toString());
                RequestBody req_pic = RequestBody.create(MediaType.parse("image/*") , picFile);
                MultipartBody.Part part = MultipartBody.Part.createFormData("pic",picFile.getName(),req_pic);
                ApiInterface api = Client.getClient().create(ApiInterface.class);
                Call<SimpleResponse> call = api.mChanel(admin.getApikey(),part,req_details);
                call.enqueue(new Callback<SimpleResponse>() {
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
                            String message = response.body().getMessage();
                            if (!error) {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStack();
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
                    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
            picFile = new File(filePath);
            iv_pic_image.setImageURI(Uri.parse(filePath));
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
