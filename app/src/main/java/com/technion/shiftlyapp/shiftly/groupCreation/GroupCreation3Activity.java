package com.technion.shiftlyapp.shiftly.groupCreation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.dataAccessLayer.DataAccess;
import com.technion.shiftlyapp.shiftly.dataTypes.Group;
import com.technion.shiftlyapp.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;

// The third activity of the group creation process.
// In this activity the future admin sets the group settings.

public class GroupCreation3Activity extends AppCompatActivity {

    private CustomSnackbar mSnackbar;
    private ConstraintLayout mLayout;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private UploadTask uploadTask;
    private Group group;
    private DataAccess dataAccess = new DataAccess();
    private String group_action;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_3);
        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);
        mLayout = (ConstraintLayout) findViewById(R.id.group_creation_3_layout);

        Toolbar mainToolbar = findViewById(R.id.group_creation_toolbar_3);
        setSupportActionBar(mainToolbar);

        group_action = getIntent().getExtras().getString("GROUP_ACTION");
        group = getIntent().getExtras().getParcelable("GROUP");
        String action_bar_title = getIntent().getExtras().getString("TITLE");

        getSupportActionBar().setTitle(action_bar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Setting the spinners with their adapters
        final Spinner days_spinner = findViewById(R.id.days_num_spinner);
        ArrayAdapter<CharSequence> days_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.days_num, R.layout.custom_spinner_item);
        days_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days_spinner.setAdapter(days_spinner_adapter);

        // set in case of group editing
        if (group_action.equals("EDIT")) {
            int days_num = group.getDays_num().intValue();
            days_spinner.setSelection(days_num-1);
        }

        final Spinner shifts_per_day_spinner = findViewById(R.id.shifts_per_day_num_spinner);
        ArrayAdapter<CharSequence> shifts_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.shifts_per_day, R.layout.custom_spinner_item);
        shifts_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shifts_per_day_spinner.setAdapter(shifts_spinner_adapter);

        final Spinner employees_per_shift_spinner = findViewById(R.id.employees_per_shift_num_spinner);
        ArrayAdapter<CharSequence> employees_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.employees_per_shift, R.layout.custom_spinner_item);
        employees_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employees_per_shift_spinner.setAdapter(employees_spinner_adapter);

        final Spinner starting_hour_spinner = findViewById(R.id.starting_hour_spinner);
        ArrayAdapter<CharSequence> starting_hour_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.starting_hour, R.layout.custom_spinner_item);
        starting_hour_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        starting_hour_spinner.setAdapter(starting_hour_spinner_adapter);

        final Spinner shift_len_spinner = findViewById(R.id.shift_len_spinner);
        ArrayAdapter<CharSequence> shift_len_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.shift_len, R.layout.custom_spinner_item);
        shift_len_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shift_len_spinner.setAdapter(shift_len_spinner_adapter);

        setSpinners(group_action, shifts_per_day_spinner, employees_per_shift_spinner, starting_hour_spinner, shift_len_spinner);

        mStorage = FirebaseStorage.getInstance();

        Button apply_button = findViewById(R.id.continue_button_group_creation_3);
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);

                Long days_num = Long.parseLong(days_spinner.getSelectedItem().toString());
                group.setDays_num(days_num);

                Long shifts_per_day = Long.parseLong(shifts_per_day_spinner.getSelectedItem().toString());
                group.setShifts_per_day(shifts_per_day);

                Long employees_per_shift = Long.parseLong(employees_per_shift_spinner.getSelectedItem().toString());
                group.setEmployees_per_shift(employees_per_shift);

                String starting_hour = starting_hour_spinner.getSelectedItem().toString();
                group.setStarting_time(starting_hour);

                Long shift_len = Long.parseLong(shift_len_spinner.getSelectedItem().toString());
                group.setShift_length(shift_len);

                byte[] group_pic_array = getIntent().getExtras().getByteArray("GROUP_PICTURE"); // must have
                // Contains the group_id to edit only in case the user reached this activity via edit, else contains the empty string
                String group_id = getIntent().getExtras().getString("GROUP_ID");

                if (group_action.equals("CREATE")) {
                    Intent group_creation_4_intent = new Intent(getApplicationContext(), GroupCreation4Activity.class);

                    group_creation_4_intent.putExtra("GROUP", group);
                    group_creation_4_intent.putExtra("GROUP_ACTION", group_action);
                    if (group_pic_array != null) {
                        group_creation_4_intent.putExtra("GROUP_PICTURE", group_pic_array);
                    }
                    startActivity(group_creation_4_intent);
                    finish();

                } else {
                    edit_group(group_id, group_pic_array);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSnackbar.show(GroupCreation3Activity.this, mLayout, getResources().getString(R.string.group_edit_success), CustomSnackbar.SNACKBAR_SUCCESS, Snackbar.LENGTH_LONG);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent groupsIManageFragment = new Intent(getApplicationContext(), GroupListsActivity.class);
                                    startActivity(groupsIManageFragment);
                                    finish();
                                }
                            }, Constants.REDIRECTION_DELAY);
                        }
                    }, Constants.REDIRECTION_DELAY);
                }

            }
        });
    }

    private void setSpinners(String group_action, Spinner shifts_per_day_spinner, Spinner employees_per_shift_spinner, Spinner starting_hour_spinner, Spinner shift_len_spinner) {
        if (group_action.equals("EDIT")) {
            int shifts_per_day = group.getShifts_per_day().intValue();
            shifts_per_day_spinner.setSelection(shifts_per_day - 1);

            int employees_per_shift = group.getEmployees_per_shift().intValue();
            employees_per_shift_spinner.setSelection(employees_per_shift - 1);

            int starting_time = Integer.parseInt(group.getStarting_time());
            starting_hour_spinner.setSelection(starting_time - 1);

            int shift_len = group.getShift_length().intValue();
            shift_len_spinner.setSelection(shift_len - 1);
        }
    }

    private void edit_group(final String group_id, byte[] group_pic_array) {
        // Group icon
        uploadPic(group_id, group_pic_array);

        // Group characteristics
        dataAccess.updateGroup(group_id, group);
    }

    private void uploadPic(final String group_id, byte[] group_pic_array) {
        mStorageRef = mStorage.getReference().child("group_pics/" + group_id + ".png");
        if (group_pic_array != null) {
            uploadTask = mStorageRef.putBytes(group_pic_array);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Image upload failed
                    mSnackbar.show(GroupCreation3Activity.this, mLayout, getResources().getString(R.string.edit_pic_error), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            group.setGroup_icon_url(uri.toString());
                            dataAccess.updateGroup(group_id, group);
                        }
                    });
                }
            });
        }
    }
}