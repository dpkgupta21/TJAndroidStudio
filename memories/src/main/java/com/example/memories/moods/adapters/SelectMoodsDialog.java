package com.example.memories.moods.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.example.memories.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectMoodsDialog extends DialogFragment {

    private static final String TAG = "SELECT_MOODS_DIALOG";
    OnEmoticonSelectListener mListener;
    private GridView mGridView;
    private MoodsGridAdapter mAdapter;
    private List<Map<String, String>> emoticons;
    private String[] emoticonNames = new String[]{
            "angry",
            "after boom",
            "amazed",
            "angry",
            "bad smelly",
            "baffle",
            "beated",
            "beauty",
            "big smile",
            "boss",
            "confident",
            "confused",
            "cry",
            "doubt",
            "embarassed",
            "hell boy",
            "hungry",
            "rap",
            "sad",
            "shame",
            "smile",
            "spiderman",
            "waaahht!",

    };
    private int[] emoticonIds = new int[]{R.drawable.angry, R.drawable.after_boom,
            R.drawable.amazed, R.drawable.angry, R.drawable.bad_smelly, R.drawable.baffle,
            R.drawable.beated, R.drawable.beauty, R.drawable.big_smile,
            R.drawable.boss, R.drawable.confident, R.drawable.confuse,
            R.drawable.cry, R.drawable.doubt, R.drawable.embarrassed, R.drawable.hell_boy, R.drawable.hungry,
            R.drawable.rap, R.drawable.sad,R.drawable.shame, R.drawable.smile, R.drawable.spiderman,
            R.drawable.waaaht,};

    private void initialize() {
        emoticons = new ArrayList<Map<String, String>>();
        int i = 0;
        Map<String, String> map;
        for (String name : emoticonNames) {
            map = new HashMap<String, String>();
            map.put("name", name);
            map.put("id", Integer.valueOf(emoticonIds[i]).toString());
            emoticons.add(map);
            i++;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        initialize();

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d(TAG, "inflater value is " + inflater);
        View aboutUsDialogView = inflater.inflate(R.layout.mood_grid, null);
        mAdapter = new MoodsGridAdapter(getActivity(), emoticons);
        mGridView = (GridView) aboutUsDialogView.findViewById(R.id.moodsGrid);
        Log.d(TAG, "inflater value is " + inflater + " adapter " + mAdapter + " GridView " + mGridView);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onEmoticonSelect(emoticons.get(position).get("name"), Integer.parseInt(emoticons.get(position).get("id")));
                SelectMoodsDialog.this.dismiss();
            }
        });

        builder.setView(aboutUsDialogView);
        builder.setTitle(null);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEmoticonSelectListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    public interface OnEmoticonSelectListener {
        public abstract void onEmoticonSelect(String name, int emoticonId);
    }

}


