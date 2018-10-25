package com.lzy.redpacketraindemo.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/1/8.
 */

public class ActiveDayConfigModel implements Serializable{

    private static final long serialVersionUID = 3905698249436230739L;

    public int InvestorId;
    public boolean IsOpenTime;
    public boolean IsFirstVisit;
    public boolean HasPlayed;
    public int Duration;        //秒
    public List<Unit> Units;
    public AwardBean Award;
    public InviteBean Invite;

    public static   class Unit implements Serializable {
        private static final long serialVersionUID = 4465133782161244883L;
        public String Code;
        public int Type;        // 红包类型 1，2，3，4
        public int Result;      // 是否中奖 0:没中奖  1:中奖啦
        public float Duration; //秒
    }
    public  class AwardBean implements Serializable {
        private static final long serialVersionUID = -4893771150086921453L;
        public float CashBonus;
        public int InvestBonus8;
        public int InvestBonus18;
        public int InvestBonus58;
    }
    public  class InviteBean implements Serializable{
        private static final long serialVersionUID = 7254418670167362488L;
        public String Title;
        public String Summary;
        public String ImageLink;
        public String UrlLink;
    }

}
