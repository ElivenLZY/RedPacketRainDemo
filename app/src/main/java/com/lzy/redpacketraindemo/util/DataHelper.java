package com.lzy.redpacketraindemo.util;

import com.lzy.redpacketraindemo.pojo.ActiveDayConfigModel;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {

    public static ActiveDayConfigModel getData() {
        ActiveDayConfigModel activeDayConfigModel = new ActiveDayConfigModel();
        activeDayConfigModel.Duration = 10;
        List<ActiveDayConfigModel.Unit> dataList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            ActiveDayConfigModel.Unit unit = new ActiveDayConfigModel.Unit();
            unit.Code = i + "";
            unit.Duration = RandomUtils.getRandom(300, 1500) / 1000.00f;
            unit.Type = RandomUtils.getRandom(1, 5);
            unit.Result = RandomUtils.getRandom(2);
            dataList.add(unit);
        }
        activeDayConfigModel.Units = dataList;
        return activeDayConfigModel;
    }
}
