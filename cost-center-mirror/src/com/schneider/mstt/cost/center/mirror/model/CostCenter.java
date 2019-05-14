package com.schneider.mstt.cost.center.mirror.model;

import com.schneider.mstt.cost.center.mirror.enums.Action;
import com.schneider.mstt.cost.center.mirror.enums.Status;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CostCenter {
    private String internalCostCenterId;
    private String secondMirrorGccId;
    private String secondMirrorGccRe;
    private Status status;
    private String lastUpdateBy;
    private Date lastUpdate;
    private Action action;
}
