package entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Task {
    private int taskId;
    private String creator;
    private String taskTitle;
    private String taskStatus;
    private Timestamp createTime;
    private Timestamp finishTime;
}
