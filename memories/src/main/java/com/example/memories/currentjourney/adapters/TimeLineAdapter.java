package com.example.memories.currentjourney.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.audio.AudioDetail;
import com.example.memories.models.Audio;
import com.example.memories.models.CheckIn;
import com.example.memories.models.Contact;
import com.example.memories.models.Memories;
import com.example.memories.models.Mood;
import com.example.memories.models.Note;
import com.example.memories.models.Picture;
import com.example.memories.models.Video;
import com.example.memories.picture.PictureDetail;
import com.example.memories.utility.AudioPlayer;
import com.example.memories.utility.AudioUtil;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.LoadBitmapFromPath;
import com.example.memories.utility.TJPreferences;
import com.example.memories.video.VideoDetail;

import java.util.ArrayList;
import java.util.List;

public class TimeLineAdapter extends BaseAdapter {

    private static final String TAG = "TimeLineAdapter";
    Context context;
    List<Memories> memoriesList;
    private LayoutInflater mInflater;

    // To play and stop audio
    private boolean isPlaying;
    private AudioPlayer mPlayer;

    public TimeLineAdapter(Context context, List<Memories> memoriesList) {
        Log.d(TAG, "constructor called");
        this.context = context;
        this.memoriesList = memoriesList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setMemoriesList(List<Memories> memoriesList){
        this.memoriesList = memoriesList;
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(memoriesList.get(position).getMemType());
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public int getCount() {
        return memoriesList.size();
    }

    @Override
    public Object getItem(int position) {
        return memoriesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void updateList(List<Memories> updatedList){
        memoriesList = updatedList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "get view called");
        final int type = getItemViewType(position);
        System.out.println("getView " + position + " " + convertView + " type = " + type);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            // uncommon views in various memories
            switch (type) {
                case HelpMe.TYPE_PICTURE:
                    convertView = mInflater.inflate(R.layout.timeline_list_picture_item, null);
                    holder.timelineItemCaption = (TextView) convertView
                            .findViewById(R.id.timelineItemCaption);
                    holder.timelineItemImage = (ImageView) convertView
                            .findViewById(R.id.timelineItemImage);
                    break;

                case HelpMe.TYPE_AUDIO:
                    convertView = mInflater.inflate(R.layout.timeline_list_audio_item, null);
                    holder.timelineItemAudioPlayBtn = (ImageButton) convertView
                            .findViewById(R.id.timelineItemPlayBtn);
                    holder.timelineItemCaption = (TextView) convertView
                            .findViewById(R.id.timelineItemCaption);
                    break;

                case HelpMe.TYPE_VIDEO:
                    convertView = mInflater.inflate(R.layout.timeline_list_picture_item, null);
                    holder.timelineItemImage = (ImageView) convertView
                            .findViewById(R.id.timelineItemImage);
                    holder.timelineItemCaption = (TextView) convertView
                            .findViewById(R.id.timelineItemCaption);
                    break;

                case HelpMe.TYPE_NOTE:
                    convertView = mInflater.inflate(R.layout.timeline_list_note_item, null);
                    holder.timelineItemContent = (TextView) convertView
                            .findViewById(R.id.timelineItemContent);
                    break;

                case HelpMe.TYPE_CHECKIN:
                    convertView = mInflater.inflate(R.layout.timeline_list_checkin_item, null);
                    holder.timelineItemCaption = (TextView) convertView
                            .findViewById(R.id.timelineItemCaption);
                    break;

                case HelpMe.TYPE_MOOD:
                    convertView = mInflater.inflate(R.layout.timeline_list_mood_item, null);
                    holder.timelineItemCaption = (TextView) convertView
                            .findViewById(R.id.timelineItemCaption);
                    break;

            }

            // common views in all memories
            holder.timeLineProfileImg = (ImageView) convertView
                    .findViewById(R.id.timelineItemUserImage);
            holder.timelineNoLikesTxt = (TextView) convertView.findViewById(R.id.noLikesTxt);
            holder.timelineItemTime = (TextView) convertView.findViewById(R.id.timelineItemTime);
            holder.timelineItemFavBtn = (ImageButton) convertView
                    .findViewById(R.id.timelineItemFavIcon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Log.d(TAG, "3.1");

        // set on click listeners to handle events on multiple buttons
        holder.timelineItemFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> list = null;
                Log.d(TAG, "list is " + list);
                Memories mem = memoriesList.get(position);
                Log.d(TAG, "Memory liked by" + mem.getLikedBy());
                // check whether the memory has been liked by the user

                List<String> likedBy = mem.getLikedBy();
                if(likedBy == null){
                    likedBy = new ArrayList();
                }
                Log.d(TAG,"fav button clicked position " + likedBy + TJPreferences.getUserId(context));
                if (likedBy.contains(TJPreferences.getUserId(context))) {
                    likedBy.remove(TJPreferences.getUserId(context));
                    Log.d(TAG, "heart empty");
                    holder.timelineItemFavBtn.setImageResource(R.drawable.heart_empty);
                } else {
                    likedBy.add(TJPreferences.getUserId(context));
                    Log.d(TAG, "heart full");
                    holder.timelineItemFavBtn.setImageResource(R.drawable.heart_full);
                }

                // update the value in the list and database
                holder.timelineNoLikesTxt.setText(String.valueOf(likedBy.size()));
                if (likedBy.size() == 0) {
                    likedBy = null;
                }
                mem.setLikedBy(likedBy);
                mem.updateLikedBy(context, mem.getId(), likedBy);
            }
        });

        // set all the data which is same for all memories
        // DATE --
        final Memories memory = memoriesList.get(position);
        holder.timelineItemTime.setText(HelpMe.getDate(memory.getCreatedAt(), HelpMe.DATE_FULL));

        // if the memory is from current user
        Log.d(TAG, "Iam executing" + memory.getCreatedBy() + TJPreferences.getUserId(context));
        if (memory.getCreatedBy().equals(TJPreferences.getUserId(context))) {
            holder.timeLineProfileImg.setImageBitmap(BitmapFactory
                    .decodeFile(TJPreferences.getProfileImgPath(context)));
        } else {
            Contact contact = ContactDataSource.getContactById(context, memory.getCreatedBy());
            if (contact != null) {
                holder.timeLineProfileImg.setImageBitmap(BitmapFactory.decodeFile(contact
                        .getPicLocalUrl()));
            }
        }
        if (memory.getLikedBy() == null){
            holder.timelineNoLikesTxt.setText("0");
            holder.timelineItemFavBtn.setImageResource(R.drawable.heart_empty);
        }else{
            holder.timelineNoLikesTxt.setText(String.valueOf(memory.getLikedBy().size()));
            if (memory.getLikedBy().contains(TJPreferences.getUserId(context))){
                holder.timelineItemFavBtn.setImageResource(R.drawable.heart_full);
            }else {
                holder.timelineItemFavBtn.setImageResource(R.drawable.heart_empty);
            }
        }

        Log.d(TAG, "3.2");

        switch (type) {
            case 0:
                Log.d(TAG, "in picture");

                Picture pic = (Picture) memoriesList.get(position);
                if (pic.getPicThumbnailPath() != null) {
                    LoadBitmapFromPath.loadBitmap(pic.getPicThumbnailPath(), holder.timelineItemImage, 256, 192, context);
//					try {
//						holder.timelineItemImage.setImageBitmap(HelpMe.decodeSampledBitmapFromPath(
//								context, pic.getDataLocalURL(), 680, 250));
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
                    holder.timelineItemCaption.setText(pic.getCaption());

                } else {
                    Log.d(TAG, "no local data URL set");
                }
                break;
            case 1:
                Log.d(TAG, "in audio");
                holder.timelineItemAudioPlayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Audio audio = (Audio) memoriesList.get(position);

                        if (!isPlaying) {
                            if (audio.getDataLocalURL() == null) {
                                ProgressDialog pDialog = new ProgressDialog(context);
                                pDialog.setMessage("Loading...");
                                pDialog.show();
                                audio.setDataLocalURL(AudioUtil.saveAudio(context, audio));
                                pDialog.dismiss();
                            }
                            mPlayer = new AudioPlayer(audio.getDataLocalURL());
                            mPlayer.startPlaying();
                        } else {
                            mPlayer.stopPlaying();
                        }
                        isPlaying = !isPlaying;

                        // HelpMe.playAudio(audio.getDataURL(), context);
                    }
                });
                Log.d(TAG, "iN AUDIO ENDED HERE");
                break;

            case 2:
                Log.d(TAG, "in video");
                Video vid = (Video) memoriesList.get(position);

