package com.example.ramsey.myCloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTempAction extends AppCompatActivity {
    private Spinner locationspinner;
    private EditText createtempactionaction;
    private static final String TAG = "CreateTempAction";
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_temp_action);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_temp_action);
        toolbar.setTitle("新建临时措施");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);//使toolbar支持ActionBar的特性
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//导航抽屉
        getSupportActionBar().setHomeButtonEnabled(true);//返回键可用

        frameLayout=(FrameLayout)findViewById(R.id.FrameLayout_temp_action_create);
        locationspinner=(Spinner)findViewById(R.id.temp_action_create_location_spinner);

        List<String> location=new ArrayList<>();
        location.add("车身");
        location.add("车头");
        location.add("车尾");
        //add hint as last item
        location.add("请填写");
        simpleArrayAdapter adapter=new simpleArrayAdapter(this,android.R.layout.simple_spinner_item,location);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationspinner.setAdapter(adapter);
        locationspinner.setSelection(location.size()-1,true);

        createtempactionaction=(EditText)findViewById(R.id.temp_action_create_temp_action_1);

        Button createtempactionbutton=(Button)findViewById(R.id.temp_action_create_button_1);

        createtempactionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validate())
                {
                    createtempaction();
                }
            }
        });
    }
    private boolean Validate()
    {
        if(createtempactionaction.getText().toString().trim().isEmpty()) {
            Snackbar.make(frameLayout, "请填写临时措施描述", Snackbar.LENGTH_SHORT).setAction("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }).show();
            return false;
        }
        else
        {
            if(locationspinner.getSelectedItem().toString().trim()=="请填写")
            {
                Snackbar.make(frameLayout, "请选择工段", Snackbar.LENGTH_SHORT).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    public void createtempaction() {
        final ProgressDialog pDiaglog=new ProgressDialog(this);
        pDiaglog.setMessage("正在上传");
        pDiaglog.show();
        String tag_createtempaction_request = "create_temp_action";
        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, AppConfig.URL_ReportTempAction,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Create Temp Action Response: " + response.toString());
                        pDiaglog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (error)  {
                                String errorMsg = obj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(CreateTempAction.this,"创建成功",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }catch(JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDiaglog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                Intent intent1=getIntent();
                String cause_uid=intent1.getStringExtra("prob_uid");
                params.put("prob_uid", cause_uid);
                params.put("tempsolution",createtempactionaction.getText().toString().trim());
                params.put("section",locationspinner.getSelectedItem().toString().trim());
                return params;
            }
        },tag_createtempaction_request);
    }
    public static void CreateTempActionActivityStart(Context context, String prob_uid)
    {
        Intent intent=new Intent(context,CreateTempAction.class);
        intent.putExtra("prob_uid",prob_uid);
        context.startActivity(intent);
    }
}
