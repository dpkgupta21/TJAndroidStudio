package com.traveljar.memories.currentjourney.adapters;

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

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.audio.AudioDetail;
import com.traveljar.memories.audio.DownloadAudioAsyncTask;
import com.traveljar.memories.checkin.CheckinDetail;
import com.traveljar.memories.models.Audio;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.models.Mood;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.moods.MoodDetail;
import com.traveljar.memories.note.NoteDetail;
import com.traveljar.memories.picture.PictureDetail;
import com.traveljar.memories.utility.AudioPlayer;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.LoadThumbFromPath;
import com.traveljar.memories.utility.MemoriesUtil;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.video.VideoDetail;

import java.io.FileNotFoundException;
import java.util.List;

public class TimeLineAdapter extends BaseAdapter implements DownloadAudioAsyncTask.OnAudioDownloadListener {

    private static final String TAG = "TimeLineAdapter";
    Context context;
    List<Memories> memoriesList;
    private LayoutInflater mInflater;

    // To play and stop audio
    private boolean isPlaying;
    private AudioPlayer mPlayer;

    private ProgressDialog pDialog;

    public TimeLineAdapter(Context context, List<Memories> memoriesList) {
        Log.d(TAG, "constructor called");
        this.context = context;
        this.memoriesList = memoriesList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pDialog = new ProgressDialog(context);
        pDialog.setCanceledOnTouchOutside(false);
    }

    public void setMemoriesList(List<Memories> memoriesList) {
        this.memoriesList = memoriesList;
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(memoriesList.get(position).getMemType());
    }

