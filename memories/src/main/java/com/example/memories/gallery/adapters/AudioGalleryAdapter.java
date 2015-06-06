package com.example.memories.gallery.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.AudioDataSource;
import com.example.memories.audio.DownloadAudioAsyncTask;
import com.example.memories.models.Audio;
import com.example.memories.utility.AudioPlayer;

import java.util.List;

public class AudioGalleryAdapter extends BaseAdapter implements DownloadAudioAsyncTask.OnAudioDownloadListener{

    private static final String TAG = "AUDIO_GALLERY_ADAPTER";
    private Context mContext;
    private List<Audio> mAudioList;

    //fields required to play audio
    private AudioPlayer mPlayer = null;
    private boolean isPlaying = false;
    private String currentPlayingAudioId = "-1"; // This will store the id of the currently playing audio (default -1)
    private int lastPlayingAudioPosition = -1;

    private ProgressDialog mProgressDialog;

    public AudioGalleryAdapter(Context context, List<Audio> audioList) {
        mContext = context;
        mAudioList = audioList;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("please wait while we are downloading your file");

    }

    @Override
    public int getCount() {
        return mAudioList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAudioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Audio audio = mAudioList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_audio_list_item, null);
        }
        final ImageButton playAudio = (ImageButton) convertView.findViewById(R.id.gallery_audio_list_item_play);

        //If current audio is being played than put pause button else put play button
        if(currentPlayingAudioId.equals(audio.getId())){
            playAudio.setImageResource(R.drawable.pause_audio_red);
        }else {
            playAudio.setImageResource(R.drawable.play_audio_red);
        }

        playAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isPlaying) {
                    //No audio is being played so play the current audio
                    Log.d(TAG, "No audio is being played so play the current audio");
                    if (audio.getDataLocalURL() == null) {
                        mProgressDialog.show();
                        DownloadAudioAsyncTask asyncTask = new DownloadAudioAsyncTask(AudioGalleryAdapter.this, audio);
                        asyncTask.execute();
                    }else {
                        mPlayer = new AudioPlayer(audio.getDataLocalURL());
                        mPlayer.startPlaying();
                    }
                    playAudio.setImageResource(R.drawable.pause_audio_red);
                    currentPlayingAudioId = audio.getId();
                    isPlaying = true;
                    lastPlayingAudioPosition = position;
                } else {
                    Log.d(TAG, "current audio id ->" + currentPlayingAudioId + " audio id ->" + audio.getId() + "...." + currentPlayingAudioId.equals(audio.getId()));
                    mPlayer.stopPlaying();

                    //If play clicked for the audio which is already playing than stop that audio
                    if(currentPlayingAudioId.equals(audio.getId())) {
                        Log.d(TAG, "play audio button clicked for the audio which was already playing");
                        playAudio.setImageResource(R.drawable.play_audio_red);
                        currentPlayingAudioId = "-1";
                        lastPlayingAudioPosition = -1;
                        isPlaying = false;
                    }else{
                        //If play clicked and another audio is also playing than stop that audio and play the requested one
                        Log.d(TAG, "play audio button clicked for the audio other than audio which is playing");
                        mPlayer = new AudioPlayer(audio.getDataLocalURL());
                        mPlayer.startPlaying();
                        playAudio.setImageResource(R.drawable.pause_audio_red);
                        currentPlayingAudioId = audio.getId();
                        lastPlayingAudioPosition = position;
                        // change the icon from pause to play for the previous audio
                    }
                }
            }
        });

        return convertView;
    }

    @Override
    public void onAudioDownload(String audioLocalUrl, Audio audio) {
        currentPlayingAudioId = audio.getId();
        mProgressDialog.dismiss();
        mPlayer = new AudioPlayer(audioLocalUrl);
        mPlayer.startPlaying();
        AudioDataSource.updateDataLocalUrl(mContext, audio.getId(), audioLocalUrl);
    }
}
