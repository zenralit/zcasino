package com.example.zkazino;

public class UserModel {
    private String email;
    private String surname;
    private String name;
    private String patronymic;
    private String phone;
    private String dateOfBirth;
    private String gender;
    private long balance;
    private long createdAt;

    public UserModel() {}

    public UserModel(String email, String surname, String name, String patronymic,
                     String phone, String dateOfBirth, String gender) {
        this.email = email;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.balance = 1000;
        this.createdAt = System.currentTimeMillis();
    }

    // Геттеры и сеттеры
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public long getBalance() { return balance; }
    public void setBalance(long balance) { this.balance = balance; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}