    @Override
    public int getViewTypeCount() {
        return 7;
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

    public void updateList(List<Memories> updatedList) {
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
                    convertView = mInflater.inflate(R.layout.timeline_list_video_item, null);
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
                    holder.timelineItemCheckinPlace = (TextView) convertView.findViewById(R.id.timelineItemCheckinPlace);
                    break;

                case HelpMe.TYPE_MOOD:
                    convertView = mInflater.inflate(R.layout.timeline_list_mood_item, null);
                    holder.timelineItemCaption = (TextView) convertView
                            .findViewById(R.id.timelineItemCaption);
                    holder.timelineItemMoodBuddyImage1 = (ImageView) convertView.findViewById(R.id.timelineItemMoodBuddyPic1);
                    holder.timelineItemMoodBuddyImage2 = (ImageView) convertView.findViewById(R.id.timelineItemMoodBuddyPic2);
                    holder.timelineItemMoodBuddyImage3 = (ImageView) convertView.findViewById(R.id.timelineItemMoodBuddyPic3);
                    holder.timelineItemMoodBuddyImage4 = (ImageView) convertView.findViewById(R.id.timelineItemMoodBuddyPic4);
                    holder.timelineItemMoodiconTxt = (TextView) convertView.findViewById(R.id.timelineItemMoodIconTxt);
                    holder.timelineItemMoodicon = (ImageView) convertView.findViewById(R.id.timelineItemMoodIcon);
                    holder.timelineItemMoodExtraBuddyTxt = (TextView) convertView.findViewById(R.id.timelineItemMoodExtraBuddyTxt);
                    break;

            }

            // common views in all memories
            holder.timeLineProfileImg = (ImageView) convertView
                    .findViewById(R.id.timelineItemUserImage);
            holder.timelineNoLikesTxt = (TextView) convertView.findViewById(R.id.noLikesTxt);
            holder.timelineItemTime = (TextView) convertView.findViewById(R.id.timelineItemTime);
            holder.timelineItemFavBtn = (ImageButton) convertView
                    .findViewById(R.id.timelineItemFavIcon);
            holder.timelineItemUserName = (TextView) convertView
                    .findViewById(R.id.timelineItemUserName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Log.d(TAG, "3.1");

        // set on click listeners to handle events on multiple buttons
        holder.timelineItemFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Memories mem = memoriesList.get(position);
                String likeId = mem.isMemoryLikedByCurrentUser(context);// Check if memory liked by current user
                Like like;

                if (likeId == null) {
                    //If not liked, create a new like object, save it to local, update on server
                    Log.d(TAG, "video is not already liked so liking it");
                    like = MemoriesUtil.createLikeRequest(mem.getId(), Request.getCategoryTypeFromMemory(mem), context, mem.getMemType());
                    mem.getLikes().add(like);
                    holder.timelineItemFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                } else {
                    // If already liked, delete from local database, delete from server
                    Log.d(TAG, "memory is not already liked so removing the like");
                    like = mem.getLikeById(likeId);
                    holder.timelineItemFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                    MemoriesUtil.createUnlikeRequest(like, Request.getCategoryTypeFromMemory(mem), context);
                    mem.getLikes().remove(like);
                }
                holder.timelineNoLikesTxt.setText(String.valueOf(mem.getLikes().size()));
            }
        });

        // set all the data which is same for all memories
        // DATE --
        final Memories memory = memoriesList.get(position);
        holder.timelineItemTime.setText(HelpMe.getDate(memory.getCreatedAt(), HelpMe.DATE_FULL));
        Log.d(TAG, ContactDataSource.getContactById(context, memory.getCreatedBy()) + memory.getCreatedBy() + "!!!");
        holder.timelineItemUserName.setText(ContactDataSource.getContactById(context, memory.getCreatedBy()).getName());

        // if the memory is from current user
        Log.d(TAG, "Iam executing" + memory.getCreatedBy() + TJPreferences.getUserId(context));
        if (memory.getCreatedBy().equals(TJPreferences.getUserId(context))) {
            try {
                holder.timeLineProfileImg.setImageBitmap(HelpMe.decodeSampledBitmapFromPath(context, TJPreferences.getProfileImgPath(context), 80, 80));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Contact contact = ContactDataSource.getContactById(context, memory.getCreatedBy());
            if (contact != null) {
                holder.timeLineProfileImg.setImageBitmap(BitmapFactory.decodeFile(contact
                        .getPicLocalUrl()));
            }
        }

        //set the favourites count
        holder.timelineNoLikesTxt.setText(String.valueOf(memory.getLikes().size()));
        holder.timelineItemFavBtn.setImageResource(memory.isMemoryLikedByCurrentUser(context) != null ? R.drawable.ic_favourite_filled : R.drawable.ic_favourite_empty);

        Log.d(TAG, "3.2");

        switch (type) {
            case 5:
                Log.d(TAG, "in picture");

                Picture pic = (Picture) memoriesList.get(position);
                if (pic.getPicThumbnailPath() != null) {
                    LoadThumbFromPath.loadBitmap(pic.getPicThumbnailPath(), holder.timelineItemImage, 256, 192, context);
                    holder.timelineItemCaption.setText(pic.getCaption());

                } else {
                    Log.d(TAG, "no thumbnail present");
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
                                pDialog.setMessage("Loading...");
                                pDialog.show();
                                DownloadAudioAsyncTask asyncTask = new DownloadAudioAsyncTask(TimeLineAdapter.this, audio);
                                asyncTask.execute();
                            } else {
                                mPlayer = new AudioPlayer(audio.getDataLocalURL());
                                mPlayer.startPlaying();
                            }
                        } else {
                            mPlayer.stopPlaying();
                        }
                        isPlaying = !isPlaying;
                    }
                });
                Log.d(TAG, "iN AUDIO ENDED HERE");
                break;

            case 6:
                Log.d(TAG, "in video");
                Video vid = (Video) memoriesList.get(position);

