package com.example.sagivproject.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class User implements Serializable {
    private String uid;
    private String email;
    private UserRole role;
    private String firstName;
    private String lastName;
    private long birthDateMillis;
    private String password;
    private String profileImage;
    private HashMap<String, Medication> medications;
    private int count_wins;

    public User() { }

    public User(String uid, String firstName, String lastName, long birthDateMillis, String email, String password, UserRole role, String profileImage, HashMap<String, Medication> medications, int count_wins) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDateMillis = birthDateMillis;
        this.email = email;
        this.password = password;
        this.role = role;
        this.profileImage = profileImage;
        this.medications = medications;
        this.count_wins = count_wins;
    }

    public String getFirstName() { return this.firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return this.lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public long getBirthDateMillis() { return this.birthDateMillis; }
    public void setBirthDateMillis(long birthDateMillis) { this.birthDateMillis = birthDateMillis; }
    public int getAge() {
        Calendar birth = Calendar.getInstance();
        birth.setTimeInMillis(birthDateMillis);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role == null ? UserRole.REGULAR : role; }
    public void setRole(UserRole role) { this.role = role; }
    public boolean isAdmin() { return this.role == UserRole.ADMIN; }

    public String getUid() { return this.uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public HashMap<String, Medication> getMedications() { return this.medications; }
    public void setMedications(HashMap<String, Medication> medications) { this.medications = medications; }

    public int getCountWins() { return this.count_wins; }
    public void setCountWins(int count_wins) { this.count_wins = count_wins; }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uid, user.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid);
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", medications=" + medications +
                ", count_wins=" + count_wins +
                '}';
    }
}