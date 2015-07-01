package com.traveljar.memories.gallery.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.audio.DownloadAudioAsyncTask;
import com.traveljar.memories.customevents.AudioDownloadEvent;
import com.traveljar.memories.models.Audio;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.utility.AudioPlayer;

import java.util.List;

import de.greenrobot.event.EventBus;

public class AudioGalleryAdapter extends BaseAdapter {

    private static final String TAG = "AUDIO_GALLERY_ADAPTER";
    private Context mContext;
    private List<Memories> mAudioList;

    //fields required to play audio
    private AudioPlayer mPlayer = null;
    private boolean isPlaying = false;
    private String currentPlayingAudioId = "-1"; // This will store the id of the currently playing audio (default -1)
    private int lastPlayingAudioPosition = -1;

    private ProgressDialog mProgressDialog;

    private static final int DOWNLOAD_EVENT_CODE = 0;

    public AudioGalleryAdapter(Context context, List<Memories> audioList) {
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
        final Audio audio = (Audio)mAudioList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_audio_list_item, null);
        }
        final ImageButton playAudio = (ImageButton) convertView.findViewById(R.id.gallery_audio_list_item_play);

        //If current audio is being played than put pause button else put play button
        if (currentPlayingAudioId.equals(audio.getId())) {
            playAudio.setImageResource(R.drawable.pause_audio_red);
        } else {
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
                        // register download  audio event to the request bus
                        registerEvent();
                        DownloadAudioAsyncTask asyncTask = new DownloadAudioAsyncTask(DOWNLOAD_EVENT_CODE, audio);
                        asyncTask.execute();
                    } else {
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
                    if (currentPlayingAudioId.equals(audio.getId())) {
                        Log.d(TAG, "play audio button clicked for the audio which was already playing");
                        playAudio.setImageResource(R.drawable.play_audio_red);
                        currentPlayingAudioId = "-1";
                        lastPlayingAudioPosition = -1;
                        isPlaying = false;
                    } else {
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

    private void registerEvent(){
        EventBus.getDefault().register(this);
    }

    private void unRegisterEvent(){
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(AudioDownloadEvent event){
        if(event.getCallerCode() == DOWNLOAD_EVENT_CODE) {
            unRegisterEvent();
            mProgressDialog.dismiss();
            if (event.isSuccess()) {
                currentPlayingAudioId = event.getAudio().getId();
                mPlayer = new AudioPlayer(event.getAudio().getDataLocalURL());
                mPlayer.startPlaying();
                AudioDataSource.updateDataLocalUrl(mContext, event.getAudio().getId(), event.getAudio().getDataLocalURL());
            } else {
                Toast.makeText(mContext, "Sorry, unable to download your audio, please try again later", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
