package com.traveljar.memories.video;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.traveljar.memories.R;
import com.traveljar.memories.models.MakeRequest;
import java.util.List;

/**
 * Created by deeksha.chaturvedi on 04/09/2015.
 */
public class MakeVideoRequestAdapter extends ArrayAdapter<MakeRequest> {

    private static final String TAG = "[MakeVideoRequestAdapter]";
    private final Activity context;
    private List<MakeRequest> makeVideoRequestList;
    MediaPlayer mPlayer;
    MakeRequest makeRequest = new MakeRequest();

    public MakeVideoRequestAdapter(Activity context, List<MakeRequest> makeVideoRequestList) {

        super(context, R.layout.activity_make_video_request,makeVideoRequestList);
        this.context = context;
        this.makeVideoRequestList = makeVideoRequestList;
    }

    public View getView(final int position, final View convertView, ViewGroup parent) {
        View rowView = null;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.activity_make_video_request_list_item, null);
            final TextView tvAudio = (TextView) rowView.findViewById(R.id.tv_audio_name);
            final ImageView ivPlay = (ImageView) rowView.findViewById(R.id.iv_audio_play);
            ivPlay.setTag(position);

            final ImageView ivChecked = (ImageView) rowView.findViewById(R.id.iv_checked);
            ivChecked.setTag(position);

            if(makeVideoRequestList.get(position).isPlaying()){
                ivPlay.setImageResource(R.drawable.pause);
                Log.d(TAG, "in isplaying if part................");
                       }
            else{
                ivPlay.setImageResource(R.drawable.play_audio);
                Log.d(TAG, "in isplaying else part................");
                        }


            ivPlay.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View view) {

                    int pos = (Integer) view.getTag();
                    makeRequest = makeVideoRequestList.get(pos);
                    if (mPlayer == null) {
                        mPlayer = MediaPlayer.create(getContext(), makeRequest.getAudioResource());/* makeVideoRequestList.get(pos).getAudioResource()*///Create MediaPlayer object with MP3 file under res/raw folder
                        mPlayer.start();
                        makeRequest.setIsPlaying(true);
                        Log.d(TAG, "in first if part..............////////////");
                    } else {
                        for (int i = 0; i < makeVideoRequestList.size(); i++) {
                            if (pos == i) {
                                Log.d(TAG, "in else-if part..............");

                            } else {
                                Log.d(TAG, "in else-else part..............");
                                mPlayer.pause();
                                makeVideoRequestList.get(i).setIsPlaying(false);
                             }
                        }
                        if (makeRequest.isPlaying()) {
                            mPlayer.pause();
                            Log.d(TAG, "in second if part");
                            makeRequest.setIsPlaying(false);
                            // ivChecked.setVisibility(View.GONE);

                        } else {
                            mPlayer = MediaPlayer.create(getContext(), makeRequest.getAudioResource());
                            mPlayer.start();
                            Log.d(TAG, "in last else part");
                            makeRequest.setIsPlaying(true);
                        }
                    }
                    Log.d(String.valueOf(pos), TAG);
                    notifyDataSetChanged();
            }


        });

            if(makeVideoRequestList.get(position).isChecked())
                ivChecked.setVisibility(View.VISIBLE);
            else
                ivChecked.setVisibility(View.GONE);


            rowView.setTag(position);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    makeRequest = makeVideoRequestList.get(pos);
                    if (makeRequest.isChecked()) {
                        makeRequest.setIsChecked(false);

                    } else {
                        for (int i = 0; i < makeVideoRequestList.size(); i++) {
                            if (pos == i) {
                                makeVideoRequestList.get(i).setIsChecked(true);
                            } else {
                                makeVideoRequestList.get(i).setIsChecked(false);
                            }
                        }
                    }

                    Log.d(String.valueOf(pos), TAG);
                    notifyDataSetChanged();
                }
            });

            tvAudio.setText(makeVideoRequestList.get(position).getName());
        }
        return rowView;
    }


    public void onBackPressed() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

}
