package platform.service.impl;

import entity.FanInfo;
import entity.Option;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import platform.mapper.FanMapper;
import platform.service.FansService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FansServiceImpl implements FansService {
    @Resource
    FanMapper fanMapper;

    @Override
    public Map<String, Object> getOptions() {
        Map<String, Object> optionMap = new HashMap<>();

        optionMap.put("sex", createOptions(
                new String[]{"Unknown", "Male", "Female"},
                new String[]{"0", "1", "2"}
        ));

        optionMap.put("bindStatus", createOptions(
                new String[]{"未绑定", "绑定"},
                new String[]{"0", "1"}
        ));

        optionMap.put("subscribeScene", createOptions(
                new String[]{"公众号搜索", "公众号迁移", "名片分享", "扫描二维码", "图文页内名称点击", "图文页右上角菜单", "支付后关注", "其他"},
                new String[]{"ADD_SCENE_SEARCH", "ADD_SCENE_ACCOUNT_MIGRATION", "ADD_SCENE_PROFILE_CARD", "ADD_SCENE_QR_CODE", "ADD_SCENE_PROFILE_LINK", "ADD_SCENE_PROFILE_ITEM", "ADD_SCENE_PAID", "ADD_SCENE_OTHERS"}
        ));

        return optionMap;
    }

    private List<Option> createOptions(String[] values, String[] codes) {
        List<Option> options = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            Option option = new Option();
            option.setValue(values[i]);
            option.setCode(codes[i]);
            options.add(option);
        }
        return options;
    }

    @Override
    public Map<String, Integer> getFansCount(FanInfo fanInfo) {
        Map<String, Integer> map = new HashMap<>();
        map.put("fansCount", fanMapper.queryFansCount(fanInfo).size());
        return map;
    }

    @Override
    public void fanTagBind(int tagId, FanInfo fanInfo) {
        List<String> openidList = fanMapper.queryFansCount(fanInfo);
        fanMapper.insertFanTagBinding(tagId, openidList);
    }
}
