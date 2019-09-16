package com.technion.shiftlyapp.shiftly.scheduleView;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.alamkanak.weekview.WeekView;
import com.technion.shiftlyapp.shiftly.R;

public class DailyViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get a reference for the week view in the layout.
        View v = inflater.inflate(R.layout.fragment_daily_view, container, false);
        Resources res = getResources();

        int[] mColors = res.getIntArray(R.array.calendar_colors);
        String groupId = getActivity().getIntent().getExtras().getString("GROUP_ID");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.daily_label));
        WeekView mDayView = (WeekView) v.findViewById(R.id.dayView);
        Scheduler daily_schedule = new Scheduler(mDayView,groupId,mColors,1, getContext());
        return v;
    }
}