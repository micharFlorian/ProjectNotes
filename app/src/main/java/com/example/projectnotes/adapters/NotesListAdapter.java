package com.example.projectnotes.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.projectnotes.R;
import com.example.projectnotes.pojos.Note;

import java.util.List;

import androidx.annotation.NonNull;


public class NotesListAdapter extends ArrayAdapter<Note> {

    private Context context;

    public NotesListAdapter(@NonNull Context context, int resource, @NonNull List<Note> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        Note note = (Note) getItem(i);
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_item, null);
        }
        TextView textViewId = (TextView) v.findViewById(R.id.textViewId);
        TextView textViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
        TextView textViewDescription = (TextView) v.findViewById(R.id.textViewDescription);
        if (note.getNoteId() != null) {
            textViewId.setText(note.getNoteId().toString());
            textViewTitle.setText(note.getNoteId().toString());
//            textViewTitle.setText(note.getTitle());
            textViewDescription.setText(note.getDescription());
        } else {
            textViewTitle.setText(note.getTitle());
            textViewDescription.setText(note.getDescription());
        }
        return v;
    }
}
