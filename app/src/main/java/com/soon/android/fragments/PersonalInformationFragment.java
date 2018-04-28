package com.soon.android.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soon.android.AuthenticationActivity;
import com.soon.android.LoginActivity;
import com.soon.android.PersonalCenterActivity;
import com.soon.android.R;
import com.soon.android.UpdatePwdActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalInformationFragment extends Fragment {

    @BindView(R.id.stroe_name)
    TextView storeName;

    @BindView(R.id.head)
    CircleImageView headFile;

    @BindView(R.id.authentication)
    LinearLayout Authentication;

    public PersonalInformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_personal_information, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences("store", Context.MODE_PRIVATE);
        String name = preferences.getString("name","");
        String imageUrl = Environment.getExternalStorageDirectory() + "/" + preferences.getString("image","");
        String state = preferences.getString("state","");

        if (state.equals("已认证")){
            Authentication.setVisibility(View.GONE);
            storeName.setText(name);
            Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
            headFile.setImageBitmap(bitmap);
        }else{
            headFile.setClickable(false);
        }
    }

    @OnClick({R.id.head,R.id.update_pwd,R.id.authentication, R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.head:
                PersonalCenterActivity.actionStart(getActivity());
                break;
            case R.id.update_pwd:
                UpdatePwdActivity.actionStart(getActivity());
                break;
            case R.id.authentication:
                AuthenticationActivity.actionStart(getActivity());
                break;
            case R.id.logout:
                BmobUser.logOut();   //清除缓存用户对象
                BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了

                SharedPreferences.Editor editor = getActivity().getSharedPreferences("store", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                LoginActivity.actionStart(getActivity());
                getActivity().finish();
                break;
        }
    }

}
