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

public class CreateNoteActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button saveButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        saveButton = findViewById(R.id.saveButton);

        // Set click listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the note to Firestore
                saveNoteToFirestore();
            }
        });
    }

    // Method to save the note to Firestore
    private void saveNoteToFirestore() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        // Check if title and content are not empty
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(CreateNoteActivity.this, "Please enter title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user's email
        String userEmail = mAuth.getCurrentUser().getEmail();

        // Create a new note map
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("content", content);
        note.put("userEmail", userEmail); // Add user's email to note

        // Add the note to Firestore
        db.collection("notes")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Note added successfully
                        Toast.makeText(CreateNoteActivity.this, "Note saved successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add note
                        Toast.makeText(CreateNoteActivity.this, "Failed to save note", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}