package com.isoma.homiladavridoctor.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by developer on 16.04.2016.
 */
public class LinkerText {
    public static SpannableString addClickablePart(String str, final doSomethink Ai) {
        int t=0;
        SpannableString ss = new SpannableString(str);
        Pattern pattern = Pattern.compile("[@]\\w*");
        final Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            if (t==0){
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        TextView tv = (TextView) textView;
                        // TODO add check if tv.getText() instanceof Spanned
                        Spanned s = (Spanned) tv.getText();
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);
                        Ai.clickedA(s.subSequence(start+1, end)+"");
                        Log.d("Linked", "onClick [" + s.subSequence(start, end) + "]");
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);

                        ds.setColor(Color.parseColor("#01628D"));
                        ds.setUnderlineText(false);
                    }
                };
                ss.setSpan(clickableSpan, matcher.start(), matcher.end(),  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else{
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        TextView tv = (TextView) textView;
                        // TODO add check if tv.getText() instanceof Spanned
                        Spanned s = (Spanned) tv.getText();
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);
                        Ai.clickedA(s.subSequence(start, end)+"");
                        Log.d("Linked", "onClick [" + s.subSequence(start, end) + "]");
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);

                        ds.setColor(Color.parseColor("#01628D"));
                        ds.setUnderlineText(false);
                    }
                };
                ss.setSpan(clickableSpan, matcher.start(), matcher.end(),  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            t++;

        }
        return ss;
    }
    public interface doSomethink{
        public void clickedA(String textView);
    }
}
