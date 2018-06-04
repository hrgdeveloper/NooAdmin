package com.developer.hrg.nooadmin.MainActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.hrg.nooadmin.Fragments.Fragment_userManage.makeChanel.Fragment_makeChanel;
import com.developer.hrg.nooadmin.Fragments.Fragment_userManage.userManage.Fragment_UserManage;
import com.developer.hrg.nooadmin.Helper.AdminInfo;
import com.developer.hrg.nooadmin.Helper.InternetCheck;
import com.developer.hrg.nooadmin.Models.Admin;
import com.developer.hrg.nooadmin.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
AdminInfo adminInfo ;
    Button btn_getUsers , btn_mNewChanel;
    FragmentManager fragmentManager ;
    Toolbar toolbar ;
    TextView tv_toolbar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adminInfo=new AdminInfo(MainActivity.this);
        findViews();
        btn_getUsers.setOnClickListener(this);
        btn_mNewChanel.setOnClickListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int count = getSupportFragmentManager().getBackStackEntryCount();
                if (count==0) {
                    toolbar.setTitle(null);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    setToolbarText("صفحه اصلی مدیریت");
                    toolbar.setVisibility(View.VISIBLE);

                }else {
                    try {
                        Fragment fragment =getSupportFragmentManager().getFragments().get(count-1);
                        fragment.onResume();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }
    public Admin getAdmin() {
        return  adminInfo.getAdmin();
    }

    @Override
    public void onClick(View view) {
        if(view==btn_getUsers) {
            if (!InternetCheck.isOnline(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            }else {
                openFragment(new Fragment_UserManage(),"userManage", false);
            }

        }else if (view==btn_mNewChanel) {
            openFragment(new Fragment_makeChanel(),"mChanel" , true);
        }

    }

    public void findViews(){
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        tv_toolbar=(TextView)toolbar.findViewById(R.id.tv_toolbar);
        btn_getUsers=(Button)findViewById(R.id.btn_GetUsers);
        btn_mNewChanel=(Button)findViewById(R.id.btn_newChanel);
    }

    @Override
    public void onBackPressed() {
        int count_fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (count_fragments>0) {
            getSupportFragmentManager().popBackStack();

        }else {
            super.onBackPressed();
        }

    }
    public void openFragment(Fragment fragment , String tag , boolean removeToolbar) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.rootLayout,fragment,tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (removeToolbar) {
            toolbar.setVisibility(View.GONE);
        }

    }
    public void setToolbarText(String title) {
        tv_toolbar.setText(title);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
