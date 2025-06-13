package com.quizeria.controller;

import com.quizeria.model.Quiz;
import com.quizeria.model.Question;
import com.quizeria.model.Feedback;
import com.quizeria.repository.QuizRepository;
import com.quizeria.repository.QuestionRepository;
import com.quizeria.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class StudentController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Student panel: list all quizzes
    @GetMapping("/student")
    public String studentPanel(Model model) {
        List<Quiz> quizzes = quizRepository.findAll();
        model.addAttribute("quizzes", quizzes);
        return "student";
    }

    // Show quiz questions
    @GetMapping("/student/quiz/{quizId}")
    public String takeQuiz(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) return "redirect:/student";
        List<Question> questions = questionRepository.findByQuizId(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        return "take-quiz";
    }

    // Handle quiz submission and show result
    @PostMapping("/student/quiz/{quizId}/submit")
    public String submitQuiz(@PathVariable Long quizId, @RequestParam Map<String, String> params, Model model) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        List<Question> questions = questionRepository.findByQuizId(quizId);
        int score = 0;
        for (Question q : questions) {
            String answer = params.get("question_" + q.getId());
            if (answer != null && answer.equalsIgnoreCase(q.getCorrectOption())) {
                score++;
            }
        }
        model.addAttribute("quiz", quiz);
        model.addAttribute("score", score);
        model.addAttribute("total", questions.size());
        return "quiz-result";
    }

    // Handle feedback submission
    @PostMapping("/student/quiz/{quizId}/feedback")
    public String submitFeedback(@PathVariable Long quizId, @RequestParam("feedback") String feedback, Model model) {
        Feedback fb = new Feedback(quizId, feedback);
        feedbackRepository.save(fb);
        model.addAttribute("message", "Thank you for your feedback!");
        return "feedback-success";
    }
}
