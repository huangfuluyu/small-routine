package com.wechat.routine.pojo;


import com.wechat.routine.common.BaseTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("满意度问卷")
public class RoutineScore extends BaseTable {

  @ApiModelProperty("用户id")
  private Integer userId;

  @ApiModelProperty("工单号")
  private String workId;

  @ApiModelProperty("单位名称")
  private String unitName;

  @ApiModelProperty("整体满意度")
  private Integer totalScore;

  @ApiModelProperty("处理及时性")
  private Integer timelyScore;

  @ApiModelProperty("服务专业性")
  private Integer professionalScore;

  @ApiModelProperty("备注")
  private String remarks;

}
