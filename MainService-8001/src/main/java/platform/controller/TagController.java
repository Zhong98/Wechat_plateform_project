package platform.controller;

import entity.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import constants.Result;
import constants.ResultCode;
import platform.service.TagService;

import java.util.Map;


@RequestMapping("/mp/internet/wechat/tag")
@RestController
public class TagController {

    @Resource
    private TagService tagService;

    @GetMapping
    public Result queryTag(Integer page, Integer pageSize) {
        if (page == null || pageSize == null) {
            return new Result(ResultCode.FAILURE, "Page or page size can't be null");
        }
        Map<String, Object> tagInfo = tagService.queryTagInfo(page, pageSize);
        return new Result(ResultCode.SUCCESS, "success", tagInfo);
    }

    @PostMapping
    public Result addTag(@RequestBody Tag tag, HttpServletRequest request) {
        if (tag == null) {
            return new Result(ResultCode.FAILURE, "Tag can't be null");
        }
        String token = request.getHeader("token");
        tagService.addTag(tag, token);
        return new Result(ResultCode.SUCCESS, "success");
    }

    @PutMapping("/{tagId}")
    public Result updateTag(@PathVariable Integer tagId, @RequestBody Tag tag) {
        if (tagId == null || tag == null) {
            return new Result(ResultCode.FAILURE, "Tag or TagId can't be null");
        }
        tag.setTagId(tagId);
        tagService.updateTag(tag);
        return new Result(ResultCode.SUCCESS, "success");
    }

    @DeleteMapping("/{tagId}")
    public Result deleteTag(@PathVariable Integer tagId) {
        if (tagId == null) {
            return new Result(ResultCode.FAILURE, "tagId can't be null");
        }
        tagService.deleteTag(tagId);
        return new Result(ResultCode.SUCCESS, "success");
    }

    @GetMapping("/detail")
    public Result queryTagDetail(Integer tagId) {
        if (tagId == null) {
            return new Result(ResultCode.FAILURE, "tagId can't be null");
        }
        Map<String, Object> tagMap = tagService.queryTagDetail(tagId);
        if (tagMap.isEmpty()) {
            return new Result(ResultCode.FAILURE, "Can't find tag detail, please check the tag ID");
        }
        return new Result(ResultCode.SUCCESS, "Tag detail query executed successfully", tagMap);
    }
}
