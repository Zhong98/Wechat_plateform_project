package entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FanInfo {
    private int sex;
    private String subscribeScene;
    private String subscribeTimeEnd;
    private String subscribeTimeStart;
    private int bindStatus;
}
