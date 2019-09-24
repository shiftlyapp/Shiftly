package com.technion.shiftlyapp.shiftly.scheduleView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.algorithm.ShiftSchedulingSolver;
import com.technion.shiftlyapp.shiftly.dataAccessLayer.DataAccess;
import com.technion.shiftlyapp.shiftly.dataTypes.Group;
import com.technion.shiftlyapp.shiftly.options.OptionsListActivity;
import com.technion.shiftlyapp.shiftly.optionsView.OptionsViewActivity;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;
import com.venmo.view.TooltipView;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScheduleViewActivity extends AppCompatActivity implements ShareActionProvider.OnShareTargetSelectedListener{

    private ConstraintLayout mLayout;
    private DatabaseReference databaseRef;
    private CustomSnackbar mSnackbar;
    private FirebaseAuth mAuth;
    private String group_id;
    private Fragment currentFragment;
    private String currentFragmentName;

    private int starting_time;
    private int shift_length;
    private int shifts_per_day;
    private int days_num;
    private int workers_in_shift;
    private String group_name;
    private String NA;

    // Weekly schedule table creation
    TableLayout scheduleTable;
    private ArrayList<String> employeeNamesList;

    private BottomNavigationView navigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            currentFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_daily:
                    currentFragment = new DailyViewFragment();
                    currentFragmentName = "daily";
                    break;
                case R.id.navigation_weekly:
                    currentFragment = new WeeklyViewFragment();
                    currentFragmentName = "weekly";
                    break;
                case R.id.navigation_agenda:
                    currentFragment = new AgendaViewFragment();
                    currentFragmentName = "agenda";
                    break;
            }
            return launchFragment(currentFragment);
        }
    };

    private LinkedHashMap<String, String> getGroupOptions(@NonNull DataSnapshot dataSnapshot) {
        // Get the members in the group
        LinkedHashMap<String, String> group_options = new LinkedHashMap<>();
        if (!dataSnapshot.child("options").exists()) {
            // Case no options are recorded
            return null;
        } else {
            // For each member, get the options
            for (DataSnapshot postSnapshot : dataSnapshot.child("options").getChildren()) {
                group_options.put(postSnapshot.getKey(), postSnapshot.getValue().toString());
            }
        }
        return group_options;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    interface FetchSchedulecallback {
        void onCallBack(boolean isSuccessful);
    }

    private void fetchSchedule(final FetchSchedulecallback updateCallback) {
        // Pull the ids of the scheduled employees from DB
        DatabaseReference mGroupDatabase = FirebaseDatabase.getInstance()
                .getReference("Groups").child(group_id); // a reference the the group

        mGroupDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasSchedule = dataSnapshot.hasChild("schedule");
                if (hasSchedule) {
                    // Load the ids of the employees from the schedule
                    employeeNamesList = new ArrayList<>();
                    for (DataSnapshot current_employee : dataSnapshot.child("schedule").getChildren()) {
                        String employeeId = current_employee.getValue(String.class);
                        String employeeName = (employeeId.equals(NA)) ? NA : dataSnapshot.child("members").child(employeeId).getValue().toString();

                        employeeNamesList.add(employeeName);
                    }
                } else {
                    Toast.makeText(ScheduleViewActivity.this, R.string.no_schedule_available, Toast.LENGTH_LONG).show();
                }
                updateCallback.onCallBack(hasSchedule);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    /* --------------- Weekly table Creation --------------- */
    ArrayList<ArrayList<String>> parseScheduleToTable() {
        // Sets up the schedule list
        ArrayList<ArrayList<String>> scheduleByRows = new ArrayList<>();
        for (int i=0 ; i< shifts_per_day ; i++) {
            ArrayList<String> rowList = new ArrayList<>();
            for (int j=0; j< days_num * workers_in_shift ; j++) {
                rowList.add("");
            }
            scheduleByRows.add(rowList);
        }
        // Iterate over the schedule map and convert it to a list arranged by rows
        for (int i = 0; i < employeeNamesList.size(); i++) {
            String employee = employeeNamesList.get(i);

            int day = i / (shifts_per_day * workers_in_shift);
            int shiftInDay = i % (shifts_per_day);
            int positionInDay = ((i%workers_in_shift) + day*workers_in_shift);

            // Append the worker's name to the list of name
            scheduleByRows.get(shiftInDay).set(positionInDay, employee);
        }
        return scheduleByRows;
    }

    private void createHeader(int rowLength) {
        TableRow header = new TableRow(ScheduleViewActivity.this);
        String[] days = getResources().getStringArray(R.array.week_days);
        for (int i=0 ; i<rowLength ; i++) {
            int span = 1;

            // Set a new cell in header
            TextView cell = new TextView(ScheduleViewActivity.this);
            cell.setPadding((int)getResources().getDimension(R.dimen.table_cell_padding_left_right),
                    (int)getResources().getDimension(R.dimen.table_cell_padding_top_bottom),
                    (int)getResources().getDimension(R.dimen.table_cell_padding_left_right),
                    (int)getResources().getDimension(R.dimen.table_cell_padding_top_bottom));
            cell.setTextSize((int)getResources().getDimension(R.dimen.table_cell_text_size));
            cell.setBackground(getDrawable(R.drawable.table_cell_shape));

            if (i == 0) {
                cell.setText("");
            } else {
                span = workers_in_shift;
                cell.setText(days[i-1]);
                cell.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            }
            header.addView(cell);

            // Set layout parameters
            TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) header.getChildAt(i).getLayoutParams();
            layoutParams.span = span; //amount of columns you will span
            header.getChildAt(i).setLayoutParams(layoutParams);

        }
        scheduleTable.addView(header);
    }

    private void createRow(ArrayList<String> rowCells, int rowLength, int shiftStartingTime) {
        TableRow row = new TableRow(ScheduleViewActivity.this);
        for (int i=0 ; i<rowLength ; i++) {
            TextView cell = new TextView(ScheduleViewActivity.this);
            cell.setBackground(getDrawable(R.drawable.table_cell_shape));
            cell.setPadding(28, 8, 28, 8);
            cell.setTextSize(18);

            if (i == 0) {
                cell.setText(String.format(getString(R.string.hour_format), shiftStartingTime < 10 ?
                        ("0" + shiftStartingTime) :
                        (shiftStartingTime), ((shift_length + shiftStartingTime) % 24) < 10 ?
                        "0" + ((shift_length + shiftStartingTime) % 24) :
                        ((shift_length + shiftStartingTime) % 24)));
            } else {
                cell.setText(rowCells.get(i-1));
            }
            row.addView(cell);
        }
        row.setBackground(getDrawable(R.drawable.table_cell_shape));
        scheduleTable.addView(row);
    }

    private void createTableBody(ArrayList<ArrayList<String>> table, int rowLength) {
        for (int i = 0; i < table.size(); i++) {
            ArrayList<String> row = table.get(i);
            // Shift starting time is calculated for each shift
            createRow(row, rowLength, (starting_time + (shift_length * i++)) % 24);
        }
    }

    Bitmap getBitmapFromView(View view) {
        Bitmap weeklyScheduleBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(weeklyScheduleBitmap);
        Drawable background = view.getBackground();
        if (background != null) {
            background.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);

        return weeklyScheduleBitmap;
    }

    private void sendImage(Bitmap weeklyScheduleBitmap) {
        // Sharing sequence
        Intent shareScheduleIntent = new Intent(Intent.ACTION_SEND);
        shareScheduleIntent.setType("image/jpeg");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        OutputStream outstream;
        try {
            outstream = getContentResolver().openOutputStream(uri);
            weeklyScheduleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        shareScheduleIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareScheduleIntent, "Share Image"));
        weeklyScheduleBitmap.recycle();
        weeklyScheduleBitmap = null;
    }

    /* --------------- Weekly table Creation --------------- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule_menu, menu);
        return true;
    }

    private void copyGroupIDToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("group code", group_id);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(ScheduleViewActivity.this, R.string.copy_group_code_text, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source,
                                         Intent intent) {
        Toast.makeText(this, intent.getComponent().toString(),
                Toast.LENGTH_LONG).show();

        return(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_view);
        Toolbar schedule_view_toolbar = (Toolbar) findViewById(R.id.schedule_view_toolbar);
        setSupportActionBar(schedule_view_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        currentFragment = new DailyViewFragment();
        currentFragmentName = "daily";
        navigationView = findViewById(R.id.bottom_navigation_schedule);

        group_id = getIntent().getExtras().getString("GROUP_ID");
        NA = getResources().getString(R.string.not_available);
        setToolbarMenuItems(schedule_view_toolbar);

        mLayout = (ConstraintLayout) findViewById(R.id.container_schedule_view);
        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);
        mAuth = FirebaseAuth.getInstance();
        getGroupParameters();

        final FloatingActionButton optionsFab = findViewById(R.id.options_fab);
        final FloatingActionButton scheduleFab = findViewById(R.id.schedule_fab);
        final FloatingActionButton editScheduleFab = findViewById(R.id.edit_schedule_fab);
        final FloatingActionButton viewOptionsFab = findViewById(R.id.view_options_fab);
        final FloatingActionsMenu menuFab = findViewById(R.id.menu_fab);

        final TooltipView options_tooltip = findViewById(R.id.options_tooltip);

        options_tooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });
        final TooltipView edit_schedule_fab_tooltip = findViewById(R.id.edit_schedule_fab_tooltip);
        edit_schedule_fab_tooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });
        final TooltipView schedule_tooltip = findViewById(R.id.schedule_tooltip);
        schedule_tooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });
        final TooltipView view_options_tooltip = findViewById(R.id.view_options_tooltip);
        view_options_tooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });

        databaseRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String admin_uuid = dataSnapshot.getValue().toString();
                String logged_in_user_uuid = mAuth.getCurrentUser().getUid();

                if (admin_uuid.equals(logged_in_user_uuid)) {
                    navigationView.getMenu().removeItem(R.id.navigation_agenda);

                    scheduleFab.setVisibility(View.VISIBLE);
                    scheduleFab.setIcon(R.drawable.ic_generate_schedule_fab);
                    scheduleFab.setTitle("Create schedule");

                    editScheduleFab.setVisibility(View.VISIBLE);
                    editScheduleFab.setIcon(R.drawable.ic_edit_schedule_fab);
                    editScheduleFab.setTitle("Edit schedule");

                    viewOptionsFab.setVisibility(View.VISIBLE);
                    viewOptionsFab.setIcon(R.drawable.ic_view_options);
                    viewOptionsFab.setTitle("View options");

                    menuFab.setVisibility(View.VISIBLE);

                    schedule_tooltip.setVisibility(View.VISIBLE);
                    edit_schedule_fab_tooltip.setVisibility(View.VISIBLE);
                    view_options_tooltip.setVisibility(View.VISIBLE);

                } else {
                    optionsFab.setVisibility(View.VISIBLE);
                    optionsFab.setIcon(R.drawable.ic_edit_timeslots_fab);
                    options_tooltip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        optionsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OptionsListActivity.class);
                intent.putExtra("GROUP_ID", group_id);
                startActivity(intent);
            }
        });

        viewOptionsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Pull data from db
                databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LinkedHashMap<String, String> group_options = getGroupOptions(dataSnapshot);
                        if (group_options == null) {
                            // Case no options are recorded
                            mSnackbar.show(ScheduleViewActivity.this, mLayout,
                                    getResources().getString(R.string.view_options_no_options),
                                    CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                        } else {
                            presentOptions(dataSnapshot, group_options, view);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        // FAB
        scheduleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirmation dialog before generating
                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleViewActivity.this, R.style.CustomAlertDialog);
                builder.setMessage(R.string.generate_schedule_dialog);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.yes, (dialog, which) -> generateSchedule());
                builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());
                AlertDialog generate_schedule_dialog = builder.create();
                generate_schedule_dialog.show();
            }
        });

        editScheduleFab.setOnClickListener(view -> {
            DataAccess dataAccess = new DataAccess();
            dataAccess.getGroup(group_id, new DataAccess.DataAccessCallback<Group>() {
                @Override
                public void onCallBack(Group group) {
                    if (group.getSchedule() == null || group.getSchedule().size() == 0) {
                        mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.no_schedule_available), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                    } else {
                        Intent intent = new Intent(view.getContext(), ScheduleEditActivity.class);
                        intent.putExtra("GROUP_ID", group_id);
                        finish();
                        startActivity(intent);
                    }
                }
            });

        });

        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        launchFragment(currentFragment);
    }

    // Toolbar menu items
    private void setToolbarMenuItems(Toolbar schedule_view_toolbar) {
        schedule_view_toolbar.setOnMenuItemClickListener(item -> {
            switch(item.getItemId()) {
                case R.id.copy_to_clipboard_item:
                    copyGroupIDToClipboard();
                    break;
                case R.id.share_item:
                    if (currentFragmentName.equals("agenda")) {
                        RecyclerView agendaView = findViewById(R.id.users_shifts);
                        if (agendaView.getChildCount() > 0) {
                            Bitmap scheduleAgendaBitMap = getBitmapFromView(agendaView);
                            sendImage(scheduleAgendaBitMap);
                        } else {
                            Toast.makeText(ScheduleViewActivity.this, R.string.no_schedule_available, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // call share methods
                        fetchSchedule(isSuccessful -> {
                            if (isSuccessful) {
                                // Clear the old share table
                                scheduleTable = (TableLayout) findViewById(R.id.schedule_table);
                                scheduleTable.removeAllViewsInLayout();
                                createWeeklyScheduleTableLayout();

                                scheduleTable.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                                    Bitmap scheduleBitMap = getBitmapFromView(scheduleTable);
                                    sendImage(scheduleBitMap);
                                });
                            }
                        });
                    }

                    break;
            }
            return true;
        });
    }

    private void createWeeklyScheduleTableLayout() {
        // Prepare the schedule array
        ArrayList<ArrayList<String>> scheduleArray = parseScheduleToTable();
        // Prepare the table's header
        createHeader(days_num +1);
        // Prepare the schedule table itself
        createTableBody(scheduleArray, (days_num*workers_in_shift +1));
    }

    private void getGroupParameters() {
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                starting_time = Integer.parseInt(dataSnapshot.child("starting_time").getValue().toString());
                shift_length = Integer.parseInt(dataSnapshot.child("shift_length").getValue().toString());
                shifts_per_day = Integer.parseInt(dataSnapshot.child("shifts_per_day").getValue().toString());
                days_num = Integer.parseInt(dataSnapshot.child("days_num").getValue().toString());
                workers_in_shift = Integer.parseInt(dataSnapshot.child("employees_per_shift").getValue().toString());
                group_name = dataSnapshot.child("group_name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void presentOptions(@NonNull DataSnapshot dataSnapshot, LinkedHashMap<String, String> group_options, View view) {
        // Collect the group members map
        LinkedHashMap<String, String> group_members = new LinkedHashMap<>();
        // Collect the members that are yet to send options
        String workers_without_options = "";

        for (DataSnapshot postSnapshot : dataSnapshot.child("members").getChildren()) {
            group_members.put(postSnapshot.getKey(), postSnapshot.getValue().toString());
            if (!group_options.containsKey(postSnapshot.getKey().toString())) {
                workers_without_options += (postSnapshot.getValue().toString() + "\n");
            }
        }

        // Switch uuids with names
        HashMap<String, String> options = new HashMap<>();
        for (LinkedHashMap.Entry<String, String> entry : group_options.entrySet()) {
            options.put(group_members.get(entry.getKey()), entry.getValue());
        }

        // Pass the member's related options to the next activity
        Intent intent = new Intent(view.getContext(), OptionsViewActivity.class);

        intent.putExtra("GROUP_NAME", group_name);
        intent.putExtra("STARTING_TIME", starting_time);
        intent.putExtra("SHIFT_LENGTH", shift_length);
        intent.putExtra("SHIFTS_PER_DAY", shifts_per_day);
        intent.putExtra("DAYS_NUM", days_num);
        intent.putExtra("WORKERS_IN_SHIFT", workers_in_shift);
        intent.putExtra("WORKERS_WITHOUT_OPTIONS", workers_without_options);
        intent.putExtra("OPTIONS", options);

        startActivity(intent);
    }

    private void generateSchedule() {
        // Pull data from db
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinkedHashMap<String, String> group_options = getGroupOptions(dataSnapshot);
                if (group_options != null && group_options.size() >= (Long) dataSnapshot.child("members_count").getValue()) {
                    String employees_per_shift = (dataSnapshot.child("employees_per_shift").getValue()).toString();
                    // Run scheduling algorithm
                    ShiftSchedulingSolver solver = new ShiftSchedulingSolver(group_options, Integer.parseInt(employees_per_shift));
                    Boolean result = solver.solve();
                    // result is always true

                    List<String> generated_schedule = solver.getFinal_schedule();
                    Map<String, Object> schedule_map = new HashMap<>();
                    schedule_map.put("schedule", generated_schedule);
                    databaseRef.updateChildren(schedule_map);

                    if (generated_schedule.contains(NA)) {
                        // Present a snackbar of "A full schedule could not be created" (warning)
                        mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.schedule_generation_error), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                    } else {
                        // Present a snackbar of "Schedule generated!" (success)
                        mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.schedule_generation_success), CustomSnackbar.SNACKBAR_SUCCESS, Snackbar.LENGTH_SHORT);
                        // Upload schedule to DB
                    }

                    switch (currentFragmentName) {
                        case "daily":
                            navigationView.getMenu().performIdentifierAction(R.id.navigation_daily, 0);
                            break;
                        case "weekly":
                            navigationView.getMenu().performIdentifierAction(R.id.navigation_weekly, 0);
                            break;
                        case "agenda":
                            navigationView.getMenu().performIdentifierAction(R.id.navigation_agenda, 0);
                            break;
                    }
                } else {
                    mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.schedule_no_options), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private boolean launchFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        return true;
    }
}