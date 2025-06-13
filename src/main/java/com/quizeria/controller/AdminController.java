package com.quizeria.controller;
import org.springframework.security.core.Authentication;
import com.quizeria.model.Quiz;
import com.quizeria.model.Question;
import com.quizeria.model.Feedback;
import com.quizeria.repository.QuizRepository;
import com.quizeria.repository.QuestionRepository;
import com.quizeria.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping
    public String adminPanel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities: " + auth.getAuthorities());
        List<Quiz> quizzes = quizRepository.findAll();
        model.addAttribute("quizzes", quizzes);
        return "admin";
    }

    @GetMapping("/add")
    public String showAddQuizForm(Model model) {
        model.addAttribute("quiz", new Quiz());
        return "add-quiz";
    }

    @PostMapping("/add")
    public String addQuiz(@ModelAttribute Quiz quiz) {
        quizRepository.save(quiz);
        return "redirect:/admin";
    }

    @GetMapping("/quiz/{quizId}/edit")
    public String showEditQuizForm(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) return "redirect:/admin";
        model.addAttribute("quiz", quiz);
        return "edit-quiz";
    }

    @PostMapping("/quiz/{quizId}/edit")
    public String editQuiz(@PathVariable Long quizId, @ModelAttribute Quiz quiz) {
        Quiz existingQuiz = quizRepository.findById(quizId).orElse(null);
        if (existingQuiz != null) {
            existingQuiz.setTitle(quiz.getTitle());
            quizRepository.save(existingQuiz);
        }
        return "redirect:/admin";
    }

    @GetMapping("/quiz/{quizId}/delete")
    public String deleteQuiz(@PathVariable Long quizId) {
        quizRepository.deleteById(quizId);
        return "redirect:/admin";
    }

    @GetMapping("/quiz/{quizId}/questions")
    public String viewQuestions(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return "redirect:/admin";
        }
        List<Question> questions = questionRepository.findByQuizId(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        return "questions";
    }

    @GetMapping("/quiz/{quizId}/questions/add")
    public String showAddQuestionForm(@PathVariable Long quizId, Model model) {
        Question question = new Question();
        question.setQuiz(quizRepository.findById(quizId).orElse(null));
        model.addAttribute("question", question);
        model.addAttribute("quizId", quizId);
        return "add-question";
    }

    @PostMapping("/quiz/{quizId}/questions/add")
    public String addQuestion(@PathVariable Long quizId, @ModelAttribute Question question) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz != null) {
            question.setQuiz(quiz);
            questionRepository.save(question);
        }
        return "redirect:/admin/quiz/" + quizId + "/questions";
    }

    @GetMapping("/quiz/{quizId}/questions/{questionId}/edit")
    public String showEditQuestionForm(@PathVariable Long quizId, @PathVariable Long questionId, Model model) {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) return "redirect:/admin/quiz/" + quizId + "/questions";
        model.addAttribute("question", question);
        model.addAttribute("quizId", quizId);
        return "edit-question";
    }

    @PostMapping("/quiz/{quizId}/questions/{questionId}/edit")
    public String editQuestion(@PathVariable Long quizId, @PathVariable Long questionId, @ModelAttribute Question question) {
        Question existingQuestion = questionRepository.findById(questionId).orElse(null);
        if (existingQuestion != null) {
            existingQuestion.setText(question.getText());
            existingQuestion.setOptionA(question.getOptionA());
            existingQuestion.setOptionB(question.getOptionB());
            existingQuestion.setOptionC(question.getOptionC());
            existingQuestion.setOptionD(question.getOptionD());
            existingQuestion.setCorrectOption(question.getCorrectOption());
            questionRepository.save(existingQuestion);
        }
        return "redirect:/admin/quiz/" + quizId + "/questions";
    }

    @GetMapping("/quiz/{quizId}/questions/{questionId}/delete")
    public String deleteQuestion(@PathVariable Long quizId, @PathVariable Long questionId) {
        questionRepository.deleteById(questionId);
        return "redirect:/admin/quiz/" + quizId + "/questions";
    }

    @GetMapping("/feedback")
    public String viewFeedback(Model model) {
        List<Feedback> feedbackList = feedbackRepository.findAll();
        model.addAttribute("feedbackList", feedbackList);
        return "admin-feedback";
    }
}
