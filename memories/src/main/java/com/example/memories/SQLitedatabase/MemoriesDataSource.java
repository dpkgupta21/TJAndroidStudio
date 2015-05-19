package com.example.memories.SQLitedatabase;

import android.content.Context;

import com.example.memories.models.Memories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoriesDataSource {

    public static List<Memories> getAllMemoriesList(Context context, String journeyId) {
        List<Memories> memoriesList = new ArrayList<Memories>();
        memoriesList.addAll(PictureDataSource.getPictureMemoriesFromJourney(context, journeyId));
        memoriesList.addAll(AudioDataSource.getAudioMemoriesForJourney(context, journeyId));
        memoriesList.addAll(CheckinDataSource.getAllCheckinsList(context, journeyId));
        memoriesList.addAll(NoteDataSource.getAllNotesList(context, journeyId));
        memoriesList.addAll(VideoDataSource.getAllVideoMemories(context, journeyId));
        memoriesList.addAll(MoodDataSource.getMoodsFromJourney(context, journeyId));
        Collections.sort(memoriesList);

        return memoriesList;
    }

}
