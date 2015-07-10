package com.traveljar.memories.newjourney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.customevents.ContactsFetchEvent;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.newjourney.adapters.AllFriendsListAdapter;
import com.traveljar.memories.services.PullContactsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

public class AllFriendsList extends AppCompatActivity {
    private static final String TAG = "AllFriendsList";
    //CustomResultReceiver mReceiver;
    private AllFriendsListAdapter mAdapter;
    private List<Contact> list;
    private ListView listView;
    private ProgressDialog mDialog;

    // For the request bus receive event to discard the received event which is not meant for this activity
    private static int ACTIVITY_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_traveljar_contact_list);

        setUpToolBar();

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("please wait while we fetch your contacts from server");
        mDialog.setCanceledOnTouchOutside(false);

        list = getIntent().getParcelableArrayListExtra("FRIENDS_LIST");
        for(Contact contact : list){
            Log.d(TAG, " is selected " + contact.isSelected());
        }
        listView = (ListView) findViewById(R.id.all_contacts_listview);
        mAdapter = new AllFriendsListAdapter(this, list);
        listView.setAdapter(mAdapter);

        // Search functionality to filter list
        EditText query = (EditText) findViewById(R.id.all_contacts_searchview);
        query.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Log.d(TAG, "search query = " + cs);
                AllFriendsList.this.mAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Add Friends");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllFriendsList.this.finish();
            }
        });

        toolbar.inflateMenu(R.menu.new_journey_selected_friends);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_next:
                        goToNext();
                        return true;
                    case R.id.action_refresh:
                        Intent intent = new Intent(getBaseContext(), PullContactsService.class);
                        Log.d(TAG, "starting intent for pull contacts service");
                        //intent.putExtra("RECEIVER", mReceiver);
                        intent.putExtra("ACTIVITY_CODE", ACTIVITY_CODE);
                        startService(intent);
                        mDialog.show();
                        return true;
                }
                return false;
            }
        });

    }

    private void goToNext() {
        Log.d(TAG, "done is clicked!!!");
        List<Contact> selectedList = new ArrayList<>();
        for(Contact contact : list){
            if(contact.isSelected()){
                selectedList.add(contact);
            }
        }

        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("SELECTED_CONTACTS_LIST", (ArrayList<Contact>) selectedList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(ContactsFetchEvent event){
        //Discard the event if the event's activity code is not similar to its own activity code
        Log.d(TAG, "inside on event for AllFriendsList");
        if(event.getActivityCode() == ACTIVITY_CODE) {
            mDialog.dismiss();
            List<Contact> newList = ContactDataSource.getAllContacts(this);
            for(Contact contact : newList){
                if(!list.contains(contact)){
                    list.add(contact);
                }
            }
            Collections.sort(list);
            if (mAdapter == null) {
                Log.d(TAG, "on receive result called with null");
                mAdapter = new AllFriendsListAdapter(this, list);
                listView.setAdapter(mAdapter);
            } else {
                Log.d(TAG, "on receive result called with not null");
                mAdapter.updateList(list);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}