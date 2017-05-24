package com.isoma.homiladavridoctor.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;

import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.fragments.AccountGuestFragment;

/**
 * Created by developer on 17.04.2017.
 */

public class LinkerTextView extends android.support.v7.widget.AppCompatTextView {

    public LinkerTextView(Context context) {
        super(context);
    }

    public LinkerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinkerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLinkToAccountGuesta(String link, final Context context){
        setText(LinkerText.addClickablePart( link, new LinkerText.doSomethink() {
            @Override
            public void clickedA(String textView) {
                AccountGuestFragment accountGuestFragment = new AccountGuestFragment();
                Bundle bundle = new Bundle();
                bundle.putString(AccountGuestFragment.USER_NAME,textView);
                accountGuestFragment.setArguments(bundle);
                ((HomilaDavri) context).getPaFragmentManager().displayFragment(accountGuestFragment);
            }
        }));
        setMovementMethod(LinkMovementMethod.getInstance());
        setHighlightColor(Color.TRANSPARENT);
    }

}
