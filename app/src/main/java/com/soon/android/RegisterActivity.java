package com.soon.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.android.bmobBean.User;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.register_toolbar)
    Toolbar registerToolbar;

    @BindView(R.id.new_account)
    TextView newAccount;

    @BindView(R.id.new_password)
    TextView newPassword;

    @BindView(R.id.send_code)
    TextView sendCode;

    @BindView(R.id.verification_code)
    TextView verificationCode;

    @BindView(R.id.add_userinfo)
    Button registerButton;

    @BindView(R.id.agree_clause)
    CheckBox agreeClause;

    private boolean isSendCode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        setSupportActionBar(registerToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // 初始化界面是判断是否曾发送了验证码、验证码是否失效
        SharedPreferences preferences = getSharedPreferences("sendCodeTime", MODE_PRIVATE);
        Long endTime = preferences.getLong("endTime",0);
        if(endTime != 0){
            isSendCode = true;
            Date dt= new Date();
            Long startTime= dt.getTime();
            new CountDownTimer(endTime - startTime, 1000){
                @Override
                public void onTick(long l) {
                    sendCode.setText("重新发送(" + l / 1000 + ")");
                }

                @Override
                public void onFinish() {
                    isSendCode = false;
                    SharedPreferences.Editor editor = getSharedPreferences("sendCodeTime", MODE_PRIVATE).edit();
                    editor.putLong("endTime",0);
                    editor.apply();
                    sendCode.setText("重新发送");
                }
            }.start();
        }
        // 设置发送验证码点击事件
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSendCode){
                    isSendCode = true;
                    Date dt= new Date();
                    Long startTime= dt.getTime();
                    SharedPreferences.Editor editor = getSharedPreferences("sendCodeTime", MODE_PRIVATE).edit();
                    editor.putLong("endTime",startTime + 600000);
                    editor.apply();
                    BmobSMS.requestSMSCode(newAccount.getText().toString(), "Soon验证码", new QueryListener<Integer>(){

                        @Override
                        public void done(Integer integer, BmobException e) {
                            if(e == null){
                                Toast.makeText(RegisterActivity.this, "验证码发送成功！短信id:" + integer,Toast.LENGTH_SHORT).show();
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
                    new CountDownTimer(600000, 1000){
                        @Override
                        public void onTick(long l) {
                            sendCode.setText("重新发送(" + l / 1000 + ")");
                        }

                        @Override
                        public void onFinish() {
                            isSendCode = false;
                            SharedPreferences.Editor editor = getSharedPreferences("sendCodeTime", MODE_PRIVATE).edit();
                            editor.putLong("endTime",0);
                            editor.apply();
                            sendCode.setText("重新发送");
                        }
                    }.start();
                }
            }
        });

        // 注册点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 验证码如果正确则开始注册
                BmobSMS.verifySmsCode(newAccount.getText().toString(), verificationCode.getText().toString(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            String account = newAccount.getText().toString();
                            String password = newPassword.getText().toString();

                            // 调用Bmob注册方法
                            User user = new User();
                            user.setUsername(account);
                            user.setMobilePhoneNumber(account);
                            user.setPassword(password);
                            user.setVIP(false);
                            user.signUp(new SaveListener<User>() {

                                @Override
                                public void done(User o, BmobException e) {
                                    if(e == null){
                                        Toast.makeText(RegisterActivity.this, "注册成功！",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else{
                                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                    }
                                }
                            });
                        }else{
                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });

            }
        });

        // 其他控件初始化
        String agreeClauseText = "我已阅读并同意<font color='#F83F3C'>使用条款和隐私政策</font>";
        agreeClause.setText(Html.fromHtml(agreeClauseText));
        agreeClause.setChecked(false);
        agreeClause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    registerButton.setEnabled(true);
                    registerButton.setBackgroundResource(R.drawable.logout_button);
                }else{
                    registerButton.setEnabled(false);
                    registerButton.setBackgroundResource(R.drawable.unclickable);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    // 返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 活动跳转
    public static void actionStart(Context context){
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
