package com.android.noteit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private OnNoteDeleteListener deleteListener;
    private OnNoteEditListener editListener; // Added

    public NoteAdapter(List<Note> notes, OnNoteDeleteListener deleteListener, OnNoteEditListener editListener) {
        this.notes = notes;
        this.deleteListener = deleteListener;
        this.editListener = editListener; // Initialized
    }

    public interface OnNoteDeleteListener {
        void onNoteDelete(Note note);
    }

    public interface OnNoteEditListener {
        void onNoteEdit(Note note); // Added method
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView contentTextView;
        private ImageButton deleteButton;
        private ImageButton editButton; // Added

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.previewTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton); // Initialized

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Note note = notes.get(position);
                        deleteListener.onNoteDelete(note);
                    }
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Note note = notes.get(position);
                        editListener.onNoteEdit(note); // Added callback
                    }
                }
            });
        }

        public void bind(Note note) {
            titleTextView.setText(note.getTitle());
            contentTextView.setText(note.getContent());
        }
    }
}