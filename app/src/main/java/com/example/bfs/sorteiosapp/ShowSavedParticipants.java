package com.example.bfs.sorteiosapp;

import android.app.Dialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class ShowSavedParticipants extends AppCompatActivity {

    private ListView lView;
    private ArrayList<Participant> table;
    private PartListAdapter adapter;
    private ParticipantDataBase db;
    private Dialog dialog;

    void loadTable() {

        table = new ArrayList<Participant>();

        Cursor ptr = db.getAllData();
        if(ptr.getCount() == 0) {
            return;
        }

        StringBuffer buffer = new StringBuffer();

        String name;
        int prob, skill, id;
        while(ptr.moveToNext()) {
            id = Integer.parseInt(ptr.getString(0));
            name = ptr.getString(1);
            prob = Integer.parseInt(ptr.getString(2));
            skill = Integer.parseInt(ptr.getString(3));

            table.add(new Participant(id, name, prob, skill));
        }
        adapter = new PartListAdapter(this, R.layout.participants_list_view, table, 99);
        lView.setAdapter(adapter);
    }

    void makeAttributions() {
        lView = (ListView)findViewById(R.id.listView);
        db = new ParticipantDataBase(ShowSavedParticipants.this);
    }

    void updateElementFromDialog(int index, int id, String prevName, int prevProb, int prevSkill) {
        final EditText writeName = (EditText) dialog.findViewById(R.id.writeName);
        final EditText writeProb = (EditText) dialog.findViewById(R.id.writeProb);
        final EditText writeSkill = (EditText) dialog.findViewById(R.id.writeSkill);

        String name = prevName;
        int prob = prevProb;
        int skill = prevSkill;

        // DONT FORGET TO CHECK IF NAME IS EMPTY

        name = writeName.getText().toString();
        prob = Integer.parseInt(checkStringEmpty(writeProb.getText().toString()));
        skill = Integer.parseInt(checkStringEmpty(writeSkill.getText().toString()));
        table.set(index, new Participant(name, prob, skill));
        boolean updated = db.updateData(id, name, prob, skill);
        if(updated == true)
            Toast.makeText(ShowSavedParticipants.this,"Data Update",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(ShowSavedParticipants.this,"Data not Updated",Toast.LENGTH_LONG).show();
        adapter.notifyDataSetChanged();
    }

    String checkIfNumberIsMinusOne(String x) {
        if(x.equals("-1"))
            return "";
        return x;
    }

    String checkStringEmpty(String x) {
        if(x.equals(""))
            return "-1";
        return x;
    }

    void removeElementFromDialog(final int index, final int id) {
        table.remove(index);
        db.deleteData(id);
        adapter.notifyDataSetChanged();
    }

    public void actionUpdateParticipant(final int id, final String oldName, final String oldProb, final String oldSkill, final int index){

        dialog = new Dialog(ShowSavedParticipants.this);

        dialog.setContentView(R.layout.dialog_template);
        TextView txtMessage=(TextView)dialog.findViewById(R.id.message);
        txtMessage.setText("Update item");

        Button butAdd = (Button)dialog.findViewById(R.id.butAdd);
        butAdd.setText("UPDATE");

        final EditText writeName = (EditText)dialog.findViewById(R.id.writeName);
        final EditText writeProb = (EditText)dialog.findViewById(R.id.writeProb);
        final EditText writeSkill = (EditText)dialog.findViewById(R.id.writeSkill);

        writeName.setText(checkIfNumberIsMinusOne(oldName));
        writeProb.setText(checkIfNumberIsMinusOne(oldProb));
        writeSkill.setText(checkIfNumberIsMinusOne(oldSkill));

        Button butRemove = (Button)dialog.findViewById(R.id.butRemove);

        butAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateElementFromDialog(index, id, oldName, Integer.parseInt(oldProb), Integer.parseInt(oldSkill));
                dialog.dismiss();
            }
        });

        butRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeElementFromDialog(index, id);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void updateElementListView() {

        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // Show input box
            Participant aux = table.get(position);
            actionUpdateParticipant(aux.getId(), aux.getName(), Integer.toString(aux.getProb()), Integer.toString(aux.getSkill()), position);

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_participants);

        makeAttributions();
        updateElementListView();

        loadTable();
    }
}
