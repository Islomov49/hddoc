package com.isoma.homiladavridoctor.fragments;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.isoma.homiladavridoctor.HomilaDavri.PAGE;


public class QuestionsViewPagerFragment extends Fragment implements  ViewPager.OnPageChangeListener {
    ImageView ivRecivedQuestions,ivMyQuestions,ivHistoryQuestions,ivAccount;
    int color = Color.parseColor("#c7c7c7");
    private ViewPager viewPager;
    ArrayList<Fragment> fragments;
    private PagerAdapter adapter;
    private ImageView ivNewQuestion;
    private ImageView ivNewMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((HomilaDavri)getActivity()).setHomeButton();
        ((HomilaDavri)getActivity()).goneAll();
        View view = inflater.inflate(R.layout.fragment_questions_view_pager, container, false);
        ivRecivedQuestions = (ImageView) view.findViewById(R.id.ivRecivedQuestions);
        ivMyQuestions = (ImageView) view.findViewById(R.id.ivMyQuestions);
        ivHistoryQuestions = (ImageView) view.findViewById(R.id.ivHistoryQuestions);
        ivAccount = (ImageView) view.findViewById(R.id.ivAccount);
        ivNewQuestion = (ImageView) view.findViewById(R.id.ivNewQuestion);
        ivNewMessage = (ImageView) view.findViewById(R.id.ivNewMessage);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        fragments = new ArrayList<>();
        fragments.add(new QuestionsFragment());
        fragments.add(new MyQuestions());
        fragments.add(new SubscribeFragment());
        fragments.add(new AccountFragment());

        toMyQuestions();
        ivRecivedQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0,true);
                toRecivedQuestionsFragment();
            }
        });
        ivMyQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1,true);
                toMyQuestions();
            }
        });
        ivHistoryQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2,true);
                toHistoryQuestions();
            }
        });
        ivAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(3,true);
                toAccount();
            }
        });


        adapter = new PagerAdapter(((HomilaDavri)getActivity()).getPaFragmentManager().getFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        toRecivedQuestionsFragment();
        if (getArguments() != null) {

            if(getArguments().getBoolean("FROM_MAIN",false)){
                viewPager.setCurrentItem(1);
                ((HomilaDavri)getActivity()).getPaFragmentManager().displayFragment(new AddQuestionFragment());
            }else {
                int page = getArguments().getInt(PAGE);
                viewPager.setCurrentItem(page, true);
                switch (page){
                    case 0:
                        toRecivedQuestionsFragment();
                        break;
                    case 1:
                        toMyQuestions();
                        break;
                    case 2:
                        toHistoryQuestions();
                        break;
                    case 3:
                        toAccount();
                        break;

                }
            }
        }
        //TODO hide or show fab according to viewpagers position
        viewPager.addOnPageChangeListener(this);

        return view;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
       switch (position){
           case 0:
               toRecivedQuestionsFragment();
               break;
           case 1:
               toMyQuestions();
               break;
           case 2:
               toHistoryQuestions();
               break;
           case 3:
               toAccount();
               break;
       }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void toRecivedQuestionsFragment(){
        ((HomilaDavri)getActivity()).getSupportActionBar().setTitle(R.string.savollarga_javob);
        ivRecivedQuestions.setColorFilter(null);
        ivMyQuestions.setColorFilter(color);
        ivHistoryQuestions.setColorFilter(color);
        ivAccount.setColorFilter(color);
    }
    private void toMyQuestions(){
        ((HomilaDavri)getActivity()).getSupportActionBar().setTitle(R.string.mening_savollarim);
        ivRecivedQuestions.setColorFilter(color);
        ivMyQuestions.setColorFilter(null);
        ivNewQuestion.setVisibility(View.GONE);
        ivHistoryQuestions.setColorFilter(color);
        ivAccount.setColorFilter(color);
    }
    private void toHistoryQuestions(){
        ((HomilaDavri)getActivity()).getSupportActionBar().setTitle(R.string.qatnashganlarim);
        ivRecivedQuestions.setColorFilter(color);
        ivMyQuestions.setColorFilter(color);
        ivHistoryQuestions.setColorFilter(null);
        ivAccount.setColorFilter(color);
    }
    private void toAccount(){
        ((HomilaDavri)getActivity()).getSupportActionBar().setTitle(R.string.mening_sahifalarim);
        ivRecivedQuestions.setColorFilter(color);
        ivMyQuestions.setColorFilter(color);
        ivHistoryQuestions.setColorFilter(color);
        ivNewMessage.setVisibility(View.GONE);
        ivAccount.setColorFilter(null);
    }
    public class PagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> list;
        public PagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }
        @Override
        public int getCount() {
            return list.size();
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnimationSubscribe(EventMessage eventMessage) {
        if(eventMessage.getForClass().equals("QuestionsViewPagerFragment")){
            if(eventMessage.getStatus().equals("subscribed")) {
                final Runnable endAction = new Runnable() {
                    public void run() {
                        ivHistoryQuestions.animate().setDuration(50).scaleXBy(0.5f).scaleYBy(0.5f).scaleX(1f).scaleY(1f).setInterpolator(new DecelerateInterpolator());
                    }
                };
                ivHistoryQuestions.animate().setDuration(50).scaleXBy(0.5f).scaleYBy(0.5f).scaleX(1.2f).scaleY(1.2f).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        endAction.run();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });

            }
            else if(eventMessage.getStatus().equals("toMyAccount")){
                FragmentManager fragmentManager = ((HomilaDavri) getActivity()).getPaFragmentManager().getFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.frame);
                if(fragment instanceof OpenedMyQuestionFragment || fragment instanceof OpenedQuestionFragment )
                fragmentManager.popBackStack();
                viewPager.setCurrentItem(3,true);
                toAccount();
            }
        }
        if(eventMessage.getForClass().equals("all")){
            if(eventMessage.getStatus().equals("newMessage")) {
                ivNewMessage.setVisibility(View.VISIBLE);
            }
            else if(eventMessage.getStatus().equals("newAnswer")) {
                ivNewQuestion.setVisibility(View.VISIBLE);
            }

        }
    }

}
