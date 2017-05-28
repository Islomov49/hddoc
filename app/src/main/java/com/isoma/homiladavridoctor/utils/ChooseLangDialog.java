package com.isoma.homiladavridoctor.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.isoma.homiladavridoctor.R;

/**
 * Created by Пользователь on 26.05.2017.
 */

public class ChooseLangDialog extends Dialog {
    View dialogView;
    LayoutInflater inflater;
    private TextView tvTitle, tvCancel;
    ListView listView;
    Context context;

    public ChooseLangDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        dialogView = getLayoutInflater().inflate(R.layout.lang_dialog, null);
        setContentView(dialogView);
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        listView = (ListView) dialogView.findViewById(R.id.lvLang);
        tvTitle = (TextView) dialogView.findViewById(R.id.tvDialogTitle);

    }

    public ChooseLangDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected ChooseLangDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setAdapter(String[] list) {
        ListAdapter listAdapter = new ListAdapter(context, list);
        listView.setAdapter(listAdapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        listView.setOnItemClickListener(onItemClickListener);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public class ListAdapter extends BaseAdapter {

        String[] list;

        public ListAdapter(Context context, String[] list) {
            this.list = list;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public Object getItem(int i) {
            return list;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View listview = inflater.inflate(R.layout.dialog_list_item, viewGroup, false);
            ((TextView) listview.findViewById(R.id.text1)).setText(list[i]);
            return listview;
        }
    }
}
