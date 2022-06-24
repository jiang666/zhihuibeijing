package com.itheima.qq15.utils;

import com.itheima.qq15.view.BaseFragment;
import com.itheima.qq15.view.ContactFragment;
import com.itheima.qq15.view.ConversationFragment;
import com.itheima.qq15.view.PluginFragment;

import java.security.SecureRandom;

/**
 * 作者： itheima
 * 时间：2016-10-17 10:42
 * 网址：http://www.itheima.com
 */

public class FragmentFactory {

    private static ConversationFragment sConversationFragment;
    private static ContactFragment sContactFragment;
    private static PluginFragment sPluginFragment;

    public static BaseFragment getFragment(int position){
        BaseFragment baseFragment = null;
        switch (position) {
            case 0:
                if (sConversationFragment==null){
                    sConversationFragment = new ConversationFragment();
                }
                baseFragment = sConversationFragment;
                break;
            case 1:
                if (sContactFragment==null){
                    sContactFragment = new ContactFragment();
                }
                baseFragment = sContactFragment;
                break;
            case 2:
                if (sPluginFragment==null){
                    sPluginFragment = new PluginFragment();
                }
                baseFragment = sPluginFragment;
                break;
        }
        return baseFragment;

    }

}
