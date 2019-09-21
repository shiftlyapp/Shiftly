package com.technion.shiftlyapp.shiftly.dataAccessLayer;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftlyapp.shiftly.dataTypes.Group;
import com.technion.shiftlyapp.shiftly.dataTypes.User;

import java.util.HashMap;

public class DataAccess {
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Constants
    private final String groups = "Groups";
    private final String users = "Users";
    private User user;
    private Group group;

    public DataAccess() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        group = new Group();
    }

    public interface DataAccessCallback<T> {
        void onCallBack(T t);
    }

    public void getUser(String userId, final DataAccessCallback<User> callback) {
        DatabaseReference userRef = databaseRef.child(users).child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                callback.onCallBack(u);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println(databaseError.toString());
            }
        });
    }

    public void updateUser(String userId, User user) {
        DatabaseReference usersRef = databaseRef.child(users).child(userId);
        usersRef.setValue(user).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public void addUser(User user) {
        updateUser(currentUser.getUid(), user);
    }

    public void removeUser(String userId) {
        DatabaseReference usersRef = databaseRef.child(users).child(userId);
        usersRef.removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public void removeGroup(String groupId) {
        DatabaseReference groupsRef = databaseRef.child(groups).child(groupId);
        groupsRef.removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public void getGroup(String groupId, final DataAccessCallback<Group> callback) {
        DatabaseReference groupRef = databaseRef.child(groups).child(groupId);
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group g = dataSnapshot.getValue(Group.class);
                callback.onCallBack(g);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println(databaseError.toString());
            }
        });
    }

    public void updateGroup(String groupId, Group group) {
        DatabaseReference groupsRef = databaseRef.child(groups).child(groupId);
        groupsRef.setValue(group).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public String addGroup(Group group) {
        String groupUid = databaseRef.child(groups).push().getKey();
        updateGroup(groupUid, group);
        return groupUid;
    }

    public interface FindFilter<T> {
        boolean test(T t);
    }

    public void findGroupsBy(FindFilter<Group> filter, DataAccessCallback<HashMap<String, Group>> callback) {
        HashMap<String, Group> foundGroups = new HashMap<>();
        DatabaseReference groupsRef = databaseRef.child(groups);
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot currentGroup : dataSnapshot.getChildren()) {
                    getGroup(currentGroup.getKey(), new DataAccessCallback<Group>() {
                        @Override
                        public void onCallBack(Group g) {
                            if (filter.test(g)) {
                                foundGroups.put(currentGroup.getKey(), g);
                            }
                        }
                    });
                }
                callback.onCallBack(foundGroups);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println(databaseError.toString());
            }
        });
    }
}
