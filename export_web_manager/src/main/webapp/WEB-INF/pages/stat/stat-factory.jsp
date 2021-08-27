<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../base.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
</head>
<body>
<div id="frameContent" class="content-wrapper" style="margin-left:0px;">
    <section class="content-header">
        <h1>
            统计分析
            <small>厂家销量统计</small>
        </h1>
    </section>
    <section class="content">
        <div class="box box-primary">
            <div id="main" style="width:90%;height:600px;"></div>
        </div>
    </section>
</div>
</body>
<script src="/plugins/jQuery/jquery-2.2.3.min.js"></script>
<script src="/plugins/echarts/echarts.min.js"></script>
<script type="text/javascript">

    /*异地请求 显示图表*/
    $(function (){
        $.ajax({
            url:"/stat/getFactoryData",
            dataType:"json",
            type:"post",
            success:function (result){
                initCharts(result);
            }
        })
    })

    function initCharts(result){


        // 指定图表的配置项和数据
        //基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('main'));

        //指定图表的配置项和数据
        option = {
            title: {
                text: '生产厂家销售情况',
                subtext: '',
                left: 'center'
            },
            tooltip: {
                trigger: 'item'
            },
            legend: {
                orient: 'vertical',
                left: 'left',
            },
            series: [
                {
                    name: '厂家销量',
                    type: 'pie',
                    radius: '50%',
                    data: result,
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ]
        };

        //使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    }

</script>
</html>