package org.fundacionparaguaya.advisorapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.NavigationListener;

/**
 * A AbstractStackedFrag is a fragment that is nested in a AbstractTabbedFrag. When it needs to navigate, it is able to communicate
 * with the parent fragment.
 */

public abstract class AbstractStackedFrag extends Fragment
{
    private NavigationListener mNavigateCallback;

    boolean mDidEnter = false;
    int lastEnterAnimId = -1;
    int lastBackStackCount =-1;

    /**
     * Gets parent fragment (of type AbstractTabbedFrag) and then calls navigation function. Current
     * fragment gets placed in a stack.
     *
     * @param fragment fragment to navigate to
     */
    public void navigateTo(AbstractStackedFrag fragment)
    {
        mNavigateCallback.onNavigateNext(fragment);
    }

    /**
     * Goes back to the previous fragment
     */
    public void navigateBack()
    {
        mNavigateCallback.onNavigateBack();
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

        boolean shouldNotAnimate = enter && mDidEnter && nextAnim == lastEnterAnimId;

        lastEnterAnimId = nextAnim;

        if(enter)
        {
            mDidEnter = true;
            lastEnterAnimId = nextAnim;
        }

        return shouldNotAnimate ? AnimationUtils.loadAnimation(getActivity(), R.anim.none)
                : super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try
        {
            //if this is a nested fragment
            if(getParentFragment() != null)
            {
                mNavigateCallback = (NavigationListener) getParentFragment();
            }
            else
            {
                //just nested inside of an activity
                mNavigateCallback = (NavigationListener) context;
            }
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Parent activity or fragment must implement NavigationListener");
        }
    }
}
