package com.quizeria.model;

import jakarta.persistence.*;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quizId;

    @Column(length = 1000)
    private String feedbackText;

    public Feedback() {}

    public Feedback(Long quizId, String feedbackText) {
        this.quizId = quizId;
        this.feedbackText = feedbackText;
    }

    public Long getId() { return id; }
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
}
