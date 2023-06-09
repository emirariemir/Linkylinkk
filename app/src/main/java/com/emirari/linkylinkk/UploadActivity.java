package com.emirari.linkylinkk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.emirari.linkylinkk.databinding.ActivityUploadBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UploadActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private ActivityUploadBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setStatusBarColor();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        binding.tagTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // empty stuff here.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    String filteredText = s.toString().replace(" ", ""); // Remove whitespace characters
                    binding.tagTextField.setText(filteredText); // Update the text in the EditText
                    binding.tagTextField.setSelection(filteredText.length()); // Set the cursor position at the end
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // empty stuff here too.
            }
        });

    }

    public void setStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(UploadActivity.this, R.color.custom_statusbar_color)); // finally change the color
    }

    public void publishPressed(View view) {

        FirebaseUser user = auth.getCurrentUser();

        String userMail = user.getEmail();
        String title = binding.titleTextField.getText().toString();
        String link = binding.linkTextField.getText().toString();
        String tag = binding.tagTextField.getText().toString();

        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("userMail", userMail);
        postMap.put("title", title);
        postMap.put("link", link);
        postMap.put("tag", tag);
        postMap.put("date", FieldValue.serverTimestamp());
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
        final String strDate = formatter.format(date);

        postMap.put("strDate", strDate); // Put everything here.

        firestore.collection("Links").add(postMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Intent intent = new Intent(UploadActivity.this, FeedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}