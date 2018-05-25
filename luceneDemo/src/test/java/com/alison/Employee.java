package com.alison;

public class Employee {
    private int id;
    private String username;
    private double salary;

    public Employee(int id, String username, double salary) {
        this.id = id;
        this.username = username;
        this.salary = salary;
    }

    public Employee() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", salary=" + salary +
                '}';
    }
}
