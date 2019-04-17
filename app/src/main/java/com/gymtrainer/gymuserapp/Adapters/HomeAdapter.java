package com.gymtrainer.gymuserapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.gymtrainer.gymuserapp.Activities.HomeActivity;
import com.gymtrainer.gymuserapp.Model.Category;
import com.gymtrainer.gymuserapp.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by haroonpc on 3/14/2019.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeAdapterViewHolder>
{
    Context context;
    ArrayList<Category> arrayListCategories;

    public HomeAdapter(Context context, ArrayList<Category> arrayListCategories)
    {
        this.context = context;
        this.arrayListCategories = arrayListCategories;

    }

    @Override
    public HomeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categories,parent,false);
        return new HomeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeAdapterViewHolder holder, int position) {
        Category category = arrayListCategories.get(position);
        holder.textViewName.setText(category.getCategoryName());
        Picasso.get().load(category.getCategoryImageUrl()).placeholder(R.drawable.placeholderitem).into(holder.circleImageView);

    }

    @Override
    public int getItemCount() {
        return arrayListCategories.size();
    }

    public class HomeAdapterViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewName;
        ImageView circleImageView;


        public HomeAdapterViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView)itemView.findViewById(R.id.textCategoryName);
            circleImageView = (ImageView) itemView.findViewById(R.id.textCategoryImage);

           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   ((HomeActivity)context).openRelevantTrainers(getAdapterPosition(),arrayListCategories);
               }
           });

        }
    }
}
