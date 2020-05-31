package com.example.projectnotes.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.projectnotes.R;
import com.example.projectnotes.pojos.Note;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Clase que nos permite convertir un ArrayList<Note> en un ListView.
 */
public class NotesListAdapter extends ArrayAdapter<Note> {

    private Context context;

    /**
     * Método constructor de la clase.
     */
    public NotesListAdapter(@NonNull Context context, int resource, @NonNull List<Note> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    /**
     * Método en el que se obtiene un Objeto de la clase Note y se meten sus datos en el item del
     * ListView.
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        Note note = (Note) getItem(i);
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_item, null);
        }
        TextView textViewId = (TextView) v.findViewById(R.id.textViewId);
        TextView textViewUserId = (TextView) v.findViewById(R.id.textViewUserId);
        TextView textViewEncode = (TextView) v.findViewById(R.id.textViewEncode);
        TextView textViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
        TextView textViewDescription = (TextView) v.findViewById(R.id.textViewDescription);
        switch (note.getEncode()) {
            case 0:
                textViewId.setText(note.getNoteId().toString());
                textViewUserId.setText(note.getUserId().getUserId().toString());
                textViewEncode.setText(note.getEncode().toString());
                textViewTitle.setText(note.getTitle());
                textViewDescription.setText(note.getDescription());
                break;

            case 1:
                textViewId.setText(note.getNoteId().toString());
                textViewUserId.setText(note.getUserId().getUserId().toString());
                textViewEncode.setText(note.getEncode().toString());
                textViewTitle.setText(note.getTitle());
                break;
        }
        return v;
    }
}
