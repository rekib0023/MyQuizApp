package com.rekibahmed.myquizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCountDown;
    private TextView textViewCrctWrng;
    private TextView textViewSolution;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button btnConfirmNext;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCw;

    private List<Questions> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Questions currentQuestion;

    private int score;
    private boolean answered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion = findViewById(R.id.quiz_question);
        textViewScore = findViewById(R.id.quiz_score);
        textViewQuestionCount = findViewById(R.id.quiz_question_count);
        textViewCountDown = findViewById(R.id.quiz_count_down);
        textViewSolution = findViewById(R.id.quiz_solution);
        textViewCrctWrng = findViewById(R.id.crct_wrng);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_btn1);
        rb2 = findViewById(R.id.radio_btn2);
        rb3 = findViewById(R.id.radio_btn3);
        rb4 = findViewById(R.id.radio_btn4);
        btnConfirmNext = findViewById(R.id.quiz_btn_confirm_next);

        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCw = textViewCrctWrng.getTextColors();


        QuizDbHelper dbHelper = new QuizDbHelper(this);
        questionList = dbHelper.getAllQuestions();
        questionCounter = 0;
        questionCountTotal = questionList.size();
        Collections.shuffle(questionList);

        showNextQuestion();

        btnConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!answered){
                    if(rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()){
                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextQuestion();
                }
            }
        });

    }

    private void showNextQuestion(){
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rb4.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();

        if(questionCounter<questionCountTotal){
            currentQuestion = questionList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());

            questionCounter++;
            textViewQuestionCount.setText("Question: "+questionCounter+"/"+questionCountTotal);
            answered = false;
            textViewSolution.setText(" ");
            textViewCrctWrng.setText(" ");
            textViewCrctWrng.setTextColor(textColorDefaultCw);
            btnConfirmNext.setText("Confirm");
        } else {
            finishQuiz();
        }
    }

    private void checkAnswer(){
        answered = true;

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        showSolution();

        if(answerNr == currentQuestion.getAnswerNr()){
            score++;
            textViewScore.setText("Score: " + score);
            textViewCrctWrng.setText("Correct answer");
            textViewCrctWrng.setTextColor(Color.GREEN);
            textViewSolution.setText(" ");
        } else{
            textViewCrctWrng.setText("Wrong answer");
            textViewCrctWrng.setTextColor(Color.RED);
        }

    }

    private void showSolution(){

        switch (currentQuestion.getAnswerNr()){
            case 1:
                textViewSolution.setText("Option 1 is correct");
                break;
            case 2:
                textViewSolution.setText("Option 2 is correct");
                break;
            case 3:
                textViewSolution.setText("Option 3 is correct");
                break;
            case 4:
                textViewSolution.setText("Option 4 is correct");
                break;
        }

        if(questionCounter < questionCountTotal){
            btnConfirmNext.setText("Next");
        } else {
            btnConfirmNext.setText("Finish");
        }
    }

    private void finishQuiz(){
        finish();
    }
}
