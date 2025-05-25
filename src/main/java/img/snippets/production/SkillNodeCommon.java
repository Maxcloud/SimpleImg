package img.snippets.production;

import img.ExpressionEvaluation;
import lombok.Getter;
import lombok.Setter;

/**
 * SkillNodeCommon is a class that represents the common attributes of a common skill node.
 * It contains various attributes such as accuracy, damage, critical rate, etc.
 * The class also provides methods to evaluate these attributes based on the skill level.
 */
@Getter
@Setter
public class SkillNodeCommon implements Cloneable {
    private ExpressionEvaluation expr = new ExpressionEvaluation();

    private int acc;
    private int accX;
    private int actionSpeed;
    private int asrR;
    private int attackCount;
    private int bulletCount;
    private int cooltime;
    private int cr;
    private int criticalDamageMin;
    private int criticalDamageMax;
    private int damage;
    private int damR;
    private int dex;
    private int dexX;
    private int dot;
    private int dotTime;
    private int dotInterval;
    private int epad;
    private int eva;
    private int expR;
    private int hpCon;
    private int ignoreMobpdpR;
    private int intX;
    private int indieAcc;
    private int indieAllStat;
    private int indieEva;
    private int indieJump;
    private int indieMhp;
    private int indieMmp;
    private int indieSpeed;
    private int itemConsume;
    private int itemConsumeNo;
    private int madX;
    private int mastery;
    private int maxLevel;
    private int mddR;
    private int mhpR;
    private int mmpR;
    private int mobCount;
    private int morph;
    private int mpCon;
    private int onlyOnce;
    private int pad;
    private int padX;
    private int pdd;
    private int pddR;
    private int prop;
    private int range;
    private int speed;
    private int strX;
    private int terR;
    private int time;
    private int u;
    private int v;
    private int w;
    private int x;
    private int y;
    private int z;

    public SkillNodeCommon() { }

    public SkillNodeCommon(int level, SkillDataCommon common) {
        this.acc                = expr.evaluate(common.getAcc(), "x", level);
        this.accX               = expr.evaluate(common.getAccX(), "x", level);
        this.actionSpeed        = expr.evaluate(common.getActionSpeed(), "x", level);
        this.asrR               = expr.evaluate(common.getAsrR(), "x", level);
        this.attackCount        = expr.evaluate(common.getAttackCount(), "x", level);
        this.bulletCount        = expr.evaluate(common.getBulletCount(), "x", level);
        this.cooltime           = expr.evaluate(common.getCooltime(), "x", level);
        this.cr                 = expr.evaluate(common.getCr(), "x", level);
        this.criticalDamageMin  = expr.evaluate(common.getCriticalDamageMin(), "x", level);
        this.criticalDamageMax  = expr.evaluate(common.getCriticalDamageMax(), "x", level);
        this.damage             = expr.evaluate(common.getDamage(), "x", level);
        this.damR               = expr.evaluate(common.getDamR(), "x", level);
        this.dex                = expr.evaluate(common.getDex(), "x", level);
        this.dexX               = expr.evaluate(common.getDexX(), "x", level);
        this.dot                = expr.evaluate(common.getDot(), "x", level);
        this.dotTime            = expr.evaluate(common.getDotTime(), "x", level);
        this.dotInterval        = expr.evaluate(common.getDotInterval(), "x", level);
        this.eva                = expr.evaluate(common.getEva(), "x", level);
        this.epad               = expr.evaluate(common.getEpad(), "x", level);
        this.expR               = expr.evaluate(common.getExpR(), "x", level);
        this.hpCon              = expr.evaluate(common.getHpCon(), "x", level);
        this.ignoreMobpdpR      = expr.evaluate(common.getIgnoreMobpdpR(), "x", level);
        this.intX               = expr.evaluate(common.getIntX(), "x", level);
        this.indieAcc           = expr.evaluate(common.getIndieAcc(), "x", level);
        this.indieAllStat       = expr.evaluate(common.getIndieAllStat(), "x", level);
        this.indieEva           = expr.evaluate(common.getIndieEva(), "x", level);
        this.indieJump          = expr.evaluate(common.getIndieJump(), "x", level);
        this.indieMhp           = expr.evaluate(common.getIndieMhp(), "x", level);
        this.indieMmp           = expr.evaluate(common.getIndieMmp(), "x", level);
        this.indieSpeed         = expr.evaluate(common.getIndieSpeed(), "x", level);
        this.itemConsume        = expr.evaluate(common.getItemCon(), "x", level);
        this.itemConsumeNo      = expr.evaluate(common.getItemConNo(), "x", level);
        this.madX               = expr.evaluate(common.getMadX(), "x", level);
        this.mastery            = expr.evaluate(common.getMastery(), "x", level);
        this.mddR               = expr.evaluate(common.getMddR(), "x", level);
        this.mhpR               = expr.evaluate(common.getMhpR(), "x", level);
        this.mmpR               = expr.evaluate(common.getMmpR(), "x", level);
        this.mobCount           = expr.evaluate(common.getMobCount(), "x", level);
        this.morph              = expr.evaluate(common.getMorph(), "x", level);
        this.mpCon              = expr.evaluate(common.getMpCon(), "x", level);
        this.pad                = expr.evaluate(common.getPad(), "x", level);
        this.padX               = expr.evaluate(common.getPadX(), "x", level);
        this.pdd                = expr.evaluate(common.getPdd(), "x", level);
        this.pddR               = expr.evaluate(common.getPddR(), "x", level);
        this.prop               = expr.evaluate(common.getProp(), "x", level);
        this.range              = expr.evaluate(common.getRange(), "x", level);
        this.speed              = expr.evaluate(common.getSpeed(), "x", level);
        this.strX               = expr.evaluate(common.getStrX(), "x", level);
        this.terR               = expr.evaluate(common.getTerR(), "x", level);
        this.time               = expr.evaluate(common.getTime(), "x", level);
        this.u                  = expr.evaluate(common.getU(), "x", level);
        this.v                  = expr.evaluate(common.getV(), "x", level);
        this.w                  = expr.evaluate(common.getW(), "x", level);
        this.x                  = expr.evaluate(common.getX(), "x", level);
        this.y                  = expr.evaluate(common.getY(), "x", level);
        this.z                  = expr.evaluate(common.getZ(), "x", level);
    }

    @Override
    public SkillNodeCommon clone() {
        try {
            return (SkillNodeCommon) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
