package com.rekibahmed.myquizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {
    public static final String EXTRA_SCORE = "extraScore";
    private static final long COUNTDOWN_IN_MILLIS = 30000;

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCategoryDifficulty;
    private TextView textViewCountDown;
    private TextView textViewCrctWrng;
    private TextView textViewSolution;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button btnConfirmNext;

    private  ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private List<Questions> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Questions currentQuestion;

    private int score;
    private boolean answered;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion = findViewById(R.id.quiz_question);
        textViewScore = findViewById(R.id.quiz_score);
        textViewQuestionCount = findViewById(R.id.quiz_question_count);
        textViewCategoryDifficulty= findViewById(R.id.quiz_category_difficulty);
        textViewCountDown = findViewById(R.id.quiz_count_down);
        textViewSolution = findViewById(R.id.quiz_solution);
        textViewCrctWrng = findViewById(R.id.crct_wrng);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_btn1);
        rb2 = findViewById(R.id.radio_btn2);
        rb3 = findViewById(R.id.radio_btn3);
        rb4 = findViewById(R.id.radio_btn4);
        btnConfirmNext = findViewById(R.id.quiz_btn_confirm_next);

        textColorDefaultCd = textViewCountDown.getTextColors();

        Intent intent = getIntent();
        int categoryID = intent.getIntExtra(MainActivity.EXTRA_CATEGORY_ID, 0);
        String categoryName = intent.getStringExtra(MainActivity.EXTRA_CATEGORY_NAME);
        String difficulty = intent.getStringExtra(MainActivity.EXTRA_DIFFICULTY);

        textViewCategoryDifficulty.setText(categoryName + " : " + difficulty);

//        QuizDbHelper dbHelper = new QuizDbHelper(this);
        QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
        questionList = dbHelper.getQuestions(categoryID, difficulty);
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
            btnConfirmNext.setText("Confirm");

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();
        } else {
            finishQuiz();
        }
    }

    private void startCountDown(){
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownText(){
        int minute = (int) (timeLeftInMillis / 1000) / 60;
        int second = (int) (timeLeftInMillis / 1000) % 60;


        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minute, second);

        textViewCountDown.setText(timeFormatted);

        if(timeLeftInMillis<10000){
            textViewCountDown.setTextColor(Color.RED);
        } else{
            textViewCountDown.setTextColor(textColorDefaultCd);
        }
    }

    private void checkAnswer(){
        answered = true;

        countDownTimer.cancel();
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
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(backPressedTime+2000 > backPressedTime){
            finishQuiz();
        } else{
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}
