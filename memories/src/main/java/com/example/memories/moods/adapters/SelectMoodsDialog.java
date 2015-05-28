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
            "angry",
            "angry",
            "angry",
            "angry",
            "angry",
            "angry",
            "angry",
            "angry",
            "angry",
            "angry",
            "angry",
            "angry"
    };
    private int[] emoticonIds = new int[]{R.drawable.angry, R.drawable.after_boom, R.drawable.ah,
            R.drawable.amazed, R.drawable.angry, R.drawable.bad_smelly, R.drawable.baffle,
            R.drawable.beat_shot, R.drawable.beated, R.drawable.beauty, R.drawable.big_smile,
            R.drawable.boss, R.drawable.burn_joss_stick, R.drawable.byebye, R.drawable.canny,
            R.drawable.choler, R.drawable.cold, R.drawable.confident, R.drawable.confuse,
            R.drawable.cool, R.drawable.cry, R.drawable.doubt, R.drawable.dribble,
            R.drawable.embarrassed, R.drawable.extreme_sexy_girl, R.drawable.feel_good,
            R.drawable.go, R.drawable.haha, R.drawable.hell_boy, R.drawable.hungry,
            R.drawable.look_down, R.drawable.misdoubt, R.drawable.nosebleed, R.drawable.oh,
            R.drawable.ops, R.drawable.pudency, R.drawable.rap, R.drawable.sad,
            R.drawable.sexy_girl, R.drawable.shame, R.drawable.smile, R.drawable.spiderman,
            R.drawable.still_dreaming, R.drawable.sure, R.drawable.surrender, R.drawable.sweat,
            R.drawable.sweet_kiss, R.drawable.tire, R.drawable.too_sad, R.drawable.waaaht,
            R.drawable.what};

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
        View aboutUsDialogView = inflater.inflate(R.layout.moods_grid, null);
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


