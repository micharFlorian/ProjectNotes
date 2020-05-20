package com.example.projectnotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.projectnotes.R;
import com.example.projectnotes.pojos.Note;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class NotesGridAdapter extends ArrayAdapter<Note> {
    public NotesGridAdapter(@NonNull Context context, int resource, @NonNull List<Note> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.gridview_item, null);
        }
        Note note = (Note)getItem(i);
        TextView textViewTitle = (TextView) v.findViewById(R.id.textViewTitleGrid);
        TextView textViewDescription = (TextView) v.findViewById(R.id.textViewDescriptionGrid);
        textViewTitle.setText(note.getTitle());
        textViewDescription.setText(note.getDescription());
        return v;
    }
}
