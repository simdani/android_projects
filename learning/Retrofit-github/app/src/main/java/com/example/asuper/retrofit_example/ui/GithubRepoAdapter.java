package com.example.asuper.retrofit_example.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.asuper.retrofit_example.R;
import com.example.asuper.retrofit_example.api.model.GithubRepo;

import org.w3c.dom.Text;

import java.util.List;

public class GithubRepoAdapter extends ArrayAdapter<GithubRepo> {
    private Context context;
    private List<GithubRepo> values;

    public GithubRepoAdapter(Context context, List<GithubRepo> values) {
        super(context, R.layout.list_item_repo, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_repo, parent, false);
        }
        TextView textView = (TextView) row.findViewById(R.id.list_item_pagination_text);

        GithubRepo item = values.get(position);
        String message = item.getName();
        textView.setText(message);

        return row;
    }
}
