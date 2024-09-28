package platform.service;

import entity.Tag;

import java.util.Map;

public interface TagService {
    Map<String, Object> queryTagInfo(int page, int size);
    void addTag(Tag tag, String token);
    void deleteTag(int tagId);
    void updateTag(Tag tag);
    Map<String, Object> queryTagDetail(int tagId);
}
