package com.android.noteit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteDeleteListener, NoteAdapter.OnNoteEditListener {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private Button logoutButton;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private FloatingActionButton fabCreateNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        logoutButton = findViewById(R.id.logoutButton);
        recyclerView = findViewById(R.id.recyclerView);
        fabCreateNote = findViewById(R.id.fabCreateNote);

        // Set layout manager for RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize NoteAdapter
        noteAdapter = new NoteAdapter(new ArrayList<>(), this, this);

        // Set adapter for RecyclerView
        recyclerView.setAdapter(noteAdapter);

        // Set click listener for logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out user from Firebase Authentication
                mAuth.signOut();

                // Redirect to StartUpActivity
                startActivity(new Intent(MainActivity.this, StartUpActivity.class));
                finish(); // Close MainActivity
            }
        });

        // Set click listener for fabCreateNote
        fabCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CreateNoteActivity
                startActivity(new Intent(MainActivity.this, CreateNoteActivity.class));
            }
        });

        // Check if user is logged in
        if (mAuth.getCurrentUser() != null) {
            // If user is logged in, show logout button
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            // If user is not logged in, hide logout button
            logoutButton.setVisibility(View.GONE);
        }
        fetchNotesFromFirestore();
    }

    // Function to fetch notes from Firestore
    // Function to fetch notes from Firestore
    private void fetchNotesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the current user's email
        String userEmail = mAuth.getCurrentUser().getEmail();

        db.collection("notes")
                .whereEqualTo("userEmail", userEmail) // Query notes specific to user's email
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Note> notes = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Note note = document.toObject(Note.class);
                                note.setDocumentId(document.getId()); // Set the document ID for each note
                                notes.add(note);
                            }
                            // Update RecyclerView with fetched notes
                            noteAdapter.setNotes(notes); // Call setNotes method here
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onNoteDelete(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_delete_confirmation)
                .setTitle("Delete Confirmation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform deletion operation
                        deleteNoteFromFirestore(note);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onNoteEdit(Note note) {
        // Open EditNoteActivity and pass the note data
        Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
        intent.putExtra("note", note);
        startActivity(intent);
    }

    private void deleteNoteFromFirestore(Note note) {
        // Get reference to Firestore collection "notes"
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the Firestore collection for the document corresponding to the note
        db.collection("notes")
                .document(note.getDocumentId()) // Use the document ID to delete the specific note
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Delete successful, refetch notes
                        fetchNotesFromFirestore(); // Refetch notes
                        Toast.makeText(MainActivity.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Delete failed, log error
                        Log.e(TAG, "Error deleting note", e);
                        Toast.makeText(MainActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}