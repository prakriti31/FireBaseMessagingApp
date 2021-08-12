package com.codewithharry.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.codewithharry.firebase.adapters.AdapterPost;
import com.codewithharry.firebase.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TheirProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    //View from xml;
    private ImageView avatarTv, coverTv;
    private TextView nameTv, emailTv, phoneTv;

    RecyclerView postsRecyclerView;

    ArrayList<ModelPost> postList;
    AdapterPost adapterPost;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_their_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Init views
        avatarTv = findViewById(R.id.avatarTv);
        coverTv = findViewById(R.id.coverTv);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);

        postsRecyclerView = findViewById(R.id.recyclerView_posts);

        firebaseAuth = FirebaseAuth.getInstance();

        //get uid of current user to retrieve poss
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Check until required data is fetched
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Get Data
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    //Set Data
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);
                    try {
                        //If image is received then set
                        Picasso.get().load(image).into(avatarTv);
                    } catch (Exception e) {
                        //If there is any exception while getting image then set default
                        Picasso.get().load(R.drawable.ic_default_img_white).into(avatarTv);
                    }
                    try {
                        //If image is received then set
                        Picasso.get().load(cover).into(coverTv);
                    } catch (Exception e) {
                        //If there is any exception while getting image then set default
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postList = new ArrayList<>();

        checkUserStatus();

        loadUsersPosts();

    }

    private void loadUsersPosts() {
        //linear layout for recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(TheirProfileActivity.this);
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerView
        postsRecyclerView.setLayoutManager(layoutManager);

        //init posts list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        /*when ever user publishes a post the uid of this user is also
         * saved as info of the post
         * So we are retrieving posts having uid equals the uid of current user*/
        Query query = ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    //add to list
                    postList.add(myPosts);

                    //adapter
                    adapterPost = new AdapterPost(TheirProfileActivity.this, postList);
                    //set this adapter to recycler view
                    postsRecyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TheirProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void searchUsersPosts(final String searchQuery) {
        //linear layout for recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(TheirProfileActivity.this);
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerView
        postsRecyclerView.setLayoutManager(layoutManager);

        //init posts list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        /*when ever user publishes a post the uid of this user is also
         * saved as info of the post
         * So we are retrieving posts having uid equals the uid of current user*/
        Query query = ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    if (myPosts.getpTitle().toLowerCase().contains(searchQuery.toLowerCase())
                            || myPosts.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())) {
                        //add to list
                        postList.add(myPosts);
                    }

                    //adapter
                    adapterPost = new AdapterPost(TheirProfileActivity.this, postList);
                    //set this adapter to recycler view
                    postsRecyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TheirProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    private void checkUserStatus() {
        //Get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //User is signed in stay here
            //set email of logged in user
            //mProfileTv.setText(user.getEmail());
        } else {
            //User not signed in go to main activity
            startActivity(new Intent(this, MainActivity.class));
           finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_add_post).setVisible(false);

        MenuItem item = menu.findItem(R.id.action_search);
        //v7 search view to search specific posts
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when user presses search button
                //if search query is not empty then search
                if(!TextUtils.isEmpty(query.trim())){
                    searchUsersPosts(query);
                } else {
                    //search text is empty
                    loadUsersPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //called when user types any letter
                if(!TextUtils.isEmpty(query.trim())){
                    searchUsersPosts(query);
                } else {
                    //search text is empty
                    loadUsersPosts();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }

}