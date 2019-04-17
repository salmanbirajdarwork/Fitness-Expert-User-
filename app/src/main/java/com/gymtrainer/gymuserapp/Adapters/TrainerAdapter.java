package com.gymtrainer.gymuserapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gymtrainer.gymuserapp.Activities.TrainersActivity;
import com.gymtrainer.gymuserapp.Model.Trainer;
import com.gymtrainer.gymuserapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by haroonpc on 3/16/2019.
 */

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.TrainerAdapterViewHolder>
{

    ArrayList<Trainer> arrayListTrainers;
    Context context;

    public TrainerAdapter(Context context,ArrayList<Trainer> arrayListTrainers)
    {
        this.context = context;
        this.arrayListTrainers = arrayListTrainers;
    }



    @Override
    public TrainerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
     View view = LayoutInflater.from(context).inflate(R.layout.item_trainers,parent,false);
        return new TrainerAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(TrainerAdapterViewHolder holder, int position) {
            Trainer trainer = arrayListTrainers.get(position);
        holder.trainerTextViewName.setText(trainer.getName());
        Picasso.get().load(trainer.getImageUrl()).placeholder(R.drawable.ic_launcher_man).into(holder.circleImageViewTrainer);

    }

    @Override
    public int getItemCount() {
        return arrayListTrainers.size();
    }

    public class TrainerAdapterViewHolder extends RecyclerView.ViewHolder
    {
        TextView trainerTextViewName;
        CircleImageView circleImageViewTrainer;


        public TrainerAdapterViewHolder(View itemView) {
            super(itemView);
            trainerTextViewName = (TextView)itemView.findViewById(R.id.nameTxt);
            circleImageViewTrainer = (CircleImageView)itemView.findViewById(R.id.trainerImg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TrainersActivity)context).openTrainerDetail(arrayListTrainers,getAdapterPosition());

                }
            });
        }
    }
}
