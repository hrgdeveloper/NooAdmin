package com.developer.hrg.nooadmin.message_fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
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

import com.yovenny.videocompress.MediaController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
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
public class VideoFragment extends Fragment implements View.OnClickListener,ProgressRequestBody.UploadCallbacks {
    public static final int GALLERY_REQUEST=100;
    public static String CHANEL_PARCABLE = "chanel";
    Chanel chanel ;
    TextView tv_gallery , tv_send  , tv_time;
    EditText et_message ;
    File videoFile = null , compressed_thumb =null;
    ImageView iv_pic ;
    int type = 3;
    String compressed_path_video , compressed_path_thumb ,time;
    FrameLayout frameLayout ;
    Admin admin ;
    AdminInfo adminInfo ;
    CoordinatorLayout coordinatorLayout ;
    TextView tv_percent ;
    ImageCompression imageCompression ;
    ProgressBar progressBar;
    //vase inke
    boolean is_compressing = false ;

      boolean okeye = true ;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment getInstance (Chanel chanel) {
        VideoFragment videoFragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHANEL_PARCABLE,chanel);
        videoFragment.setArguments(bundle);
        return  videoFragment;
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
        AndroidNetworking.initialize(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
         tv_time=(TextView)view.findViewById(R.id.tv_time);
        tv_gallery=(TextView)view.findViewById(R.id.tv_videoFragment_gallery);
        tv_send=(TextView)view.findViewById(R.id.tv_videoFragment_send);
        et_message=(EditText)view.findViewById(R.id.et_VideoFragment);
        frameLayout=(FrameLayout) view.findViewById(R.id.framelayout_fragment_video);
        iv_pic=(ImageView)view.findViewById(R.id.iv_picVideo);
        progressBar=(ProgressBar)view.findViewById(R.id.progress_video);
        tv_percent=(TextView)view.findViewById(R.id.tv_VideoFragment_percent);
        coordinatorLayout=(CoordinatorLayout)view.findViewById(R.id.coordinate_video_fragment);
        return  view;
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
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent , GALLERY_REQUEST);
            }

        }else if (view==tv_send) {
            String message = et_message.getText().toString();
            if(is_compressing) {

                MySnack.showSnack(coordinatorLayout,"لطفا تا فرشده سازی  کامل ویدیو صبر نمایید");
            }
               else if (videoFile==null) {
                MySnack.showSnack(coordinatorLayout,"لطفا ابتدا یک ویدیو انتخاب نمایید");
            }

            else if (!InternetCheck.isOnline(getActivity())) {
                MySnack.showSnack(coordinatorLayout,"عدم دسترسی به اینترنت .. لطفا وضعیت اینترنت خود را برسی نمایید");
            }else {
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type",type);
                    jsonObject.put("admin_id",admin.getAdmin_id());
                    jsonObject.put("message",message.length()==0 ? null : message);
                    jsonObject.put("time",time);
                } catch (JSONException e) {e.printStackTrace();

                }
                RequestBody req_content = RequestBody.create(MediaType.parse("text/plain"),jsonObject.toString());
                ProgressRequestBody video_body = new ProgressRequestBody(videoFile,this,"video/*");
                MultipartBody.Part video_part = MultipartBody.Part.createFormData("file",videoFile.getName(),video_body);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"),compressed_thumb);
                MultipartBody.Part thumb_part = MultipartBody.Part.createFormData("thumb",compressed_thumb.getName(),requestBody);

                ApiInterface api = Client.getClient().create(ApiInterface.class);
                Call<SimpleResponse> call = api.makeVideoMessage(admin.getApikey(),Integer.valueOf(chanel.getChanel_id()) ,req_content,
                        video_part,thumb_part
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
                                MySnack.showSnack(coordinatorLayout,message);


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
            if (ActivityCompat.checkSelfPermission(getActivity(),permissions[0])== PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent , GALLERY_REQUEST);
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
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent , GALLERY_REQUEST);


            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       if (videoFile!=null && compressed_thumb!=null) {
           videoFile.delete();
           compressed_thumb.delete();
       }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null)  {
                  okeye = true;
            Uri selectedVideo = data.getData();
          //  final String    realPath=getPath(selectedVideo);
            final String    realPath= RealPathUtil.getPath(getActivity(),selectedVideo);
           File file = new File(realPath);
            Log.e("TestVideoFile",file.getAbsolutePath());

            if (file.length()/(1024*1024) <= 20) {
                videoFile=file;
                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(getActivity(), Uri.fromFile(videoFile));
                    time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInMillisec = Long.parseLong(time );

                    time = String.format("%02d:%02d ",
                            TimeUnit.MILLISECONDS.toMinutes(timeInMillisec),
                            TimeUnit.MILLISECONDS.toSeconds(timeInMillisec) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillisec))
                    );

                    tv_time.setText(time);
                    tv_time.setVisibility(View.VISIBLE);
                }catch (Exception e) {
                    Toast.makeText(getActivity(), "ویدیو نا معتبر میباشد", Toast.LENGTH_SHORT).show();
                    videoFile=null;
                    okeye=false;
                }




            }else {
                new VideoCompressor(realPath).execute();
            }


        if (okeye) {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(realPath,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            bitmapToFile(thumb);
            iv_pic.setImageBitmap(thumb);
            frameLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),android.R.color.black));
        }

        }
    }

    private  void bitmapToFile(Bitmap bitmap) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/noor/Files/Compressed/videos/thumb"
        );
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }
        String currnet = String.valueOf(System.currentTimeMillis()) ;
        String mImageName="IMG"+ currnet.substring(currnet.length()-5) ;

        File imageFile = new File(mediaStorageDir, mImageName + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();


        } catch (Exception e) {

        }finally {
            compressed_path_thumb = imageCompression.compressImage(imageFile.getAbsolutePath());
         compressed_thumb=new File(compressed_path_thumb);

        }
    }

    public String getVideoName(String videoPath) {
        String extenstion =      videoPath.substring(videoPath.lastIndexOf("."));
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/noor/Files/Compressed/videos"
        );

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }

    //    return mediaStorageDir.getAbsolutePath();
        String currnet = String.valueOf(System.currentTimeMillis()) ;
        String mImageName="VID"+ currnet.substring(currnet.length()-5)  + extenstion;
        compressed_path_video  = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);;
        return compressed_path_video;

    }


