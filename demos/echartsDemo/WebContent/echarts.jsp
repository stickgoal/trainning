<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<title>echarts demo</title>
	<script type="text/javascript" src="js/echarts.js"></script>
</head>
<body>
    <div id="main" style="width: 600px;height:400px;"></div>

<script type="text/javascript">
	        var myChart = echarts.init(document.getElementById('main'));
// 指定图表的配置项和数据
// http://echarts.baidu.com/option.html#series-pie.radius
// http://echarts.baidu.com/examples/#chart-type-pie
       option = {
        title : {
            text: '${echartVO.titleText}',
            subtext: '${echartVO.titleSubText}',
            x:'${echartVO.titleX}'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            x : 'center',
            y : 'bottom',
            data:[
		<c:forEach items="${echartVO.legendData}" var="d">
			'${d}',
		</c:forEach>
            	]
        },
        toolbox: {
            show : true,
            feature : {
                mark : {show: true},
                dataView : {show: true, readOnly: false},
                magicType : {
                    show: true, 
                    type: ['pie', 'funnel']
                },
                restore : {show: true},
                saveAsImage : {show: true}
            }
        },
        calculable : true,
        series : [
            {
                name:'${echartVO.seriesName}',
                type:'pie',
                radius : [40, 100],
                center : ['50%', 200],
                roseType : 'radius',
                width: '30%',       // for funnel
                max: 40,            // for funnel
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
                data:[
                 <c:forEach items="${echartVO.dataList}" var="d">
                 	{value:'${d.value}',name:'${d.name}'<c:if test="${d.tooltip!=null}">,tooltip:'${d.tooltip}'</c:if>},
                 </c:forEach>
                ]
            }
        
    ]
};
                    

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);


</script>
</body>
</html>