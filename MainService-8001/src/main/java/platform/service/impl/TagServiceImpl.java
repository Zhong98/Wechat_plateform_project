package platform.service.impl;

import entity.RuleTag;
import entity.Tag;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import platform.mapper.TagMapper;
import platform.service.TagService;
import platform.util.JWTUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class TagServiceImpl implements TagService {

    @Resource
    TagMapper tagMapper;

    @Override
    public Map<String, Object> queryTagInfo(int page, int size) {
        int start = (page - 1) * size;
        List<Tag> tags = tagMapper.getTags(start, size);
        Map<String, Object> map = new HashMap<>();
        int ruleTag = tagMapper.getRuleTag();
        int localTag = tagMapper.getLocalTag();
        int totalPage = (ruleTag + localTag) % size == 0 ? (ruleTag + localTag) / size : (ruleTag + localTag) / size + 1;
        map.put("ruleTag", ruleTag);
        map.put("localTag", localTag);
        map.put("totalPage", totalPage);
        map.put("tagList", tags);
        return map;
    }

    @Override
    public void addTag(Tag tag, String token) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(new Date(System.currentTimeMillis()));
        String username = (String) JWTUtil.parseToken(token).get("username");
        tag.setCreatedBy(username);
        tag.setDateCreated(date);
        tagMapper.addTag(tag);
    }

    @Override
    public void deleteTag(int tagId) {
        tagMapper.deleteTag(tagId);
    }

    @Override
    public void updateTag(Tag tag) {
        tagMapper.updateTag(tag);
    }

    @Override
    public Map<String, Object> queryTagDetail(int tagId) {
        Map<String, Object> tagMap = new HashMap<>();
        Tag tag = tagMapper.queryTagDetail(tagId);
        if (tag != null) {
            tagMap.put("tagId", tag.getTagId());
            tagMap.put("fansCount", tag.getFansCount());
            tagMap.put("tagName", tag.getTagName());
            tagMap.put("type", tag.getType());
            if (tag.getType()==2){
                RuleTag ruleTag = tagMapper.queryRuleTag(tagId);
                Map<String, String> ruleTagMap = new HashMap<>();
                if (ruleTag != null) {
                    ruleTagMap.put("subscribeTimeStart", ruleTag.getSubscribeTimeStart());
                    ruleTagMap.put("subscribeTimeEnd", ruleTag.getSubscribeTimeEnd());
                }
                tagMap.put("tagRule", ruleTagMap);
            }
            tagMap.put("description", tag.getDescription());
        }
        return tagMap;
    }
}