//                LoadThumbnailFromPath.loadBitmap(vid.getDataLocalURL(), holder.timelineItemImage, context);
                holder.timelineItemCaption.setText(vid.getCaption());
                LoadThumbFromPath.loadBitmap(vid.getLocalThumbPath(), holder.timelineItemImage, 256, 192, context);
                //holder.timelineItemImage.setImageBitmap(HelpMe.getVideoThumbnail(vid.getDataURL()));
                break;

            case 4:
                Log.d(TAG, "in notes_capture");
                Note note = (Note) memoriesList.get(position);
                holder.timelineItemContent.setText(note.getContent());
                break;

            case 2:
                Log.d(TAG, "in checkin");
                CheckIn checkin = (CheckIn) memoriesList.get(position);
                holder.timelineItemCaption.setText(checkin.getCaption());
                holder.timelineItemCheckinPlace.setText(checkin.getCheckInPlaceName());
                break;

            case 3:
                Log.d(TAG, "in mood");
                Mood mood = (Mood) memoriesList.get(position);

                int buddyCount = mood.getBuddyIds().size();
                Contact fContact;
                for (int i = 0; i<buddyCount; i++){

                    switch (i){
                        case 0: fContact = ContactDataSource.getContactById(context, mood.getBuddyIds().get(i));
                            holder.timelineItemMoodBuddyImage1.setVisibility(View.VISIBLE);
                            holder.timelineItemMoodBuddyImage1.setImageBitmap(BitmapFactory.decodeFile(fContact
                                    .getPicLocalUrl()));
                            break;

                        case 1: fContact = ContactDataSource.getContactById(context, mood.getBuddyIds().get(i));
                            holder.timelineItemMoodBuddyImage2.setVisibility(View.VISIBLE);
                            holder.timelineItemMoodBuddyImage2.setImageBitmap(BitmapFactory.decodeFile(fContact
                                    .getPicLocalUrl()));
                            break;

                        case 2: fContact = ContactDataSource.getContactById(context, mood.getBuddyIds().get(i));
                            holder.timelineItemMoodBuddyImage3.setVisibility(View.VISIBLE);
                            holder.timelineItemMoodBuddyImage3.setImageBitmap(BitmapFactory.decodeFile(fContact
                                    .getPicLocalUrl()));
                            break;

                        case 3: fContact = ContactDataSource.getContactById(context, mood.getBuddyIds().get(i));
                            holder.timelineItemMoodBuddyImage4.setVisibility(View.VISIBLE);
                            holder.timelineItemMoodBuddyImage4.setImageBitmap(BitmapFactory.decodeFile(fContact
                                    .getPicLocalUrl()));
                            break;

                        default: Log.d(TAG, "more than 3 poepl ein mood");
                            holder.timelineItemMoodExtraBuddyTxt.setVisibility(View.VISIBLE);
                            break;
                    }

                }

                holder.timelineItemMoodiconTxt.setText(mood.getMood());
                holder.timelineItemCaption.setText(mood.getReason());
                int resourceId = context.getResources().getIdentifier(mood.getMood(), "drawable",
                        context.getPackageName());
                holder.timelineItemMoodicon.setImageResource(resourceId);
                holder.timelineItemMoodExtraBuddyTxt.setText("and " + (buddyCount - 4) + " others");

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
                    case HelpMe.TYPE_NOTE:
                        intent = new Intent(context, NoteDetail.class);
                        intent.putExtra("NOTE_ID", memory.getId());
                        context.startActivity(intent);
                        break;
                    case HelpMe.TYPE_CHECKIN:
                        intent = new Intent(context, CheckinDetail.class);
                        intent.putExtra("CHECKIN_ID", memory.getId());
                        context.startActivity(intent);
                        break;
                    case HelpMe.TYPE_MOOD:
                        intent = new Intent(context, MoodDetail.class);
                        intent.putExtra("MOOD_ID", memory.getId());
                        context.startActivity(intent);
                        break;
                }
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        });

        return convertView;
    }

    @Override
    public void onAudioDownload(String audioLocalUrl, Audio audio) {
        pDialog.dismiss();
        mPlayer = new AudioPlayer(audioLocalUrl);
        mPlayer.startPlaying();
        AudioDataSource.updateDataLocalUrl(context, audio.getId(), audioLocalUrl);
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
        public TextView timelineItemUserName;
        public TextView timelineItemCheckinPlace;

        // Mood
        public ImageView timelineItemMoodBuddyImage1;
        public ImageView timelineItemMoodBuddyImage2;
        public ImageView timelineItemMoodBuddyImage3;
        public ImageView timelineItemMoodBuddyImage4;
        public ImageView timelineItemMoodicon;
        public TextView timelineItemMoodiconTxt;
        public TextView timelineItemMoodExtraBuddyTxt;
    }
}
