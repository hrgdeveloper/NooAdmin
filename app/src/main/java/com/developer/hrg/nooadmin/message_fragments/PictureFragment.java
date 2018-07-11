package com.developer.hrg.nooadmin.message_fragments;


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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.ImageCompression;
import com.developer.hrg.nooadmin.Helper.InternetCheck;
import com.developer.hrg.nooadmin.Helper.MyAlert;
import com.developer.hrg.nooadmin.Helper.MyProgress;
import com.developer.hrg.nooadmin.Helper.MySnack;
import com.developer.hrg.nooadmin.Helper.ProgressRequestBody;
import com.developer.hrg.nooadmin.MainActivity.MainActivity;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.Models.Chanel;
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
public class PictureFragment extends Fragment implements View.OnClickListener,ProgressRequestBody.UploadCallbacks {
    public static final int GALLERY_REQUEST=100;
    public static String CHANEL_PARCABLE = "chanel";
    Chanel chanel ;
    TextView tv_gallery , tv_send ;
    EditText et_message ;
     File file = null;
    ImageView iv_pic ;
    int type = 2;
    FrameLayout frameLayout ;
    Admin admin ;
    AdminInfo adminInfo ;
    CoordinatorLayout coordinatorLayout ;
   TextView tv_percent ;
    ImageCompression imageCompression ;
    public PictureFragment() {

    }

    public static PictureFragment getInstance (Chanel chanel) {
        PictureFragment pictureFragment = new PictureFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHANEL_PARCABLE,chanel);
        pictureFragment.setArguments(bundle);
        return  pictureFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            chanel=getArguments().getParcelable(CHANEL_PARCABLE);
        }
        adminInfo=new AdminInfo(getActivity());
        admin= adminInfo.getAdmin();
        imageCompression=new ImageCompression(getActivity());
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        tv_gallery=(TextView)view.findViewById(R.id.tv_pictureFragment_gallery);
        tv_send=(TextView)view.findViewById(R.id.tv_pictureFragment_send);
        et_message=(EditText)view.findViewById(R.id.et_pictureFragment);
        frameLayout=(FrameLayout) view.findViewById(R.id.framelayout_fragment_pic);
        iv_pic=(ImageView)view.findViewById(R.id.iv_picFragment);
        tv_percent=(TextView)view.findViewById(R.id.tv_pictureFragment_percent);
        coordinatorLayout=(CoordinatorLayout)view.findViewById(R.id.coordinate_picture_fragment);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_gallery.setOnClickListener(this);
        tv_send.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if (view==tv_gallery) {
            if (Build.VERSION.SDK_INT>=23) {
                requestPremission(Manifest.permission.WRITE_EXTERNAL_STORAGE,GALLERY_REQUEST);
            }else {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,GALLERY_REQUEST);
            }

        }else if (view==tv_send) {


            String message = et_message.getText().toString();
            if (file==null) {
                MySnack.showSnack(coordinatorLayout,"لطفا ابتدا یک عکس انتخاب نمایید");
            }else if (!InternetCheck.isOnline(getActivity())) {
                MySnack.showSnack(coordinatorLayout,"عدم دسترسی به اینترنت .. لطفا وضعیت اینترنت خود را برسی نمایید");
            }else {
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type",type);
                    jsonObject.put("admin_id",admin.getAdmin_id());
                    jsonObject.put("message",message.length()==0 ? null : message);




                } catch (JSONException e) {e.printStackTrace();

                }

                RequestBody req_content = RequestBody.create(MediaType.parse("text/plain"),jsonObject.toString());

                ProgressRequestBody progress_file = new ProgressRequestBody(file,this,"image/jpeg");
                MultipartBody.Part file_part = MultipartBody.Part.createFormData("file",file.getName(),progress_file);
                ApiInterface api = Client.getClient().create(ApiInterface.class);
                Call<SimpleResponse> call = api.makePictureMessage(admin.getApikey(),Integer.valueOf(chanel.getChanel_id()) ,req_content,file_part);

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
                                MySnack.showSnack(coordinatorLayout,message);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else {
                            Log.e("tosalem" , "errorNadare");
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
                        Log.e("tomoshkel" , "moshkelDare");

                      //  Toast.makeText(getActivity(), t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        MyAlert.showAlert(getActivity(),"خطا"," خطای "+ "\n" + t.toString() + "\n"+ "لطفا دوباره تلاش کنید");

                    }
                });

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==GALLERY_REQUEST) {
            if (ActivityCompat.checkSelfPermission(getActivity(),permissions[0])==PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,GALLERY_REQUEST);
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
        if (request==GALLERY_REQUEST) {
            if (ContextCompat.checkSelfPermission(getActivity(),premission)!= PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),premission)) {
                    ActivityCompat.requestPermissions(getActivity(),new String[] {premission},GALLERY_REQUEST);
                }
                ActivityCompat.requestPermissions(getActivity(),new String[] {premission},GALLERY_REQUEST);
            }else {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,GALLERY_REQUEST);


            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null)  {

            Uri selectedImage = data.getData();
            String    realPath=getRealPathFromURI(getActivity(),selectedImage);

            String filePath = imageCompression.compressImage(realPath);

            file = new File(filePath);
            iv_pic.setImageURI(Uri.parse(filePath));

//            frameLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),android.R.color.black));

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

    @Override
    public void onProgressUpdate(int percentage) {
           tv_percent.setText(percentage+"%");
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }
}
