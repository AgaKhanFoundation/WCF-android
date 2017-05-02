package com.android.wcf.fragments.parent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.wcf.R;
import com.android.wcf.fragments.child.LeaderboardFragment;
import com.android.wcf.fragments.child.MyProfileFragment;
import com.android.wcf.fragments.child.MyTeamFragment;
import com.android.wcf.utils.AppUtil;

/**
 * Created by Malik Khoja on 4/27/2017.
 */

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
