package com.itheima.qq15.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hyphenate.chat.EMClient;
import com.itheima.qq15.MainActivity;
import com.itheima.qq15.R;
import com.itheima.qq15.presenter.IPluginPresenter;
import com.itheima.qq15.presenter.impl.PluginPresenter;
import com.itheima.qq15.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class PluginFragment extends BaseFragment implements View.OnClickListener,PluginView {

    private Button mBtnLogout;
    private IPluginPresenter mPluginPresenter;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plugin, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnLogout = (Button) view.findViewById(R.id.btn_logout);
        String currentUser = EMClient.getInstance().getCurrentUser();
        mBtnLogout.setText("退（"+currentUser+"）出");
        mBtnLogout.setOnClickListener(this);
        //对象的手动注入
        mPluginPresenter = new PluginPresenter(this);
    }

    @Override
    public void onClick(View v) {
        //退出登录
        mProgressDialog.setMessage("正在退出...");
        mProgressDialog.show();
        mPluginPresenter.logout();
    }

    @Override
    public void onLogout(String username, boolean success, String msg) {
        mProgressDialog.hide();
        if (success){
            MainActivity activity = (MainActivity)getActivity();
            activity.startActivity(LoginActivity.class,true);
        }else{
            ToastUtils.showToast(getContext(),msg);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mProgressDialog.dismiss();
    }
}
