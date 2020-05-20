package com.example.projectnotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.projectnotes.R;
import com.example.projectnotes.pojos.Note;

import java.util.ArrayList;

public class NotesListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Note> listNotes;

    public NotesListAdapter(Context context, ArrayList<Note> listNotes) {
        this.context = context;
        this.listNotes = listNotes;
    }

    @Override
    public int getCount() {
        if (listNotes != null) return listNotes.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return listNotes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Note note = (Note)getItem(i);
        view = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
        textViewTitle.setText(note.getTitle());
        textViewDescription.setText(note.getDescription());
        return view;
    }
}
