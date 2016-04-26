package com.example.desy.myandroid.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by desy on 4/21/16.
 */
public class Interest {
    private String interest;
    private boolean checked;
    private SharedPreferences interestPref;
    private SharedPreferences.Editor interestEditor;

    public Interest (String interest) {
        this.interest = interest;
    }

    public Interest (String interest, Context context) {
        this.interest = interest;
        interestPref = context.getSharedPreferences("profile_interest", Context.MODE_PRIVATE);
        interestEditor = interestPref.edit();
        this.checked = interestPref.getBoolean(interest, true);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        interestEditor.putBoolean(interest, checked);
        interestEditor.commit();
        this.checked = checked;
    }



    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }


    private static int lastContactId = 0;

    public static ArrayList<Interest> createContactsList(int numContacts) {
        ArrayList<Interest> interests = new ArrayList<>();

        for (int i = 1; i <= numContacts; i++) {
            interests.add(new Interest("interest " + ++lastContactId));
        }

        return interests;
    }
}
