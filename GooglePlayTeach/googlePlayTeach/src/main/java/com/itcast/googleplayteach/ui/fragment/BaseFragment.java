package com.itcast.googleplayteach.ui.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itcast.googleplayteach.ui.widget.LoadingPage;
import com.itcast.googleplayteach.ui.widget.LoadingPage.ResultState;
import com.itcast.googleplayteach.utils.UIUtils;

public abstract class BaseFragment extends Fragment {

	private LoadingPage mLoadingPage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLoadingPage = new LoadingPage(UIUtils.getContext()) {

			@Override
			public View onCreateSuccessView() {
				return BaseFragment.this.onCreateSuccessView();
			}

			@Override
			public ResultState onLoad() {
				return BaseFragment.this.onLoad();
			}

		};
		return mLoadingPage;
	}

	// 由子类实现创建布局的方法
	public abstract View onCreateSuccessView();

	// 由子类实现加载网络数据的逻辑, 该方法运行在子线程
	public abstract ResultState onLoad();

	// 开始加载网络数据
	public void loadData() {
		if (mLoadingPage != null) {
			mLoadingPage.loadData();
		}
	}

	/**
	 * 校验数据的合法性,返回相应的状态
	 * @param data
	 * @return
	 */
	public ResultState check(Object data) {
		if (data != null) {
			if (data instanceof List) {//判断当前对象是否是一个集合
				List list = (List) data;
				if (!list.isEmpty()) {//数据不为空,访问成功
					return ResultState.STATE_SUCCESS;
				} else {//空集合
					return ResultState.STATE_EMPTY;
				}
			}
		}

		return ResultState.STATE_ERROR;
	}

}
