package platform.service;

import entity.FanInfo;

import java.util.Map;

public interface FansService {
    Map<String, Object> getOptions();
    Map<String, Integer> getFansCount(FanInfo fanInfo);
    void fanTagBind(int tagId, FanInfo fanInfo);
}
