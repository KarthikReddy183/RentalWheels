package com.auto.rentalwheels.common;


import androidx.annotation.NonNull;

import com.auto.rentalwheels.data.UserDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FireBaseDbRepository {

    UserDetails userDetails = new UserDetails();

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


    public void getUserId(String userId) {
        reference.child("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //need to set or push the users & speed data here.
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}
