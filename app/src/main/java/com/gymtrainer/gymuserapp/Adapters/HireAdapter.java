package com.gymtrainer.gymuserapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gymtrainer.gymuserapp.Activities.HiredTrainersActivity;
import com.gymtrainer.gymuserapp.Model.Hire;
import com.gymtrainer.gymuserapp.Model.Trainer;
import com.gymtrainer.gymuserapp.Model.User;
import com.gymtrainer.gymuserapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HireAdapter extends RecyclerView.Adapter<HireAdapter.HireAdapterViewHolder>
{

    ArrayList<Hire> hireList;
    Context context;


    public HireAdapter(ArrayList<Hire> hireList, Context context)
    {
        this.context = context;
        this.hireList = hireList;
    }

    @Override
    public HireAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hired_trainers,parent,false);
        return new HireAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HireAdapterViewHolder holder, int position) {
                Hire hire = hireList.get(position);
                holder.textViewName.setText(hire.getTrainerName());
                Picasso.get().load(hire.getImageUrl()).placeholder(R.drawable.ic_launcher_man).into(holder.hiredImg);


    }

    @Override
    public int getItemCount() {
        return hireList.size();
    }

    public class HireAdapterViewHolder extends RecyclerView.ViewHolder
    {
            TextView textViewName,textViewRate,textViewCategory;
            CircleImageView hiredImg;

            public HireAdapterViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView)itemView.findViewById(R.id.hiredTrainerName);
            hiredImg = (CircleImageView)itemView.findViewById(R.id.hiredTrainerImg);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Hire hire = hireList.get(getAdapterPosition());
                    ((HiredTrainersActivity)context).openNewActivity(hire.getTrainerId(),hire.getHourList(),hire.getCategoryName(),hire.getUserId(),hire.getDate());

                }
            });
        }
    }
}