//    public String getPath(Uri uri) {
//        String[] projection = { MediaStore.Video.Media.DATA };
//
//        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
//        if (cursor != null) {
//            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
//            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
//            int column_index = cursor
//                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//
//        } else
//            return null;
//    }



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

    class VideoCompressor extends AsyncTask<Void, Void, Boolean> {
        String filepath ;
        public VideoCompressor(String filepath ) {
            this.filepath=filepath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            is_compressing=true;
            tv_time.setVisibility(View.GONE);
            tv_percent.setText("در حال فشرده سازی");
            progressBar.setVisibility(View.VISIBLE);
            Log.d("hamid","Start video compression");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(filepath,getVideoName(filepath));
        }

        @Override
        protected void onPostExecute(Boolean compressed) {

            super.onPostExecute(compressed);

            progressBar.setVisibility(View.GONE);
            if(compressed){
                okeye=true;
                tv_percent.setText("0%");
                Toast.makeText(getActivity(), "فشرده سازی انجام شد", Toast.LENGTH_SHORT).show();
                //vase inke check konim bebinim aya qable upload compressesh kamel shode ya na
                is_compressing=false;
                videoFile=new File(compressed_path_video);
                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(getActivity(), Uri.fromFile(videoFile));
                    time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInMillisec = Long.parseLong(time );

                    time = String.format("%02d:%02d ",
                            TimeUnit.MILLISECONDS.toMinutes(timeInMillisec),
                            TimeUnit.MILLISECONDS.toSeconds(timeInMillisec) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillisec))
                    );
                    tv_time.setText(time);
                    tv_time.setVisibility(View.VISIBLE);

                }catch (Exception e) {
                    Toast.makeText(getActivity(), "ویدیو نا معتبر میباشد", Toast.LENGTH_SHORT).show();
                    videoFile=null;
                    okeye=false;
                }

            }
        }
    }

}
