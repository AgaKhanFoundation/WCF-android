package com.android.wcf.obsolete.fragments.parent;
/**
 * Copyright © 2017 Aga Khan Foundation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.wcf.R;
import com.android.wcf.obsolete.fragments.child.MyProfileFragment;
import com.android.wcf.obsolete.fragments.child.MyTeamFragment;
import com.android.wcf.home.Leaderboard.LeaderboardFragment;
import com.android.wcf.utils.AppUtil;

public class ParentFragment extends Fragment {

    private View mView;
    private FragmentManager mFragmentManager;
    private static Fragment fragment = null;
    private String fragmentTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // parent fragment
        mView = inflater.inflate(R.layout.fragment_parent, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            fragmentTitle = bundle.getString("FragmentTitle");
        }
        showSubfragments(fragmentTitle);
        return mView;
    }

    public void showSubfragments(String tagName) {
        // this function loads the child fragment
        Fragment fragment = null;
        switch (fragmentTitle) {
            case AppUtil.LEADERBOARD_FRAGMENT:
                fragment = new LeaderboardFragment();
                break;
            case AppUtil.MYTEAM_FRAGMENT:
                fragment = new MyTeamFragment();
                break;
            case AppUtil.MYPROFILE_FRAGMENT:
                fragment = new MyProfileFragment();
                break;
        }
        mFragmentManager = this.getChildFragmentManager();
        FragmentTransaction fragtrans = mFragmentManager.beginTransaction();
        if (fragment != null) {
            fragtrans.replace(R.id.frame_container_child, fragment, tagName);
            fragtrans.addToBackStack(tagName);
            fragtrans.commit();
        }
    }

    @Override
    public void onAttach(Context context) {
        //TODO : Check if this method gets called for >API 23
        fragment = this;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        fragment = null;
        super.onDetach();
    }
}
