package com.example.memories.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.memories.R;

import java.util.ArrayList;
import java.util.List;

public class ChooseInterest extends Activity {

    private ListView mListView;
    private List<String> mInterestList;
    private InterestListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_choose_interest);
        mListView = (ListView) findViewById(R.id.interestList);
        mInterestList = new ArrayList<String>();
        mInterestList.add("Cook");
        mInterestList.add("Singer");
        mInterestList.add("Dancing");
        mInterestList.add("Guitarist");
        mInterestList.add("Smoker");
        mInterestList.add("Drinker");
        mInterestList.add("Foodie");
        mInterestList.add("Bookworm");
        mListAdapter = new InterestListAdapter(mInterestList, this);
        mListView.setAdapter(mListAdapter);
    }
}

class InterestListAdapter extends BaseAdapter {

    List<String> mInterestList;
    Context mContext;

    public InterestListAdapter(List<String> interestList, Context context) {
        mInterestList = interestList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mInterestList.size();
    }

    @Override
    public Object getItem(int position) {
        return mInterestList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.profile_choose_interest_list_item, null);
        }
        ((TextView) convertView.findViewById(R.id.interest)).setText(mInterestList.get(position));
        return convertView;
    }

}
