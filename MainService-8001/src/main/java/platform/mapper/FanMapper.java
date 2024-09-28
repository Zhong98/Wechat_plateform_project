package platform.mapper;

import entity.FanInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FanMapper {
    List<String> queryFansCount(FanInfo fanInfo);
    void insertFanTagBinding(int tagId, List<String> openidList);
}
