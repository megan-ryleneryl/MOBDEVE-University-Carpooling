package com.example.uniride;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyBookingHomeAdapter extends RecyclerView.Adapter<MyBookingHomeAdapter.ViewHolder> {

    BookingModel[] myBookingData;
    Context context;

    public MyBookingHomeAdapter(BookingModel[] myMovieData,BookingHomeActivity activity) {
        this.myBookingData = myBookingData;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.booking_home_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BookingModel myBookingDataList = myBookingData[position];
//        holder.textViewName.setText(myMovieDataList.getMovieName());
//        holder.textViewDate.setText(myMovieDataList.getMovieDate());
//        holder.movieImage.setImageResource(myMovieDataList.getMovieImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "It worked", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, BookingHomeDetailsActivity.class);
//                i.putExtra("image",myMovieDataList.getMovieImage());
//                i.putExtra("name",myMovieDataList.getMovieName());
//                i.putExtra("date",myMovieDataList.getMovieDate());
//                i.putExtra("summary",myMovieDataList.getMovieSummary());

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myBookingData.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
//        ImageView movieImage;
//        TextView textViewName;
//        TextView textViewDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            movieImage = itemView.findViewById(R.id.imageview);
//            movieImage = itemView.findViewById(R.id.imageview);
//            textViewName = itemView.findViewById(R.id.textName);
//            textViewDate = itemView.findViewById(R.id.textdate);
        }
    }
}
