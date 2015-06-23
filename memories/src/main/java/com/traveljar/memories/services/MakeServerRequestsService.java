package com.traveljar.memories.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.SQLitedatabase.CheckinDataSource;
import com.traveljar.memories.SQLitedatabase.MoodDataSource;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.SQLitedatabase.VideoDataSource;
import com.traveljar.memories.models.Audio;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.models.Mood;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.utility.AudioUtil;
import com.traveljar.memories.utility.CheckinUtil;
import com.traveljar.memories.utility.MoodUtil;
import com.traveljar.memories.utility.NotesUtil;
import com.traveljar.memories.utility.PictureUtilities;
import com.traveljar.memories.utility.VideoUtil;

/**
 * Created by ankit on 22/6/15.
 */
public class MakeServerRequestsService extends IntentService  {

    private Request request;
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
        request = RequestQueueDataSource.getFirstNonCompletedRequest(this);
        noRequestTry = 0;
        int i = 0;
        boolean result;

        do{
            if(request == null){
                if(RequestQueueDataSource.getFailedRequestsCount(this) != 0){
                    // make all the finished requests as not completed
                    RequestQueueDataSource.updateStatusOfAllFailedRequests(this);
                }//else no action

            }else {
                switch (request.getCategoryType()){
                    case Request.CATEGORY_TYPE_AUDIO :
                        for(i = 1; i <= 3; i++){
                            result = audioRequests(request);
                            if(result){
                                break;
                            }
                        }
                        break;

                    case Request.CATEGORY_TYPE_CHECKIN :
                        for(i = 1; i <= 3; i++) {
                            result = checkInRequests(request);
                            if(result){
                                break;
                            }
                        }
                        break;

                    case Request.CATEGORY_TYPE_MOOD :
                        for(i = 1; i <= 3; i++) {
                            result = moodRequests(request);
                            if(result){
                                break;
                            }
                        }
                        break;

                    case Request.CATEGORY_TYPE_NOTE :
                        for(i = 1; i <= 3; i++) {
                            result = noteRequests(request);
                            if(result){
                                break;
                            }
                        }
                        break;

                    case Request.CATEGORY_TYPE_PICTURE :
                        for(i = 1; i <= 3; i++) {
                            result = pictureRequests(request);
                            if(result){
                                break;
                            }
                        }
                        break;

                    case Request.CATEGORY_TYPE_VIDEO :
                        for(i = 1; i <= 3; i++) {
                            result = videoRequests(request);
                            if(result){
                                break;
                            }
                        }
                        break;
                }
                RequestQueueDataSource.updateRequestStatus(this, request.getId(), i == 3 ? Request.REQUEST_STATUS_FAILED :
                        Request.REQUEST_STATUS_COMPLETED);
                request = RequestQueueDataSource.getFirstNonCompletedRequest(this);
            }
        }while (request != null && RequestQueueDataSource.getFailedRequestsCount(this) != 0);
    }

    public boolean audioRequests(Request request){
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                Audio audio = AudioDataSource.getAudioById(this, request.getLocalId());
                boolean result = AudioUtil.uploadAudioOnServer(this, audio);
                if (result)
                    RequestQueueDataSource.updateRequestStatus(this, request.getId(), Request.REQUEST_STATUS_COMPLETED);
                break;

            case Request.OPERATION_TYPE_LIKE:

                break;

            case Request.OPERATION_TYPE_UNLIKE:
                break;

        }
        return false;
    }

    public boolean checkInRequests(Request request){
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                CheckIn checkIn = CheckinDataSource.getCheckInById(request.getLocalId(), this);
                boolean result = CheckinUtil.uploadCheckInOnServer(this, checkIn);
                if (result)
                    RequestQueueDataSource.updateRequestStatus(this, request.getId(), Request.REQUEST_STATUS_COMPLETED);
                break;

            case Request.OPERATION_TYPE_LIKE:
                break;

            case Request.OPERATION_TYPE_UNLIKE:
                break;

        }
        return false;
    }

    public boolean moodRequests(Request request){
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                Mood mood = MoodDataSource.getMoodById(request.getLocalId(), this);
                boolean result = MoodUtil.uploadMoodOnServer(this, mood);
                if (result)
                    RequestQueueDataSource.updateRequestStatus(this, request.getId(), Request.REQUEST_STATUS_COMPLETED);
                break;

            case Request.OPERATION_TYPE_LIKE:
                break;

            case Request.OPERATION_TYPE_UNLIKE:
                break;

        }
        return false;
    }

    public boolean noteRequests(Request request){
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                Note note = NoteDataSource.getNote(request.getLocalId(), this);
                boolean result = NotesUtil.uploadNoteOnServer(this, note);
                if (result)
                    RequestQueueDataSource.updateRequestStatus(this, request.getId(), Request.REQUEST_STATUS_COMPLETED);
                break;

            case Request.OPERATION_TYPE_LIKE:
                break;

            case Request.OPERATION_TYPE_UNLIKE:
                break;

        }
        return false;
    }

    public boolean pictureRequests(Request request){
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                Picture picture = PictureDataSource.getPictureById(this, request.getLocalId());
                boolean result = PictureUtilities.uploadPicOnServer(this, picture);
                if (result)
                    RequestQueueDataSource.updateRequestStatus(this, request.getId(), Request.REQUEST_STATUS_COMPLETED);
                return result;
            case Request.OPERATION_TYPE_LIKE:
                break;

            case Request.OPERATION_TYPE_UNLIKE:
                break;
        }
        return false;
    }

    public boolean videoRequests(Request request){
        switch (request.getOperationType()) {
            case Request.OPERATION_TYPE_CREATE:
                Video video = VideoDataSource.getVideoById(request.getLocalId(), this);
                boolean result = VideoUtil.uploadVideoOnServer(this, video);
                if (result)
                    RequestQueueDataSource.updateRequestStatus(this, request.getId(), Request.REQUEST_STATUS_COMPLETED);
                return result;

            case Request.OPERATION_TYPE_LIKE:
                break;

            case Request.OPERATION_TYPE_UNLIKE:
                break;

        }
        return false;
    }

}
