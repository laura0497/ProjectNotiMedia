package com.delarosa.notimedia.controller.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.delarosa.notimedia.R;
import com.delarosa.notimedia.model.Entitys.NotificationManagerEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NotificationManagerAdapter extends RecyclerView.Adapter<NotificationManagerAdapter.MyViewHolder> {

    private List<NotificationManagerEntity> notificationManagerEntityList;
    View itemLayoutView;
    private ClickListener clickListener;
    public ArrayList<NotificationManagerEntity> arrayNotificationManagerEntity;
    Context context;

    //this method receive the list with the data to show
    public NotificationManagerAdapter(List<NotificationManagerEntity> notificationManagerList, ClickListener mClickListener, Context mContext) {
        notificationManagerEntityList = notificationManagerList;
        clickListener = mClickListener;
        arrayNotificationManagerEntity = new ArrayList<>();
        arrayNotificationManagerEntity.addAll(notificationManagerList);
        context = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_manager_row, parent, false);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)  {
        NotificationManagerEntity notificationManager = notificationManagerEntityList.get(position);

        int status = notificationManager.getStatus();
        if (status == 1) {
            holder.tvduration.setText("Descarga en proceso");
            holder.tvuniquedelete.setVisibility(View.INVISIBLE);
        } else if (status == 2) {
            holder.tvduration.setText("Descarga finalizada en " + notificationManager.getDuration() + " min");
            holder.tvuniquedelete.setVisibility(View.VISIBLE);
        }

        holder.tvdate.setText(notificationManager.getDate().substring(0, 10));
    }

    @Override
    public int getItemCount() {
        return notificationManagerEntityList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.uniquedelete)
        ImageButton tvuniquedelete;
        @BindView(R.id.duration)
        public TextView tvduration;
         @BindView(R.id.date)
        public TextView tvdate;



        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvuniquedelete.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClick(view, getAdapterPosition());
            }
        }
    }

    public interface ClickListener {
        void itemClick(View view, int position);

    }

    // clear the recyclerview
    public void clear() {
        notificationManagerEntityList.clear();
        notifyDataSetChanged();
    }


}
