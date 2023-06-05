package com.emirari.linkylinkk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.emirari.linkylinkk.databinding.ActivityFeedBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private ActivityFeedBinding binding;

    PostAdopter feedRecyclerAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    ArrayList<Post> posts;

    ArrayList<Post> allPosts;
    ArrayList<Post> filteredPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setStatusBarColor();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toolbar.setSubtitle("Hello, " + mAuth.getCurrentUser().getEmail());

        posts = new ArrayList<Post>();

        allPosts = new ArrayList<Post>();
        filteredPosts = new ArrayList<Post>();

        getPosts();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerAdapter = new PostAdopter(FeedActivity.this, filteredPosts);
        binding.recyclerView.setAdapter(feedRecyclerAdapter);

        setupTagSearch();
    }

    public void setStatusBarColor() {
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(FeedActivity.this, R.color.custom_statusbar_color));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addLink) {
            Intent intent = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.signOut) {
            mAuth.signOut();
            Intent intent = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    private void getPosts() {
        firestore.collection("Links").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                if (value != null) {
                    allPosts.clear();
                    filteredPosts.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Map<String, Object> post = doc.getData();
                        String userMail = (String) post.get("userMail");
                        String title = (String) post.get("title");
                        String link = (String) post.get("link");
                        String tag = (String) post.get("tag");
                        Post postObject = new Post(userMail, title, link, tag);
                        posts.add(postObject);
                        allPosts.add(postObject);
                    }
                    filteredPosts.addAll(allPosts);
                    feedRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void filterPosts(String tag) {
        filteredPosts.clear();

        for (Post post : allPosts) {
            if (post.getTag().equals(tag)) {
                filteredPosts.add(post);
            }
        }

        feedRecyclerAdapter.notifyDataSetChanged();
    }

    // Call this method when the user performs a search
    private void performSearch(String tag) {
        if (tag.isEmpty()) {
            // If the search tag is empty, display all posts
            filteredPosts.clear();
            filteredPosts.addAll(allPosts);
        } else {
            // Filter posts based on the search tag
            filterPosts(tag);
        }

        feedRecyclerAdapter.notifyDataSetChanged();
    }


    private void setupTagSearch() {
        binding.tagSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String searchTag = binding.tagSearchEditText.getText().toString();
                performSearch(searchTag);
                return true;
            }
            return false;
        });
    }
}