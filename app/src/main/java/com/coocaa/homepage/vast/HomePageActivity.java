package com.coocaa.homepage.vast;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tianci.movieplatform.R;

public class HomePageActivity extends AppCompatActivity {

    private static final String TAG = "HomePageActivity";
    private ImageView iv;
    private TextView tvDefaultName;
    private SharedPreferences sharedPreferences;
    private Handler handler;

    /* access modifiers changed from: protected */
    @Override
    // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasCancel=false;
        setContentView(R.layout.activity_main);
        iv = ((ImageView) findViewById(R.id.iv_app));
        tvDefaultName = ((TextView) findViewById(R.id.tv_default_app));
        sharedPreferences = getSharedPreferences("config", Context.MODE_WORLD_READABLE);
        String packagename = sharedPreferences.getString("packagename", "");
        Log.w(TAG,"PACKAGENAME:"+packagename);
        handler=new Handler();
        updateUi(packagename);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doDefaultLaunch(packagename);
            }
        },5000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HomePageActivity.this, "请稍候,即将启动默认应用,按返回键或方向键取消自动启动,按菜单键进入电视", Toast.LENGTH_SHORT).show();
            }
        },500);

    }

    private void doDefaultLaunch(String packagename) {
        if(TextUtils.isEmpty(packagename)){

            launchDesktop();
        }  else if(packagename.equals(this.getPackageName())){
            Toast.makeText(this, "无法启动自身!", Toast.LENGTH_SHORT).show();
        } else if(packagename.equals("android")){
            Toast.makeText(this, "忽略启动", Toast.LENGTH_SHORT).show();
        }else{
            PackageManager packageManager = getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packagename);
            launchByIntent(intent, ""+packagename);
        }
    }
    boolean hasCancel=false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.w(TAG,"KEYCODE_:"+keyCode);
        switch (keyCode){

            case KeyEvent.KEYCODE_ENTER:     //确定键enter
            case KeyEvent.KEYCODE_DPAD_CENTER:
                handler.removeCallbacksAndMessages(null);
                Log.d(TAG,"enter--->");
                break;
            case KeyEvent.KEYCODE_CHANNEL_DOWN:
                launchTv();
               return true;
            case KeyEvent.KEYCODE_MENU:
                launchTv();
                break;
            case KeyEvent.KEYCODE_CHANNEL_UP:
                launchMacket();
                break;
            case KeyEvent.KEYCODE_BACK:    //返回键
                Log.d(TAG,"back--->");
                if(!hasCancel){
                    cancelAutolAUNCH();
                    return true;
                }else{

                }



            case KeyEvent.KEYCODE_SETTINGS: //设置键
                Log.d(TAG,"setting--->");
              break;

            case KeyEvent.KEYCODE_DPAD_DOWN:   //向下键

                /*    实际开发中有时候会触发两次，所以要判断一下按下时触发 ，松开按键时不触发
                 *    exp:KeyEvent.ACTION_UP
                 */
                if (event.getAction() == KeyEvent.ACTION_DOWN){

                    Log.d(TAG,"down--->");
                }
                if(!hasCancel){
                    cancelAutolAUNCH();
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_UP:   //向上键
                Log.d(TAG,"up--->");
                if(!hasCancel){
                    cancelAutolAUNCH();
                    return true;
                }
                break;


            case KeyEvent.KEYCODE_DPAD_LEFT: //向左键
                if(!hasCancel){
                    cancelAutolAUNCH();
                    return true;
                }
                Log.d(TAG,"left--->");

                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:  //向右键
                Log.d(TAG,"right--->");
                if(!hasCancel){
                    cancelAutolAUNCH();
                    return true;
                }
                break;


        }

        return super.onKeyDown(keyCode, event);

    }

    private void cancelAutolAUNCH() {
        handler.removeCallbacksAndMessages(null);
        Toast.makeText(this, "已取消默认启动,按菜单键进入电视..", Toast.LENGTH_SHORT).show();
        hasCancel=true;
    }

    private void launchDesktop() {
        Log.w(TAG,"Launch desktop");
        Intent paramIntent = new Intent("android.intent.action.MAIN");
        paramIntent.setComponent(new ComponentName("android", "com.android.internal.app.ResolverActivity"));
        paramIntent.addCategory("android.intent.category.DEFAULT");
        paramIntent.addCategory("android.intent.category.HOME");
        startActivity(paramIntent);
    }

    public void open(View view) {
        handler.removeCallbacksAndMessages(null);
        switch (view.getId()) {
            case R.id.btn_desktop:
                launchDesktop();
                break;
            case R.id.btn_market:
                launchMacket();
                break;
            case R.id.btn_tv:
                launchTv();
                break;
            case R.id.btn_choose_app:
                Intent intent=new Intent(this,ChooseAppActivity.class);
                startActivityForResult(intent,1);
                break;
        }


    }

    private void launchTv() {
//        com.dianshijia.newlive
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage("com.dianshijia.newlive");
        launchByIntent(intent, "电视家");


    }

    private void launchMacket() {

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.ant.store.appstore");
        launchByIntent(intent,"蚂蚁市场");
    }

    private void launchByIntent(Intent intent, String packageName) {
        try {
            startActivity(intent);
        } catch (Throwable e) {
            Toast.makeText(this, "启动"+packageName+"失败，请确保应用是否已安装!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            String packagename = data.getStringExtra("packagename");
          sharedPreferences.edit().putString("packagename", packagename).apply();
            updateUi(packagename);
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();

        }else{
            new AlertDialog.Builder(this).setMessage("没有选择应用,是否设置默认启动桌面应用?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(HomePageActivity.this, "更新成功!", Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putString("packagename", "").apply();
                    updateUi("");

                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

        }
    }

    private void updateUi(String packagename) {
        if(TextUtils.isEmpty(packagename)){
            iv.setImageResource(R.mipmap.ic_launcher);
            tvDefaultName.setText("主页");
            return;
        } else if(packagename.equals(this.getPackageName())){
            iv.setImageResource(R.mipmap.ic_launcher);
            tvDefaultName.setText("不自动启动");
            return;
        }
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(packagename, PackageManager.GET_UNINSTALLED_PACKAGES);
            ApplicationInfo applicationInfo =packageInfo.applicationInfo;
                    String appName= this.getPackageManager().getApplicationLabel(applicationInfo).toString();  //
            tvDefaultName.setText(appName+"/"+applicationInfo.packageName+"/V"+packageInfo.versionName+" build "+packageInfo.versionCode);
            Drawable drawable = applicationInfo.loadIcon(getPackageManager());
            iv.setImageDrawable(drawable);



        } catch (Throwable e) {
            Toast.makeText(this, "获取失败!"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}