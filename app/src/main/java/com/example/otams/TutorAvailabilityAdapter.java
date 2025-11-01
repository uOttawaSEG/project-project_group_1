package com.example.otams;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.otams.model.TutorAvailabilityEntity;

import java.util.ArrayList;
import java.util.List;

public class TutorAvailabilityAdapter extends RecyclerView.Adapter<TutorAvailabilityAdapter.VH> {

    public interface OnDeleteClickListener {
        void onDelete(TutorAvailabilityEntity e);
    }

    private final List<TutorAvailabilityEntity> data = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;

    public TutorAvailabilityAdapter(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void submitList(List<TutorAvailabilityEntity> list) {
        data.clear();
        if (list != null) {
            data.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tutor_availability, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final TutorAvailabilityEntity e = data.get(position);
        holder.tvDate.setText(e.date);

        String timeText = e.startTime + " - " + e.endTime;
        if (e.autoApprove) {
            timeText = timeText + " (auto)";
        }
        holder.tvTime.setText(timeText);

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClickListener.onDelete(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvTime;
        TextView tvDelete;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDelete = itemView.findViewById(R.id.tvDelete);
        }
    }
}