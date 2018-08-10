package com.developer.hrg.nooadmin.Fragments.Fragment_userManage.userManage;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.developer.hrg.nooadmin.Helper.DateConvertor;
import com.developer.hrg.nooadmin.Models.User;
import com.developer.hrg.nooadmin.R;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GetUser_Adapter extends RecyclerView.Adapter<GetUser_Adapter.Holder> {
    private Context context ;
    private ArrayList<User> userList;
    private ArrayList<User> userListCopy =new ArrayList<>();
    MyClickListener myClickListener;
        public GetUser_Adapter(Context context , ArrayList<User> userList) {
            this.context=context;
            this.userList=userList;
        }
 public interface MyClickListener{
     public void iv_active_clicked(int position , View view) ;

 }

 public void setMyClickListener(MyClickListener myClickListener) {
     this.myClickListener=myClickListener;
 }

 public void copyItems(ArrayList<User> copy ) {
     userListCopy.addAll(copy);
 }
 public void filter(String text) {
     userList.clear();
     if (text.isEmpty()) {
         userList.addAll(userListCopy);
     }else  {
         text=text.toLowerCase();
         for (User user : userListCopy) {
             if (user.getUsername().contains(text) || user.getMobile().contains(text)) {
                 userList.add(user);
             }
         }
     }
     notifyDataSetChanged();
 }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.custom_getuser,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(Holder holder, int position) {
        User user = userList.get(position);
        Calendar calendar = DateConvertor.getCalendarFromString(user.getCreated_at());
        String shamsi_date = DateConvertor.shamsiDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
        String curTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));



        holder.tv_count.setText(""+Integer.valueOf(position+1));
        holder.tv_mobile.setText("شماره تماس : "+user.getMobile());
        holder.tv_user_id.setText("شماره کاربری :"+user.getId());
        holder.tv_date.setText(shamsi_date + "  " + curTime );
        if (user.getUsername().equalsIgnoreCase("e")) {
            holder.tv_username.setText("نام کاربری : تکمیل نشده است");
        }else {
            holder.tv_username.setText("نام کاربری : "+ user.getUsername());
        }

        if (user.getActive()==1) {
            holder.iv_active.setImageDrawable(context.getResources().getDrawable(R.drawable.tick));
        }else {
            holder.iv_active.setImageDrawable(context.getResources().getDrawable(R.drawable.close));
        }

        if (user.getStatus()==1) {
            holder.iv_complete.setImageDrawable(context.getResources().getDrawable(R.drawable.tick));
        }else {
            holder.iv_complete.setImageDrawable(context.getResources().getDrawable(R.drawable.close));
        }
    }




    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tv_mobile,tv_user_id , tv_username , tv_date , tv_count ;
        ImageView iv_complete , iv_active ;
        public Holder(View itemView) {
            super(itemView);
            tv_count=(TextView)itemView.findViewById(R.id.tv_user_count);
            tv_mobile=(TextView)itemView.findViewById(R.id.tv_user_number);
            tv_user_id=(TextView)itemView.findViewById(R.id.tv_user_id);
            tv_username=(TextView)itemView.findViewById(R.id.tv_user_username);
            tv_date=(TextView)itemView.findViewById(R.id.tv_user_date);
            iv_complete=(ImageView)itemView.findViewById(R.id.iv_user_compelete);
            iv_active=(ImageView)itemView.findViewById(R.id.iv_user_active);
            iv_active.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myClickListener.iv_active_clicked(getAdapterPosition(),view);
                }
            });
        }
    }


}
