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

import com.developer.hrg.nooadmin.Fragments.Fragment_userManage.makeChanel.Fragment_makeChanel;
import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.Helper.Config;
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
import java.net.SocketTimeoutException;
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
public class AudioFragment extends Fragment implements View.OnClickListener,ProgressRequestBody.UploadCallbacks {

    public static final int ADUIO_GALLERY_REQUEST=100;
    public static String CHANEL_PARCABLE = "chanel";
    Chanel chanel ;
    TextView tv_audio_gallery , tv_send  , tv_time , tv_percent , tv_size , tv_name , tv_label;
    Button btn_type ;
    EditText et_text ;
    Admin admin ;
    int type = 4;
    String time , filename  ;
    File audiFile = null ;
    AdminInfo adminInfo ;
    CoordinatorLayout coordinatorLayout ;
    Call<SimpleResponse> callAudio ;
    boolean uploading = false ;
    private static final String TAG = AudioFragment.class.getName();
    public AudioFragment() {
        // Required empty public constructor
    }

    public static AudioFragment getInstance (Chanel chanel) {
        AudioFragment audioFragment = new AudioFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHANEL_PARCABLE,chanel);
        audioFragment.setArguments(bundle);
        return  audioFragment;
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
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        tv_audio_gallery=(TextView)view.findViewById(R.id.tv_audioFragment_gallery);
        tv_send=(TextView)view.findViewById(R.id.tv_audioFragment_send);
        tv_time=(TextView)view.findViewById(R.id.tv_time_audio);
        tv_percent=(TextView)view.findViewById(R.id.tv_audio_percent);
        tv_label=(TextView)view.findViewById(R.id.lable_fragment_audio);
        tv_size=(TextView)view.findViewById(R.id.tv_audio_size);
        tv_name=(TextView)view.findViewById(R.id.tv_audio_name);
        btn_type=(Button)view.findViewById(R.id.btn_type_audio);
        et_text=(EditText)view.findViewById(R.id.et_adudio_text);
        coordinatorLayout=(CoordinatorLayout)view.findViewById(R.id.coordinate_audio_fragment);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_audio_gallery.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        tv_label.setText("کانال "+ chanel.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onClick(View view) {
        if (view==tv_audio_gallery) {
            if (Build.VERSION.SDK_INT>=23) {
                requestPremission(Manifest.permission.WRITE_EXTERNAL_STORAGE,ADUIO_GALLERY_REQUEST);
            }else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,ADUIO_GALLERY_REQUEST);
            }

        }else if (view==tv_send) {
            if (uploading) {
              cancelUpload();
            }else {
                String message = et_text.getText().toString();
                if (audiFile == null) {
                    MySnack.showSnack(coordinatorLayout, "لطفا ابتدا یک ویدیو انتخاب نمایید");
                } else {

                    final JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("type", type);
                        jsonObject.put("admin_id", admin.getAdmin_id());
                        jsonObject.put("message", message.length() == 0 ? null : message);
                        jsonObject.put("time", time);
                        jsonObject.put("filename", filename);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    uploading=true;
                    tv_send.setText("لغو ارسال");

                    RequestBody req_content = RequestBody.create(MediaType.parse("text/plain"), jsonObject.toString());
                    ProgressRequestBody audioBody = new ProgressRequestBody(audiFile, this, "audio/*");
                    MultipartBody.Part audioPart = MultipartBody.Part.createFormData("file", audiFile.getName(), audioBody);
                    ApiInterface api = Client.getClient().create(ApiInterface.class);
                    callAudio = api.makeAudioMessage(admin.getApikey(), Integer.valueOf(chanel.getChanel_id()), req_content,audioPart
                    );
                    callAudio.enqueue(new Callback<SimpleResponse>() {
                        @Override
                        public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                            if (!response.isSuccessful()) {
                                try {

                                    JSONObject jsonobjectt = new JSONObject(response.errorBody().string());
                                    String message = jsonobjectt.getString("message");
                                    MySnack.showSnack(coordinatorLayout, message);
                                    cancelUpload();

                                } catch (JSONException e) {
                                    Toast.makeText(getActivity(), R.string.badResponseException, Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } else {

                                boolean error = response.body().isError();
                                String message = response.body().getMessage();
                                if (!error) {
                                    if (!Config.isAppIsInBackground(getActivity())) {
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        getFragmentManager().popBackStack();
                                    }else {
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        tv_percent.setText(0+" %");
                                        audiFile=null;
                                        tv_send.setText("ارسال");
                                    }


                                } else {
                                   cancelUpload();
                                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
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

                           cancelUpload();


                        }
                    });
            }



            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADUIO_GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null)  {

            Uri selectedAudio = data.getData();
         //   final String    realPath=getPath(selectedAudio);
            final String    realPath= RealPathUtil.getPath(getActivity(),selectedAudio);
            audiFile = new File(realPath);
            String extenstion = audiFile.getName().substring(audiFile.getName().lastIndexOf(".")+1);
            btn_type.setText(extenstion);
            filename=audiFile.getName();
            tv_name.setText(filename);
            tv_size.setText(readableFileSize(audiFile.length()));

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getActivity(), Uri.fromFile(audiFile));
            time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time );
            time = String.format("%02d:%02d ",
                    TimeUnit.MILLISECONDS.toMinutes(timeInMillisec),
                    TimeUnit.MILLISECONDS.toSeconds(timeInMillisec) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillisec))
            );
            tv_time.setText(time);
            tv_name.setVisibility(View.VISIBLE);
            tv_size.setVisibility(View.VISIBLE);
            tv_time.setVisibility(View.VISIBLE);
        }
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public void cancelUpload(){
        callAudio.cancel();
        tv_send.setText("ارسال");
        uploading=false;
        tv_percent.setText("0%");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==ADUIO_GALLERY_REQUEST) {
            if (ActivityCompat.checkSelfPermission(getActivity(),permissions[0])== PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,ADUIO_GALLERY_REQUEST);
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
        if (request==ADUIO_GALLERY_REQUEST) {
            if (ContextCompat.checkSelfPermission(getActivity(),premission)!= PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),premission)) {
                    ActivityCompat.requestPermissions(getActivity(),new String[] {premission},ADUIO_GALLERY_REQUEST);
                }
                ActivityCompat.requestPermissions(getActivity(),new String[] {premission},ADUIO_GALLERY_REQUEST);
            }else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,ADUIO_GALLERY_REQUEST);


            }

        }

    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Audio.Media.DATA };
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
        tv_percent.setText(percentage+" %");

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }
}
