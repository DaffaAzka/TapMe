package com.dest.tapme.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dest.tapme.OtherProfileActivity;
import com.dest.tapme.R;
import com.dest.tapme.models.Medal;
import com.dest.tapme.models.Record;
import com.dest.tapme.models.User;
import com.dest.tapme.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private Context context;
    private List<Record> records;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public RankAdapter(Context context, List<Record> records) {
        Collections.reverse(records);
        this.context = context;
        this.records = records;
    }

    @NonNull
    @Override
    public RankAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rank_item, parent, false);
        return new RankAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankAdapter.ViewHolder holder, int position) {
        final Record record = records.get(position);

        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Users")
                .child(record.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                holder.tv_rank_item_username.setText(user.getUsername());
                holder.tv_rank_item_score.setText("" + record.getScore());

                if (user.getProfileImg().equals("default")) {
                    holder.img_rank_item_profile_picture.setImageResource(R.drawable.ic_default_profile);
                } else {
                    Picasso.get().load(user.getProfileImg()).into(holder.img_rank_item_profile_picture);
                }

                if (user.equals(firebaseUser.getUid())) {

                } else {
                    holder.img_rank_item_profile_picture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                                    .edit().putString("profileId", user.getId()).apply();

                            context.startActivity(new Intent(context, OtherProfileActivity.class));
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (position == 0) {
            FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Medals")
                    .child(record.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Medal medal = snapshot.getValue(Medal.class);

                    if (medal.getMedalKing().equals("claimed")) {

                    } else {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("medalKing", "claim");
                        FirebaseDatabase.getInstance(Util.realtimeDBLink()).getReference().child("Medals")
                                .child(firebaseUser.getUid()).updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {

        if (records.size() < 10) {
            return records.size();
        } else {
            return 10;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView img_rank_item_profile_picture;
        public TextView tv_rank_item_username, tv_rank_item_score;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_rank_item_profile_picture = itemView.findViewById(R.id.img_rank_item_profile_picture);
            tv_rank_item_username = itemView.findViewById(R.id.tv_rank_item_username);
            tv_rank_item_score = itemView.findViewById(R.id.tv_rank_item_score);
        }
    }
}
