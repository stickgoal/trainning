<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>echarts demo</title>
<script type="text/javascript" src="js/echarts.js"></script>
<script src="js/jquery-1.12.4.min.js"></script>	
</head>
<body>
	<div id="main" style="width: 600px; height: 400px;"></div>

	<button id="in">收入</button>
	<button id="out">支出</button>

	<script type="text/javascript">
		var myChart = echarts.init(document.getElementById('main'));
		option = {
			title : {
				text : '资金支出情况',
				subtext : '月度统计',
				x : 'center'
			},
			tooltip : {
				trigger : 'item',
				formatter : "{a} <br/>{b} : {c} ({d}%)"
			},
			legend : {
				x : 'center',
				y : 'bottom',
				data : []
			},
			toolbox : {
				show : true,
				feature : {
					mark : {
						show : true
					},
					dataView : {
						show : true,
						readOnly : false
					},
					magicType : {
						show : true,
						type : [ 'pie', 'funnel' ]
					},
					restore : {
						show : true
					},
					saveAsImage : {
						show : true
					}
				}
			},
			calculable : true,
			series : [ {
				name : '',
				type : 'pie',
				radius : [ 40, 100 ],
				center : [ '50%', 200 ],
				roseType : 'radius',
				width : '30%', // for funnel
				max : 40, // for funnel
				itemStyle : {
					normal : {
						label : {
							show : true
						},
						labelLine : {
							show : true
						}
					},
					emphasis : {
						label : {
							show : true
						},
						labelLine : {
							show : true
						}
					}
				},
				data : []
			}

			]
		};

		// 使用刚指定的配置项和数据显示图表。
		myChart.setOption(option);
		myChart.showLoading();
		
		//启动时加载
		getData("out");
		
		//绑定按钮点击时加载
		$(function(){
			$("#in").click(function(){
				myChart.showLoading();
				getData("in");
			});
			$("#out").click(function(){
				myChart.showLoading();
				getData("out");
			});
		})
		
		function getData(direction){
			$.get("echartsAjax",{direction:direction}).done(function(result) {
				myChart.hideLoading();

				myChart.setOption({
					title : {
						text : result.titleText,
						subtext : result.subTitleText,
						x : result.titleX
					},
					 legend: {
			                data:result.legendData
			            },
					series : [{
						name:result.seriesName,
						 data:result.dataList
					}]
				});
			})
		}
		
		
	</script>
</body>
</html>