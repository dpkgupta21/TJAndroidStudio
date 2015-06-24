package com.traveljar.memories.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.SQLitedatabase.CheckinDataSource;
import com.traveljar.memories.SQLitedatabase.LikeDataSource;
import com.traveljar.memories.SQLitedatabase.MoodDataSource;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.SQLitedatabase.VideoDataSource;
import com.traveljar.memories.models.Audio;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.models.Mood;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.utility.AudioUtil;
import com.traveljar.memories.utility.CheckinUtil;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.MemoriesUtil;
import com.traveljar.memories.utility.MoodUtil;
import com.traveljar.memories.utility.NotesUtil;
import com.traveljar.memories.utility.PictureUtilities;
import com.traveljar.memories.utility.VideoUtil;

/**
 * Created by ankit on 22/6/15.
 */
public class MakeServerRequestsService extends IntentService {

    private Request request;
    private int reqTotalCount;
    private int noRequestTry = 0;

    private static final String TAG = "<ServerRequestService>";

    public MakeServerRequestsService() {
        super("ServerRequestService");
    }

    public MakeServerRequestsService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "service started");


        noRequestTry = 0;
        int i = 0;
        boolean result = false;

        while (HelpMe.isNetworkAvailable(this)) {

            // Check if any requests are there in RQ, otherwise break
            reqTotalCount = RequestQueueDataSource.getTotalRequestsCount(this);
            if (reqTotalCount < 1) {
                break;
            }
            Log.d(TAG, "totalReqCount = " + reqTotalCount);
            request = RequestQueueDataSource.getFirstNonCompletedRequest(this);

            if (request != null) {
                Log.d(TAG, "request = " + request.getCategoryType() + " retotacount " + reqTotalCount);
                // Increment all requests no of attempts to keep track for failure
                RequestQueueDataSource.incrementRequestNoOfAttempts(request, this);
                result = parseRequest(request);
            } else {
                if (RequestQueueDataSource.getFailedRequestsCount(this) != 0) {
                    request = RequestQueueDataSource.getFirstFailedRequest(this);
                    result = parseRequest(request);
                }//else no action
                else {
                    Log.d(TAG, "No more requests to serve!!");
                    break;
                }
            }
        }

    }

    public boolean parseRequest(Request request) {
        Boolean result = false;
        switch (request.getCategoryType()) {
            case Request.CATEGORY_TYPE_AUDIO:
                Log.d(TAG, "serving audio request");
                result = audioRequests(request);
                break;

            case Request.CATEGORY_TYPE_CHECKIN:
                Log.d(TAG, "serving checkin request");
                result = checkInRequests(request);
                break;

            case Request.CATEGORY_TYPE_MOOD:
                Log.d(TAG, "serving mood request");
                result = moodRequests(request);
                break;

            case Request.CATEGORY_TYPE_NOTE:

                Log.d(TAG, "serving note request");
                result = noteRequests(request);
                break;

            case Request.CATEGORY_TYPE_PICTURE:
                Log.d(TAG, "serving picture request");
                result = pictureRequests(request);
                break;

            case Request.CATEGORY_TYPE_VIDEO:
                Log.d(TAG, "serving video request");
                result = videoRequests(request);
                break;
        }

        if (result) {
            RequestQueueDataSource.updateRequestStatus(this, request.getId(), Request.REQUEST_STATUS_COMPLETED);
        }

        if (request.getNoOfAttempts() == 3) {
            RequestQueueDataSource.updateRequestStatus(this, request.getId(), Request.REQUEST_STATUS_FAILED);
            //RequestQueueDataSource.updateSubsequentRequestsToHold();
        }

        return result;
    }

    public boolean audioRequests(Request request) {
        Audio audio;
        boolean result;
        Like like;
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                audio = AudioDataSource.getAudioById(this, request.getObjectLocalId());
                result = AudioUtil.uploadAudioOnServer(this, audio);
                return result;

            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                audio = AudioDataSource.getAudioById(this, like.getMemorableId());
                like.setMemorableId(audio.getIdOnServer());
                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                audio = AudioDataSource.getAudioById(this, like.getMemorableId());
                like.setMemorableId(audio.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);

        }
        return false;
    }

    public boolean checkInRequests(Request request) {
        CheckIn checkIn;
        boolean result;
        Like like;
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                checkIn = CheckinDataSource.getCheckInById(request.getObjectLocalId(), this);
                result = CheckinUtil.uploadCheckInOnServer(this, checkIn);
                return result;

            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                checkIn = CheckinDataSource.getCheckInById(like.getMemorableId(), this);
                like.setMemorableId(checkIn.getIdOnServer());
                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                checkIn = CheckinDataSource.getCheckInById(like.getMemorableId(), this);
                like.setMemorableId(checkIn.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);

        }
        return false;
    }

    public boolean moodRequests(Request request) {
        Mood mood;
        boolean result;
        Like like;
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                mood = MoodDataSource.getMoodById(request.getObjectLocalId(), this);
                result = MoodUtil.uploadMoodOnServer(this, mood);
                return result;

            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                mood = MoodDataSource.getMoodById(like.getMemorableId(), this);
                like.setMemorableId(mood.getIdOnServer());
                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                mood = MoodDataSource.getMoodById(like.getMemorableId(), this);
                like.setMemorableId(mood.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);

        }
        return false;
    }

    public boolean noteRequests(Request request) {
        Note note;
        boolean result;
        Like like;
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                note = NoteDataSource.getNote(request.getObjectLocalId(), this);
                result = NotesUtil.uploadNoteOnServer(this, note);
                return result;

            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                note = NoteDataSource.getNote(like.getMemorableId(), this);
                like.setMemorableId(note.getIdOnServer());
                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                note = NoteDataSource.getNote(like.getMemorableId(), this);
                like.setMemorableId(note.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);

        }
        return false;
    }

    public boolean pictureRequests(Request request) {
        Picture picture;
        boolean result;
        Like like;
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                picture = PictureDataSource.getPictureById(this, request.getObjectLocalId());
                result = PictureUtilities.uploadPicOnServer(this, picture);
                return result;
            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                picture = PictureDataSource.getPictureById(this, like.getMemorableId());
                like.setMemorableId(picture.getIdOnServer());
                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                picture = PictureDataSource.getPictureById(this, like.getMemorableId());
                like.setMemorableId(picture.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);
        }
        return false;
    }

    public boolean videoRequests(Request request) {
        Video video;
        boolean result;
        Like like;
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                video = VideoDataSource.getVideoById(request.getObjectLocalId(), this);
                result = VideoUtil.uploadVideoOnServer(this, video);
                return result;

            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                video = VideoDataSource.getVideoById(like.getMemorableId(), this);
                like.setMemorableId(video.getIdOnServer());
                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                video = VideoDataSource.getVideoById(like.getMemorableId(), this);
                like.setMemorableId(video.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);
        }
        return false;
    }

}
