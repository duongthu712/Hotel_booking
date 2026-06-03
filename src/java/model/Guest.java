package model;

import java.time.LocalDate;

/**
 * Last update 23:55 02/06/2026
 *
 * @author LinhLTHE200306
 */
public class Guest {

    private int guestId;
    private String fullName;
    private String email;
    private String phone;
    private String idNumber;
    private String nationality;
    private LocalDate dateOfBirth;

    //Constructor
    public Guest() {
    }

    public Guest(int guestId, String fullName, String email, String phone, String idNumber, String nationality, LocalDate dateOfBirth) {
        this.guestId = guestId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.idNumber = idNumber;
        this.nationality = nationality;
        this.dateOfBirth = dateOfBirth;
    }

    //Getter & Setter
    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
