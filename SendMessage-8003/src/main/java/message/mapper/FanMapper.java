package message.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FanMapper {
    List<String> getFanListByTagId(int tagId);
    List<String> selectNewSubscribers();
}
