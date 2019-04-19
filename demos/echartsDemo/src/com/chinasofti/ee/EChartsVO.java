package com.chinasofti.ee;

import java.util.ArrayList;
import java.util.List;
/**
 * 手写JSON:
 * {
	"titleText":"资金支出统计",
	"titleSubText":"月度统计",
	"titleX":"center",
	"seriesName":"支出",
	"legendData":["通讯","聚餐","买红酒","小龙虾","牛排","游泳"],
	"dataList":[
		{"value":120,"name":"通讯"},
		{"value":320,"name":"聚餐"}
	]
}
程序拼接JSON串
StringBuilder sb = new StringBuilder();

sb.append("{");
sb.append("\"titleText\":\"").append(vo.getTitleText()).append("\",");
sb.append("\"titleSubText\":\"").append(vo.getTitleSubText()).append("\",");
 * @author Administrator
 *
 */
public class EChartsVO {

	private String titleText;

	private String titleSubText;

	private String titleX;

	private String seriesName;
	
	private List<String> legendData=new ArrayList<>();
	
	private  List<EChartsData> dataList=new ArrayList<>();
	
	public void addData(String name,double value,String toolTips){
		dataList.add(new EChartsData(name, value,toolTips));
	}
	
	public void addData(String name,double value){
		dataList.add(new EChartsData(name, value));
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public String getTitleSubText() {
		return titleSubText;
	}

	public void setTitleSubText(String titleSubText) {
		this.titleSubText = titleSubText;
	}

	public String getTitleX() {
		return titleX;
	}

	public void setTitleX(String titleX) {
		this.titleX = titleX;
	}

	public List<EChartsData> getDataList() {
		return dataList;
	}

	public void setDataList(List<EChartsData> dataList) {
		this.dataList = dataList;
	}


	@Override
	public String toString() {
		return "EChartsVO [titleText=" + titleText + ", titleSubText=" + titleSubText + ", titleX=" + titleX
				+ ", dataList=" + dataList + ", seriesName=" + seriesName + "]";
	}


	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}


	public List<String> getLegendData() {
		return legendData;
	}

	public void setLegendData(List<String> legendData) {
		this.legendData = legendData;
	}


	public static class EChartsData {
		private String name;
		private double value;
		private String tooltip;

		public EChartsData(String name, double value) {
			super();
			this.name = name;
			this.value = value;
		}

		public EChartsData(String name, double value, String tooltip) {
			super();
			this.name = name;
			this.value = value;
			this.tooltip = tooltip;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public String getTooltip() {
			return tooltip;
		}

		public void setTooltip(String tooltip) {
			this.tooltip = tooltip;
		}

		@Override
		public String toString() {
			return "EChartsData [name=" + name + ", value=" + value + ", tooltip=" + tooltip + "]";
		}

	}

}
