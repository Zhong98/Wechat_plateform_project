package platform.mapper;

import entity.RuleTag;
import entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {
    void addTag(Tag tag);
    void deleteTag(int tagId);
    void updateTag(Tag tag);
    int getRuleTag();
    int getLocalTag();
    List<Tag> getTags(int start, int size);
    Tag queryTagDetail(int tagId);
    RuleTag queryRuleTag(int tagId);
}
