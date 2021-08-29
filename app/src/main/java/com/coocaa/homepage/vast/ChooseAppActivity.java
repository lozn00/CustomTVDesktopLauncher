package com.coocaa.homepage.vast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;


import com.tianci.movieplatform.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseAppActivity extends Activity {

    private static final String TAG = "ChooseApp";
    private ListView listview;
    MySimpleAdapter myAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_app);
        listview = ((ListView) findViewById(R.id.listview));
        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        //用三个数组装载数据

        List<ApplicationInfo> packageinfo = this.getPackageManager().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);// GET_UNINSTALLED_PACKAGES代表已删除，但还有安装目录的


        for (ApplicationInfo info : packageinfo) {
            Map<String, Object> showitem = new HashMap<String, Object>();

            int labelRes = info.labelRes;

            String appName= this.getPackageManager().getApplicationLabel(info).toString();  //
            String packageName = info.packageName;
            Drawable drawable = info.loadIcon(getPackageManager());
             showitem.put("app_name", appName+"");

            showitem.put("position", listitem.size());
            showitem.put("image", drawable);

            showitem.put("packagename", packageName);
            listitem.add(showitem);
        }

             myAdapter = new MySimpleAdapter(getApplicationContext(), listitem,
                    R.layout.list_item, new String[]{"position", "app_name", "packagename"},
                    new int[]{R.id.iv, R.id.app_name, R.id.packagename});
        listview.setAdapter(myAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap item = (HashMap) myAdapter.getItem(position);
                String str= (String) item.get("packagename");
                Log.w(TAG,"CHOOSE PACKAGENAME"+str+","+item);
                Intent i = new Intent();
                i.putExtra("packagename", str);
                setResult(RESULT_OK,i);
                finish();

            }
        });


    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        finish();

    }
}
