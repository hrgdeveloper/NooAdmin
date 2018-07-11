package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.comment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.ApiInterface;
import com.developer.hrg.nooadmin.Helper.Client;
import com.developer.hrg.nooadmin.MainActivity.MainActivity;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.Models.Comment;
import com.developer.hrg.nooadmin.Models.SimpleResponse;
import com.developer.hrg.nooadmin.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment implements Comment_adapter.ClickListener {
    Admin admin ;
    AdminInfo adminInfo ;
    public static final String CHANEL_ID = "chanel_id";
    public static final String CHANEL_NAME = "chanel_name";

    int chanel_id  ;
    String chanel_name ;
    RecyclerView recyclerView;
    ArrayList<Comment> comments=new ArrayList<>();
    Comment_adapter adapter_comment ;

    public CommentFragment() {
        // Required empty public constructor
    }

    public static CommentFragment getInstance(int chanel_id ,String chanel_name)  {
        CommentFragment commentFragment =new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CHANEL_ID,chanel_id);
        bundle.putString(CHANEL_NAME,chanel_name);
        commentFragment.setArguments(bundle);
        return commentFragment;


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            chanel_id=getArguments().getInt(CHANEL_ID);
            chanel_name=getArguments().getString(CHANEL_NAME);

        }
        adminInfo=new AdminInfo(getActivity());
        admin=adminInfo.getAdmin();
        adapter_comment=new Comment_adapter(getActivity(),comments);
        adapter_comment.setClickListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_comment_, container, false);
       recyclerView=(RecyclerView)view.findViewById(R.id.rv_comment);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter_comment);

        ApiInterface api = Client.getClient().create(ApiInterface.class);
        Call<SimpleResponse> call_comments = api.getAllComments(admin.getApikey(),chanel_id);
        call_comments.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (!response.isSuccessful()) {

                }else  {
                    if (response.body().getComments()==null) {
                        Toast.makeText(getActivity(), "بدون نظر ثبت شده", Toast.LENGTH_SHORT).show();

                    }else {

                        comments.addAll(response.body().getComments());
                        adapter_comment.notifyDataSetChanged();

                    }




                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.e("comments",t.getMessage());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setToolbarText( "نظرات کانال " + chanel_name);
    }

    @Override
    public void active_clicked(final int position, View view) {
        final int state = comments.get(position).getVisible()==0 ? 1  : 0 ;

        ApiInterface api = Client.getClient().create(ApiInterface.class);
        Call<SimpleResponse> call_comments = api.setCommentState(admin.getApikey(),comments.get(position).getComment_id(),state);
        call_comments.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (!response.isSuccessful()) {

                }else  {
                    if (response.body().isError()) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        Comment comment = comments.get(position);
                        comment.setVisible(state);
                        comments.set(position,comment);
                        adapter_comment.notifyDataSetChanged();

                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }



                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.e("comments",t.getMessage());
            }
        });

    }
}
