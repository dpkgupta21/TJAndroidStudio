package com.traveljar.memories.newjourney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.newjourney.adapters.FriendsAutoCompleteAdapter;
import com.traveljar.memories.newjourney.adapters.SelectedFriendsListAdapter;
import com.traveljar.memories.volley.AppController;

import java.util.ArrayList;
import java.util.List;

public class SelectedFriendsList extends AppCompatActivity {

    private static final String TAG = "<SelectedFriendsList>";
    public List<Contact> selectedList;

    private List<Contact> allContactsList;
    public SelectedFriendsListAdapter contactListViewAdapter;
    private ListView contactListView;
    private ProgressDialog mProgressDialog;

    private FriendsAutoCompleteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_selected_friends_list);
        Log.d(TAG, "entered Add friends");

        setUpToolBar();

        selectedList = new ArrayList<>();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);

        contactListView = (ListView) findViewById(R.id.addFriendsList);

        initializeData();
    }

    private void initializeData() {
        mProgressDialog.dismiss();
        contactListViewAdapter = new SelectedFriendsListAdapter(this, selectedList);
        contactListView.setAdapter(contactListViewAdapter);

        // configure auto complete text view
        final MultiAutoCompleteTextView macTv = (MultiAutoCompleteTextView) findViewById(R.id.addFriendsContactSearch);

        allContactsList = ContactDataSource.getAllContacts(this);

        adapter = new FriendsAutoCompleteAdapter(this, allContactsList);
        macTv.setAdapter(adapter);
        macTv.setThreshold(1);
        macTv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        macTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Contact contact = adapter.getFilteredContactAtPosition(position);
                if (!selectedList.contains(contact)) {
                    selectedList.add(contact);
                    contactListViewAdapter.notifyDataSetChanged();
                }
                macTv.setText(null);
            }
        });
    }

    public void goToAllContactList(View v) {
        for(Contact contact : allContactsList){
            Log.d(TAG, "inside gotoAllContactList " + contact.isSelected());
        }
        Intent intent = new Intent(this, AllFriendsList.class);
        intent.putParcelableArrayListExtra("FRIENDS_LIST", (ArrayList<Contact>) allContactsList);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 1){
            allContactsList = data.getParcelableArrayListExtra("SELECTED_CONTACTS_LIST");
            selectedList.clear();
            for(Contact contact : allContactsList){
                if(contact.isSelected()){
                    selectedList.add(contact);
                }
            }
            for(Contact contact : allContactsList){
                Log.d(TAG, "inside gotoAllContactList " + contact.isSelected());
            }
            contactListViewAdapter.updateList(selectedList);
            contactListViewAdapter.notifyDataSetChanged();
        }
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Select Friends");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "finished");
                finish();
            }
        });
        TextView next = (TextView)toolbar.findViewById(R.id.action_done);
        next.setText("Next");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNext();
                finish();
            }
        });

    }

    private void goToNext() {
        AppController.buddyList = new ArrayList<>();
        // save all the selected friend's id in "buddyList" in AppCOntroller
        int len = selectedList.size();

        if (len > 0) {
            for (int i = 0; i < len; i++) {
                AppController.buddyList.add(selectedList.get(i).getIdOnServer());
                Log.d(TAG, "ids are" + selectedList.get(i).getIdOnServer());
            }
        }

        Intent i = new Intent(getBaseContext(), NewJourneyDetail.class);
        startActivity(i);
    }

    @Override
    public void onResume(){
        List<Contact> allContacts = ContactDataSource.getAllContacts(this);
        adapter.updateList(allContacts);
        adapter.notifyDataSetChanged();
        super.onResume();
    }

}