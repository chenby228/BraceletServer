package com.example.demo.entities;

import javax.persistence.*;

/**
 * alter table step_data change id  id int AUTO_INCREMENT;
 */
@Entity
@Table(name = "step_data")
public class StepData {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "user_id")
    private String userId;  // 用户

    @Column(name = "date")
    private String date; //日期

    @Column(name = "steps")
    private String steps; //记步数

    public StepData() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }
}
