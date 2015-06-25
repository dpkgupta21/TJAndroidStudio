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

public class MakeServerRequestsService extends IntentService {

    private Request request;
    private int reqTotalCount;

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

        while (HelpMe.isNetworkAvailable(this)) {

            // Check if any requests are there in RQ, otherwise break
            reqTotalCount = RequestQueueDataSource.getTotalRequestsCount(this);
            if (reqTotalCount < 1) {
                break;
            }
            Log.d(TAG, "totalReqCount = " + reqTotalCount);
            request = RequestQueueDataSource.getFirstNonCompletedRequest(this);

            if (request != null) {
                Log.d(TAG, "NC request category type = " + request.getCategoryType() + " retotacount " + reqTotalCount);
                parseRequest(request);
                // Increment all requests no of attempts to keep track for failure
                RequestQueueDataSource.incrementRequestNoOfAttempts(request, this);
            } else {
                if (RequestQueueDataSource.getFailedRequestsCount(this) != 0) {
                    Log.d(TAG, "NC request category type = " + request.getCategoryType() + " retotacount " + reqTotalCount);
                    request = RequestQueueDataSource.getFirstFailedRequest(this);
                    parseRequest(request);
                    RequestQueueDataSource.incrementRequestNoOfAttempts(request, this);
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
        Audio audio = AudioDataSource.getAudioById(this, request.getObjectLocalId());
        boolean result;
        Like like;
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                result = AudioUtil.uploadAudioOnServer(this, audio);
                return result;

            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(audio.getIdOnServer());
                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(audio.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_DELETE:
                MemoriesUtil.getInstance().deleteMemory(this, audio.getIdOnServer());
        }
        return false;
    }

    public boolean checkInRequests(Request request) {
        CheckIn checkIn = CheckinDataSource.getCheckInById(request.getObjectLocalId(), this);
        boolean result;
        Like like;
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                result = CheckinUtil.uploadCheckInOnServer(this, checkIn);
                return result;

            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(checkIn.getIdOnServer());
                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(checkIn.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_DELETE:
                MemoriesUtil.getInstance().deleteMemory(this, checkIn.getIdOnServer());
        }
        return false;
    }

    public boolean moodRequests(Request request) {
        Mood mood = MoodDataSource.getMoodById(request.getObjectLocalId(), this);
        boolean result;
        Like like;
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                result = MoodUtil.uploadMoodOnServer(this, mood);
                return result;

            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(mood.getIdOnServer());
                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(mood.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_DELETE:
                MemoriesUtil.getInstance().deleteMemory(this, mood.getIdOnServer());
        }
        return false;
    }

    public boolean noteRequests(Request request) {
        Note note = NoteDataSource.getNoteById(request.getObjectLocalId(), this);
        boolean result;
        Like like;

        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                result = NotesUtil.uploadNoteOnServer(this, note);
                return result;

            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(note.getIdOnServer());

                Log.d(TAG, "1.1.1" + note);
                Log.d(TAG, "1.1.1" + like.getId() + "==" + like.getIdOnServer() + "===" + like.getMemoryLocalId() + "===" + like.getMemoryServerId());


                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(note.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_DELETE:
                MemoriesUtil.getInstance().deleteMemory(this, note.getIdOnServer());
        }
        return false;
    }

    public boolean pictureRequests(Request request) {
        Picture picture = PictureDataSource.getPictureById(this, request.getObjectLocalId());
        boolean result;
        Like like;
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                result = PictureUtilities.uploadPicOnServer(this, picture);
                return result;
            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(picture.getIdOnServer());
                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(picture.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_DELETE:
                MemoriesUtil.getInstance().deleteMemory(this, picture.getIdOnServer());
        }
        return false;
    }

    public boolean videoRequests(Request request) {
        Video video = VideoDataSource.getVideoById(request.getObjectLocalId(), this);
        boolean result;
        Like like;
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                result = VideoUtil.uploadVideoOnServer(this, video);
                return result;

            case Request.OPERATION_TYPE_LIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(video.getIdOnServer());
                return MemoriesUtil.likeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_UNLIKE:
                like = LikeDataSource.getLikeById(request.getObjectLocalId(), this);
                like.setMemoryLocalId(video.getIdOnServer());
                return MemoriesUtil.unlikeMemoryOnServer(this, like);

            case Request.OPERATION_TYPE_DELETE:
                MemoriesUtil.getInstance().deleteMemory(this, video.getIdOnServer());
        }
        return false;
    }

}
