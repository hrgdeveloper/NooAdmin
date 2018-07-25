package com.developer.hrg.nooadmin.message_fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.ImageCompression;
import com.developer.hrg.nooadmin.Helper.InternetCheck;
import com.developer.hrg.nooadmin.Helper.MyAlert;
import com.developer.hrg.nooadmin.Helper.MySnack;
import com.developer.hrg.nooadmin.Helper.ProgressRequestBody;
import com.developer.hrg.nooadmin.Helper.RealPathUtil;
import com.developer.hrg.nooadmin.MainActivity.MainActivity;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.Models.Chanel;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FileFragment extends Fragment implements View.OnClickListener,ProgressRequestBody.UploadCallbacks {

    public static final int FILE_REQUEST=100;
    public static String CHANEL_PARCABLE = "chanel";
    Chanel chanel ;
    TextView tv_file_gallery , tv_send   , tv_percent , tv_size , tv_name;
    Button btn_type ;
    EditText et_text ;
    Admin admin ;
    int type = 5;
    String time , filename  ;
    File file = null ;
    AdminInfo adminInfo ;
    CoordinatorLayout coordinatorLayout ;
    public FileFragment() {
        // Required empty public constructor
    }

    public static FileFragment getInstance (Chanel chanel) {
        FileFragment fileFragment = new FileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHANEL_PARCABLE,chanel);
        fileFragment.setArguments(bundle);
        return  fileFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            chanel=getArguments().getParcelable(CHANEL_PARCABLE);
        }
        adminInfo=new AdminInfo(getActivity());
        admin= adminInfo.getAdmin();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file, container, false);
        tv_file_gallery=(TextView)view.findViewById(R.id.tv_fileFragment_gallery);
        tv_send=(TextView)view.findViewById(R.id.tv_fileFragment_send);

        tv_percent=(TextView)view.findViewById(R.id.tv_file_percent);

        tv_size=(TextView)view.findViewById(R.id.tv_file_size);
        tv_name=(TextView)view.findViewById(R.id.tv_file_name);
        btn_type=(Button)view.findViewById(R.id.btn_type_file);
        et_text=(EditText)view.findViewById(R.id.et_file_text);
        coordinatorLayout=(CoordinatorLayout)view.findViewById(R.id.coordinate_file_fragment);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_file_gallery.setOnClickListener(this);
        tv_send.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if (view==tv_file_gallery) {
            if (Build.VERSION.SDK_INT>=23) {
                requestPremission(Manifest.permission.WRITE_EXTERNAL_STORAGE,FILE_REQUEST);
            }else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILE_REQUEST);
            }

        }else if (view==tv_send) {
            String message = et_text.getText().toString();
            if (file == null) {
                MySnack.showSnack(coordinatorLayout, "لطفا ابتدا یک ویدیو انتخاب نمایید");
            } else if (!InternetCheck.isOnline(getActivity())) {
                MySnack.showSnack(coordinatorLayout, "عدم دسترسی به اینترنت .. لطفا وضعیت اینترنت خود را برسی نمایید");
            } else {
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", type);
                    jsonObject.put("admin_id", admin.getAdmin_id());
                    jsonObject.put("message", message.length() == 0 ? null : message);
                    jsonObject.put("filename", filename);

                } catch (JSONException e) {
                    e.printStackTrace();

                }

                RequestBody req_content = RequestBody.create(MediaType.parse("text/plain"), jsonObject.toString());

            //    ProgressRequestBody fileBody = new ProgressRequestBody(file, this, "multipart/form-data");
                ProgressRequestBody fileBody = new ProgressRequestBody(file, this, "file/*");
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);



                ApiInterface api = Client.getClient().create(ApiInterface.class);
                Call<SimpleResponse> call = api.makeFileMessage(admin.getApikey(), Integer.valueOf(chanel.getChanel_id()), req_content,filePart
                );

                call.enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                        if (!response.isSuccessful()) {
                            try {
//                                  Log.e("toerror" , "injast");
//                                  Log.e("toerror",response.raw().message());
//                                Log.e("toerror",response.errorBody().string());

                                JSONObject jsonobjectt = new JSONObject(response.errorBody().string());

                                //     Log.e("toerror",jsonObject.toString());

                                String message = jsonobjectt.getString("message");
                                MySnack.showSnack(coordinatorLayout, message);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {

                            boolean error = response.body().isError();
                            String message = response.body().getMessage();
                            if (!error) {

                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStack();
                            } else {

                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        Log.e("tomoshkel", "moshkelDare");

                        //  Toast.makeText(getActivity(), t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        MyAlert.showAlert(getActivity(), "خطا", " خطای " + "\n" + t.toString() + "\n" + "لطفا دوباره تلاش کنید");

                    }
                });

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null)  {

            Uri selectedfile = data.getData();
            String realPath = RealPathUtil.getPath(getActivity(),selectedfile);


            file = new File(realPath);
            String extenstion = file.getName().substring(file.getName().lastIndexOf(".")+1);
            btn_type.setText(extenstion);
            filename=file.getName();
            tv_name.setText(filename);
            tv_size.setText(readableFileSize(file.length()));


            tv_name.setVisibility(View.VISIBLE);
            tv_size.setVisibility(View.VISIBLE);

        }
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==FILE_REQUEST) {
            if (ActivityCompat.checkSelfPermission(getActivity(),permissions[0])== PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILE_REQUEST);
            }else {
                Toast.makeText(getActivity(), "اجازه دسترسی داده نشد", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    public void requestPremission(String premission , int request) {
        if (request==FILE_REQUEST) {
            if (ContextCompat.checkSelfPermission(getActivity(),premission)!= PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),premission)) {
                    ActivityCompat.requestPermissions(getActivity(),new String[] {premission},FILE_REQUEST);
                }
                ActivityCompat.requestPermissions(getActivity(),new String[] {premission},FILE_REQUEST);
            }else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILE_REQUEST);

            }

        }

    }
    public String getPath(Uri uri) {
        String[] projection = {"_data"};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

        } else
            return null;
    }


    @Override
    public void onProgressUpdate(int percentage) {
        tv_percent.setText(percentage+"");

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }
}