//                LoadThumbnailFromPath.loadBitmap(vid.getDataLocalURL(), holder.timelineItemImage, context);
                holder.timelineItemCaption.setText(vid.getCaption());
                LoadBitmapFromPath.loadBitmap(vid.getLocalThumbPath(), holder.timelineItemImage, 256, 192, context);
                //holder.timelineItemImage.setImageBitmap(HelpMe.getVideoThumbnail(vid.getDataURL()));
                break;

            case 3:
                Log.d(TAG, "in notes_capture");
                Note note = (Note) memoriesList.get(position);
                holder.timelineItemContent.setText(note.getContent());
                break;

            case 4:
                Log.d(TAG, "in checkin");
                CheckIn checkin = (CheckIn) memoriesList.get(position);
                String checkInStatus = checkin.getCaption() + " @ " + checkin.getCheckInPlaceName();
                if (checkin.getCheckInWith() != null && checkin.getCheckInWith().size() > 0) {
                    Contact firstContact = ContactDataSource.getContactById(context, checkin.getCheckInWith().get(0));
                    if (firstContact != null) {
                        if (checkin.getCheckInWith().size() == 1) {
                            checkInStatus += " with " + firstContact.getName();
                        } else if (checkin.getCheckInWith().size() > 1) {
                            checkInStatus += " with " + firstContact.getName() + " and " + (checkin.getCheckInWith().size() - 1) + " others";
                        }
                    }
                }
                holder.timelineItemCaption.setText(checkInStatus);
                break;

            case 5:
                Log.d(TAG, "in mood");
                Mood mood = (Mood) memoriesList.get(position);
                String friendMood = "";
                Contact fContact = ContactDataSource.getContactById(context, mood.getBuddyIds().get(0));
                if (fContact != null) {
                    if (mood.getBuddyIds().size() == 1) {
                        friendMood += fContact.getName();
                    } else if (mood.getBuddyIds().size() > 1) {
                        friendMood += fContact.getName() + " and " + (mood.getBuddyIds().size() - 1) + " others";
                    }
                }
                friendMood += " feeling " + mood.getMood() + " because " + mood.getReason();
                holder.timelineItemCaption.setText(friendMood);

            default:
                break;
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch (type) {
                    case HelpMe.TYPE_PICTURE:
                        intent = new Intent(context, PictureDetail.class);
                        intent.putExtra("PICTURE_ID", memory.getId());
                        context.startActivity(intent);
                        break;

                    case HelpMe.TYPE_VIDEO:
                        intent = new Intent(context, VideoDetail.class);
                        intent.putExtra("VIDEO_ID", memory.getId());
                        context.startActivity(intent);
                        break;
                    case HelpMe.TYPE_AUDIO:
                        intent = new Intent(context, AudioDetail.class);
                        intent.putExtra("AUDIO_ID", memory.getId());
                        context.startActivity(intent);
                        break;
                }
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        public TextView timelineItemCaption;
        public TextView timelineItemTime;
        public ImageView timelineItemImage;
        public TextView timelineItemContent;
        public ImageButton timelineItemFavBtn;
        public ImageButton timelineItemAudioPlayBtn;
        public ImageView timeLineProfileImg;
        public TextView timelineNoLikesTxt;
    }
}
