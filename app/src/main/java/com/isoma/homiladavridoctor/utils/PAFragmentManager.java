package com.isoma.homiladavridoctor.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.fragments.BelgilarFragment;
import com.isoma.homiladavridoctor.fragments.GalleryFragment;
import com.isoma.homiladavridoctor.fragments.InfoHaftaFragment;
import com.isoma.homiladavridoctor.fragments.OvqotlanishFragment;
import com.isoma.homiladavridoctor.fragments.QuestionsViewPagerFragment;
import com.isoma.homiladavridoctor.fragments.SavolJavobListFragment;
import com.isoma.homiladavridoctor.fragments.SettingsFragment;
import com.isoma.homiladavridoctor.fragments.TestlarCarcasFragment;

/**
 * Created by developer on 19.03.2017.
 */

public class PAFragmentManager {
    private HomilaDavri  activity;
    private FragmentManager fragmentManager;
    public PAFragmentManager(HomilaDavri activity){
        this.activity = activity;
        fragmentManager = activity.getSupportFragmentManager();
    }
    public void notifyInfosVisibility(boolean visibility) {
        if (fragmentManager == null || fragmentManager.getFragments() == null) return;
        int size = fragmentManager.getFragments().size();
        if(size==0){
            if(visibility){
                activity.goneview();
            }
            else {
                activity.visbview();
            }
        }
    }
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void clearAllFragments(){
        int count = fragmentManager.getBackStackEntryCount();
        while (count > 0) {
            fragmentManager.popBackStack();
            count--;
        }
    }
    public void displayMainWindow() {

       clearAllFragments();
        notifyInfosVisibility(false);
        activity.getSupportActionBar().setTitle(R.string.app_name_main);
        activity.setNavigationButton();
        activity.menuchangeback();

    }
    public void displayFragment(Fragment fragment) {
        if (fragmentManager.findFragmentById(R.id.frame) != null && fragment.getClass().getName().equals(fragmentManager.findFragmentById(R.id.frame).getClass().getName()))
            return;

        notifyInfosVisibility(true);
        fragmentManager
                .beginTransaction()
                .add(R.id.frame, fragment, fragment.getClass().getName()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }
    public void remoteBackPress() {
        fragmentManager.popBackStack();
        if(fragmentManager.getFragments().size()!=0){
            Fragment fragment = fragmentManager.findFragmentById(R.id.frame);
            if(fragment instanceof TestlarCarcasFragment ||
                    fragment instanceof SavolJavobListFragment ||
                    fragment instanceof GalleryFragment ||
                    fragment instanceof BelgilarFragment ||
                    fragment instanceof OvqotlanishFragment ||
                    fragment instanceof SettingsFragment ||
                    fragment instanceof InfoHaftaFragment ||
                    fragment instanceof QuestionsViewPagerFragment){
                displayMainWindow();

            }
        }
    }
}
