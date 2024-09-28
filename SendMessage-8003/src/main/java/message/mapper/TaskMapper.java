package message.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper {
    void updateTaskStatus(int taskId);
}
