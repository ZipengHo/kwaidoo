package basic.cmn.dto.vote;

import gpf.adur.data.DataType;
import gpf.anotation.FieldMeta;
import gpf.dc.dto.NestingDto;

import java.io.Serializable;

/**
 * 投票选项
 *
 */
public class VoteOptionDto extends NestingDto implements Serializable{
    public final static String FormModelId = "gpf.md.slave.VoteOption";
    public final static String FieldCode_Option = "syuan3Siang4";
    public final static String sOption = "选项";
    public final static String FieldCode_Description = "shuo1Ming2";
    public final static String sDescription = "说明";
    public final static String FieldCode_VoteCount = "dang1Cian2De2Piao4Shu4";
    public final static String sVoteCount = "当前得票数";
    public final static String FieldCode_Weight = "cyuan2Jhong4";
    public final static String sWeight = "权重";
    @FieldMeta(code = FieldCode_Option,name = sOption, dataType = DataType.Text)
    String option;
    @FieldMeta(code = FieldCode_Description,name = sDescription, dataType = DataType.Text)
    String description;
    @FieldMeta(code = FieldCode_VoteCount,name = sVoteCount, dataType = DataType.Long)
    Long voteCount;
    @FieldMeta(code = FieldCode_Weight,name = sWeight, dataType = DataType.Text)
    String weight;

    public String getOption() {
        return option;
    }

    public VoteOptionDto setOption(String option) {
        this.option = option;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VoteOptionDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public VoteOptionDto setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public VoteOptionDto setWeight(String weight) {
        this.weight = weight;
        return this;
    }
}
