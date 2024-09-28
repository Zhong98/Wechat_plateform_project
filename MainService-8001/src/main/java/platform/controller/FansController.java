package platform.controller;

import com.google.gson.Gson;
import entity.FanInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import constants.Result;
import constants.ResultCode;
import platform.service.FansService;

import java.util.Map;

@RestController
@RequestMapping(value = "/mp/internet/wechat")
public class FansController {

    @Resource
    private FansService fansService;

    @GetMapping("/fans/option")
    public Result getFansOption() {
        return new Result(ResultCode.SUCCESS, "success", fansService.getOptions());
    }

    @PostMapping("/fans/queryFansInfo")
    public Result queryFansInfo(@RequestBody FanInfo fanInfo) {
        return new Result(ResultCode.SUCCESS, "success", fansService.getFansCount(fanInfo));
    }

    @PostMapping("/tagBindingRule")
    public Result tagBinding(@RequestBody Map<String, Object> tagBindingMap) {
        if (tagBindingMap.get("tagId") != null){
            int tagId = Integer.parseInt(tagBindingMap.get("tagId").toString());
            Gson gson = new Gson();
            FanInfo fanInfo = gson.fromJson(tagBindingMap.get("rule").toString(), FanInfo.class);
            fansService.fanTagBind(tagId, fanInfo);
            return new Result(ResultCode.SUCCESS, "success");
        }
        return new Result(ResultCode.FAILURE, "Please select a tag");
    }
}
