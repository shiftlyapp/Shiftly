package com.technion.shiftlyapp.shiftly.groupsList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.dataAccessLayer.DataAccess;
import com.technion.shiftlyapp.shiftly.dataTypes.Group;
import com.technion.shiftlyapp.shiftly.groupCreation.GroupCreation1Activity;
import com.technion.shiftlyapp.shiftly.scheduleView.ScheduleViewActivity;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;
import com.technion.shiftlyapp.shiftly.utility.DividerItemDecorator;
import com.venmo.view.TooltipView;

import java.util.ArrayList;
import java.util.List;

public class GroupsIManageFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<String> groupsNames;
    private List<Long> groupsMembersCount;
    private List<String> groupsIds;
    private List<String> groupsIconsUrls;
    private LottieAnimationView loading_icon;
    private LinearLayout no_groups_container;
    private CustomSnackbar mSnackbar;
    private Context context;
    private GroupListsActivity activity;
    private View view;
    private TooltipView manage_tooltip;
    private TooltipView delete_group_tooltip;
    private FloatingActionButton create_group_fab;
    private DataAccess dataAccess;

    private void handleLoadingState(int state) {
        switch (state) {
            case Constants.HIDE_LOADING_ANIMATION:
                manage_tooltip.setVisibility(View.GONE);
                delete_group_tooltip.setVisibility(View.VISIBLE);
                loading_icon.setVisibility(View.GONE);
                create_group_fab.show();
                mRecyclerView.setVisibility(View.VISIBLE);
                break;
            case Constants.SHOW_LOADING_ANIMATION:
                manage_tooltip.setVisibility(View.GONE);
                delete_group_tooltip.setVisibility(View.GONE);
                loading_icon.setVisibility(View.VISIBLE);
                create_group_fab.hide();
                mRecyclerView.setVisibility(View.INVISIBLE);
                break;
            case Constants.EMPTY_GROUPS_COUNT:
                manage_tooltip.setVisibility(View.VISIBLE);
                delete_group_tooltip.setVisibility(View.GONE);
                no_groups_container.setVisibility(View.VISIBLE);
                loading_icon.setVisibility(View.GONE);
                create_group_fab.show();
                mRecyclerView.setVisibility(View.GONE);
                break;
        }
    }

    private void showError(@NonNull DatabaseError error) {
        mSnackbar.show(context, view, error.getMessage(), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
    }

    private void loadRecyclerViewData() {
        // ---------------------------- Check current User groups_count ----------------------------
        DatabaseReference mGroupDatabase = FirebaseDatabase.getInstance().getReference("Groups");
        Query query = mGroupDatabase.orderByChild("admin").equalTo(activity.getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot current_group : dataSnapshot.getChildren()) {
                        String group_name = current_group.child("group_name").getValue(String.class);
                        Long members_count = current_group.child("members_count").getValue(Long.class);
                        String group_icon_url = current_group.child("group_icon_url").getValue(String.class);
                        groupsIds.add(current_group.getKey());
                        groupsIconsUrls.add(group_icon_url);
                        groupsNames.add(group_name);
                        groupsMembersCount.add(members_count);
                    }
                    mAdapter.notifyDataSetChanged();
                    handleLoadingState(Constants.HIDE_LOADING_ANIMATION);
                } else {
                    handleLoadingState(Constants.EMPTY_GROUPS_COUNT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showError(databaseError);
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (mAdapter!=null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_groups_i_manage, container, false);
        activity = (GroupListsActivity) getActivity();
        context = getContext();

        dataAccess = new DataAccess();

        // Getting views loaded with findViewById
        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_manage);
        loading_icon = view.findViewById(R.id.loading_icon_manage);
        LottieAnimationView eye_anim = view.findViewById(R.id.eye_anim_manage);
        no_groups_container = view.findViewById(R.id.no_groups_container_manage);

        create_group_fab = view.findViewById(R.id.create_fab);
        create_group_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent group_edit_creation = new Intent(context,GroupCreation1Activity.class);
                group_edit_creation.putExtra("GROUP_ACTION", "CREATE");
                startActivity(group_edit_creation);
            }
        });

        manage_tooltip = view.findViewById(R.id.manage_tooltip);
        manage_tooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });

        delete_group_tooltip = view.findViewById(R.id.delete_group_tooltip);
        delete_group_tooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });

        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);

        // Configuring RecyclerView with A LinearLayout and adding dividers
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.recycler_divider));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Setting the animations scale
        eye_anim.setScale(Constants.EYE_ANIM_SCALE);
        loading_icon.setScale(Constants.LOADING_ANIM_SCALE);

        // Show the loading animation upon loading the fragment
        handleLoadingState(Constants.SHOW_LOADING_ANIMATION);

        groupsIds = new ArrayList<>();
        groupsNames = new ArrayList<>();
        groupsIconsUrls = new ArrayList<>();
        groupsMembersCount = new ArrayList<>();
        mAdapter = new GroupsListAdapter(context, groupsNames, groupsMembersCount, groupsIconsUrls);
        mRecyclerView.setAdapter(mAdapter);
        loadRecyclerViewData();
        ((GroupsListAdapter) mAdapter).setClickListener(new GroupsListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent schedule_view = new Intent(context, ScheduleViewActivity.class);
                schedule_view.putExtra("GROUP_ID", groupsIds.get(position));
                startActivity(schedule_view);
            }
        });

        ((GroupsListAdapter) mAdapter).setLongClickListener(new GroupsListAdapter.ItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                presentEditDeleteDialog(position);
            }
        });
        return view;
    }

    private void presentEditDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        builder.setMessage(R.string.edit_delete_group_dialog);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.edit_group, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get group details to present in the initial form
                String group_id = groupsIds.get(position);
                dataAccess.getGroup(group_id, new DataAccess.DataAccessCallback<Group>() {
                    @Override
                    public void onCallBack(Group group) {
                        Intent group_edit_creation = new Intent(context,GroupCreation1Activity.class);

                        group_edit_creation.putExtra("GROUP_ACTION", "EDIT");
                        group_edit_creation.putExtra("GROUP_ID", groupsIds.get(position));
                        group_edit_creation.putExtra("GROUP", group);

                        startActivity(group_edit_creation);
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.delete_group, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the group only if the user tapped on "Delete Group" in the dialog
                deleteGroup(position);
                groupsIds.remove(position);
                groupsIconsUrls.remove(position);
                groupsNames.remove(position);
                groupsMembersCount.remove(position);
                mRecyclerView.removeViewAt(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, groupsIds.size());
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog delete_dialog = builder.create();
        delete_dialog.show();
    }

    private void deleteGroup(int position) {
        // Save the group IDs

        final String group_id = groupsIds.get(position);

        final ArrayList<String> memberIds = new ArrayList<>();

        final DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        groupsRef.child(group_id).child("members").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showError(databaseError);
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Go over the group members and save their ID's in a variable
                for (DataSnapshot member : dataSnapshot.getChildren()) {
                    memberIds.add(member.getKey());
                }

                // Go over the saved list and for each user go to groups and delete the correct group
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showError(databaseError);
                    }

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (memberIds.isEmpty()) {
                            groupsRef.child(group_id).removeValue();
                        } else {
                            // for each user, decrease the groups number by 1
                            for (final DataSnapshot user : dataSnapshot.getChildren()) {
                                if (memberIds.contains(user.getKey())) {
                                    final String old_groups_num = String.valueOf(dataSnapshot.child(user.getKey()).child("groups_count").getValue());
                                    final String new_groups_num = String.valueOf(Long.valueOf(old_groups_num) - 1);

                                    // Delete the group entirely
                                    DataAccess dataAccess = new DataAccess();
                                    dataAccess.removeGroup(group_id);

                                    final DatabaseReference userGroups = usersRef.child(user.getKey()).child("groups");
                                    userGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int group_index = 0;
                                            for (DataSnapshot userGroup : dataSnapshot.getChildren()) {
                                                if (userGroup.getValue().equals(group_id)) {
                                                    // remove the group from each user
                                                    usersRef.child(user.getKey()).child("groups").child(String.valueOf(group_index)).removeValue();
                                                    usersRef.child(user.getKey()).child("groups_count").setValue(new_groups_num);
                                                    break;
                                                }
                                                group_index++;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}