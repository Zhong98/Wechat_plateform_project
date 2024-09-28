package platform.mapper;

import entity.LastLogin;
import entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Mapper
public interface LoginMapper {
    User findUserByPhone(String phone);

    LastLogin findLastLoginByPhone(String phone);

    void saveLogin(Map<String, Object> loginMap);

    void updatePassword(String newPassword, String phone);

    List<String> findOldPassword(String phone);

    void saveModification(String phone, String password);
}
