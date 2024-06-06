package com.developerali.lshistutorial.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.developerali.lshistutorial.Models.QuestionModel;
import com.developerali.lshistutorial.R;
import com.developerali.lshistutorial.databinding.ActivityPlayQuizBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayQuizActivity extends AppCompatActivity {

    ActivityPlayQuizBinding binding;
    String apiKey;
    ArrayList<QuestionModel> questions;

    int index = 0;
    QuestionModel question;
    CountDownTimer timer, answerTimer;
    FirebaseDatabase data;
    String Path;

    MediaPlayer worng, right;
    int correctAnswers = 0;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiKey = getIntent().getStringExtra("apiKey");

        AlertDialog.Builder obj = new AlertDialog.Builder(PlayQuizActivity.this);
        obj.setTitle("Rules");
        obj.setMessage("1. Change Question by Next-btn." + "\n" +
                "2. 30 sec timer for each question." + "\n" +
                "3. Practice everyday for best performance!");
        obj.setIcon(R.drawable.error_24);
        obj.setCancelable(false);

        obj.setPositiveButton("START", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                obj.setCancelable(true);

                dialog = new ProgressDialog(PlayQuizActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("Connecting Server...");
                dialog.show();

                questions = new ArrayList<>();
                data = FirebaseDatabase.getInstance();


                // playing audio and vibration when user
                worng = MediaPlayer.create(PlayQuizActivity.this, R.raw.right);
                worng.setLooping(false);
                right = MediaPlayer.create(PlayQuizActivity.this, R.raw.right);
                right.setLooping(false);

                Path = getIntent().getStringExtra("path");

                readDataFromSheet();
            }
        });
        obj.show();





    }

    private void readDataFromSheet() {
        String url = apiKey + "action=read";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("items");
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);

                        String question = object.getString("question");
                        String optionA = object.getString("optionA");
                        String optionB = object.getString("optionB");
                        String optionC = object.getString("optionC");
                        String optionD = object.getString("optionD");
                        String answer = object.getString("answer");

                        questions.add(new QuestionModel(question, optionA, optionB, optionC, optionD, answer));
                    }

                    resetTimer();
                    setNextQuestion();
                    dialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PlayQuizActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PlayQuizActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        RequestQueue queue = Volley.newRequestQueue(PlayQuizActivity.this);
        queue.add(jsonObjectRequest);

    }
    void resetTimer() {
        timer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.timer.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                binding.option1.setEnabled(false);
                binding.option2.setEnabled(false);
                binding.option3.setEnabled(false);
                binding.option4.setEnabled(false);
                showAnswer();
            }
        };
    }

    void showAnswer() {
        if(question.getAnswer().equals(binding.option1.getText().toString()))
            binding.option1.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if(question.getAnswer().equals(binding.option2.getText().toString()))
            binding.option2.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if(question.getAnswer().equals(binding.option3.getText().toString()))
            binding.option3.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if(question.getAnswer().equals(binding.option4.getText().toString()))
            binding.option4.setBackground(getResources().getDrawable(R.drawable.option_right));
        try {
            worng = MediaPlayer.create(PlayQuizActivity.this, R.raw.wrong);
            worng.start();
        } catch(Exception e) { e.printStackTrace(); }
    }

    void setNextQuestion() {
        if(timer != null)
            timer.cancel();

        timer.start();
        if(index < questions.size()) {
            binding.questionCounter.setText(String.format("%d/%d", (index+1), questions.size()));
            question = questions.get(index);
            binding.question.setText("Q. " +question.getQuestion());
            binding.option1.setText(question.getOption1());
            binding.option2.setText(question.getOption2());
            binding.option3.setText(question.getOption3());
            binding.option4.setText(question.getOption4());
        }
    }

    void checkAnswer(String selectedAnswer, RadioButton radioButton) {

        binding.option1.setEnabled(false);
        binding.option2.setEnabled(false);
        binding.option3.setEnabled(false);
        binding.option4.setEnabled(false);

        if(selectedAnswer.equals(question.getAnswer())) {
            correctAnswers++;
            try {
                right = MediaPlayer.create(PlayQuizActivity.this, R.raw.right);
                right.start();
            } catch(Exception e) { e.printStackTrace(); }
            radioButton.setBackground(getResources().getDrawable(R.drawable.option_right));
        } else {
            showAnswer();
            radioButton.setBackground(getResources().getDrawable(R.drawable.option_wrong));
        }
    }

    void reset() {

        binding.option1.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option2.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option3.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option4.setBackground(getResources().getDrawable(R.drawable.option_unselected));

        binding.option1.setEnabled(true);
        binding.option2.setEnabled(true);
        binding.option3.setEnabled(true);
        binding.option4.setEnabled(true);

        binding.radioGroup.clearCheck();
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.option_1 || id == R.id.option_2 || id == R.id.option_3 || id == R.id.option_4) {
            if (timer != null)
                timer.cancel();

            if (binding.option1.isChecked()) {
                String SelectedAns = binding.option1.getText().toString();
                checkAnswer(SelectedAns, binding.option1);
            } else if (binding.option2.isChecked()) {
                String SelectedAns = binding.option2.getText().toString();
                checkAnswer(SelectedAns, binding.option2);
            } else if (binding.option3.isChecked()) {
                String SelectedAns = binding.option3.getText().toString();
                checkAnswer(SelectedAns, binding.option3);
            } else if (binding.option4.isChecked()) {
                String SelectedAns = binding.option4.getText().toString();
                checkAnswer(SelectedAns, binding.option4);
            } else {
                Toast.makeText(PlayQuizActivity.this, "Timed Out", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nextBtn) {
            if (answerTimer != null) {
                answerTimer.cancel();
            }

            reset();
            index++;


            if (index < questions.size()) {
                setNextQuestion();
            } else {
                Intent intent = new Intent(PlayQuizActivity.this, ResultActivity.class);
                intent.putExtra("correct", correctAnswers);
                intent.putExtra("total", questions.size());
                startActivity(intent);
                finish();
            }
        }
    }


}