package com.codineasy.wip;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
    private Context mContext;


    public RecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        if(!WipGlobals.isShowingDirection) {
            viewHolder.column1.setText(String.valueOf(WipGlobals.details.get(WipGlobals.detailsIndex.get()).get(i).getWeather().temperature()) + "°C");
            viewHolder.column2.setText(WipGlobals.details.get(WipGlobals.detailsIndex.get()).get(i).getWeather().summary());

            StringBuilder sb = new StringBuilder();
            long sec = WipGlobals.details.get(WipGlobals.detailsIndex.get()).get(i).getTimeToArrive();
            if (sec > 60) {
                long min = sec / 60;
                sec = sec - min * 60;
                if (min > 60) {
                    long hrs = min / 60;
                    min = min - hrs * 60;
                    sb.append(hrs + "h" + min + "m");
                } else {
                    sb.append(min + "m" + sec + "s");
                }
            } else {
                sb.append(sec + 's');
            }

            viewHolder.column3.setText(sb.toString());

            viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "" + WipGlobals.details.get(WipGlobals.detailsIndex.get()).get(i).getWeather().icon(), Toast.LENGTH_SHORT).show();

                    final Dialog dialog = new Dialog(mContext);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_textview);
                    dialog.show();


                }
            });
        } else {
        }
    }

    @Override
    public int getItemCount() {
        if(WipGlobals.details.size() != 0)
            return WipGlobals.details.get(WipGlobals.detailsIndex.get()).size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView column1;
        TextView column2;
        TextView column3;

        RelativeLayout parentLayout;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            column1 = itemView.findViewById(R.id.temperature);
            column2 = itemView.findViewById(R.id.summary);
            column3 = itemView.findViewById(R.id.duration);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
