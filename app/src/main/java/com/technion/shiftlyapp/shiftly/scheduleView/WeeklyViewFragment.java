package com.technion.shiftlyapp.shiftly.scheduleView;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.alamkanak.weekview.WeekView;
import com.technion.shiftlyapp.shiftly.R;

public class WeeklyViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get a reference for the week view in the layout.
        View v = inflater.inflate(R.layout.fragment_weekly_view, container, false);
        Resources res = getResources();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(getContext(), R.string.recommend_landscape, Toast.LENGTH_LONG).show();
        }
        int[] mColors = res.getIntArray(R.array.calendar_colors);
        String groupId = getActivity().getIntent().getExtras().getString("GROUP_ID");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.weekly_label));
        WeekView mWeekView = (WeekView) v.findViewById(R.id.weekView);
        Scheduler weekly_schedule = new Scheduler(mWeekView, groupId, mColors, 7, getContext());
        return v;
    }
}