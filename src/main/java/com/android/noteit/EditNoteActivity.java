package com.android.noteit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button saveButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Note note; // Added to store the note being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Retrieve the note object passed from the MainActivity
        note = (Note) getIntent().getSerializableExtra("note");

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        saveButton = findViewById(R.id.saveButton);

        // Populate the EditText fields with existing note data
        titleEditText.setText(note.getTitle());
        contentEditText.setText(note.getContent());

        // Set click listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the note in Firestore
                updateNoteInFirestore();
            }
        });
    }

    // Method to update the note in Firestore
    private void updateNoteInFirestore() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        // Check if title and content are not empty
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(EditNoteActivity.this, "Please enter title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user's email
        String userEmail = mAuth.getCurrentUser().getEmail();

        // Create a new note map
        Map<String, Object> updatedNote = new HashMap<>();
        updatedNote.put("title", title);
        updatedNote.put("content", content);
        updatedNote.put("userEmail", userEmail); // Add user's email to note

        // Update the note in Firestore
        db.collection("notes")
                .document(note.getDocumentId()) // Use document ID of the note to update
                .update(updatedNote)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Note updated successfully
                        Toast.makeText(EditNoteActivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update note
                        Toast.makeText(EditNoteActivity.this, "Failed to update note", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}