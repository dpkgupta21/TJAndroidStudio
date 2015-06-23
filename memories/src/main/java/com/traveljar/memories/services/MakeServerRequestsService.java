package com.traveljar.memories.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.utility.PictureUtilities;

/**
 * Created by ankit on 22/6/15.
 */
public class MakeServerRequestsService extends IntentService  {

    private Request request;

    private static final String TAG = "<ServerRequestService>";

    public MakeServerRequestsService() {
        super("ServerRequestService");
    }

    public MakeServerRequestsService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        request = RequestQueueDataSource.getFirstNonCompletedRequest(this);
        if(request == null){
            Log.d(TAG, "no request in the queue so exiting");
        }else {
            switch (request.getCategoryType()){
                case Request.CATEGORY_TYPE_AUDIO :
                case Request.CATEGORY_TYPE_CHECKIN :
                case Request.CATEGORY_TYPE_MOOD :
                case Request.CATEGORY_TYPE_NOTE :
                case Request.CATEGORY_TYPE_PICTURE :
                    Picture picture = PictureDataSource.getPictureById(this, request.getLocalId());
                    switch (request.getOperationType()) {
                        case Request.OPERATION_TYPE_CREATE:
                            int result = PictureUtilities.uploadPicOnServer(this, picture);
                            if (result == 0)
                                RequestQueueDataSource.updateRequestStatus(this, request.getId(), Request.REQUEST_STATUS_COMPLETED);
                            break;

                        case Request.OPERATION_TYPE_LIKE:
                            break;

                        case Request.OPERATION_TYPE_UNLIKE:
                            break;

                    }
                case Request.CATEGORY_TYPE_VIDEO :
            }
        }
    }

}
