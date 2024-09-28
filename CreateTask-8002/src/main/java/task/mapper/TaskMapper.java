package task.mapper;

import entity.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapper {
    void createTask(Task task);
    List<Task> getTaskList(String creator, String startTime, String endTime, String taskType, int start, int pageSize);
}
