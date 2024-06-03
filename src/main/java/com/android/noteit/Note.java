package com.android.noteit;

import java.io.Serializable;

public class Note implements Serializable {
    private String documentId; // New field to store document ID
    private String title;
    private String content;

    public Note() {
        // Default constructor required for Firestore
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}