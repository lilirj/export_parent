<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../base.jsp"%>
<!DOCTYPE html>
<html>

<head>
    <!-- 页面meta -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>数据 - AdminLTE2定制版</title>
    <meta name="description" content="AdminLTE2定制版">
    <meta name="keywords" content="AdminLTE2定制版">
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no" name="viewport">
    <!-- 页面meta /-->

</head>
<body>
<div id="frameContent" class="content-wrapper" style="margin-left:0px;">
    <section class="content-header">
        <h1>
            统计分析
            <small>在线人数统计</small>
        </h1>
    </section>
    <section class="content">
        <div class="box box-primary">
            <div id="main" style="width: 90%;height:500px;"></div>
        </div>
    </section>
</div>
</body>

<script src="../plugins/jQuery/jquery-2.2.3.min.js"></script>
<script src="../../plugins/echarts/echarts.min.js"></script>
<script type="text/javascript">

    /*按小时统计访问人数  异步请求*/
    $(function () {
         $.ajax({
             url:"/stat/getOnline",
             dataType:"json",
             type:"post",
             success:function (result){
                initCharts(result);
             }
         })
    })

    function initCharts(result){
        //初始数组
        let name =[];
        let value =[];
        //遍历结果
        for (let i = 0; i < result.length; i++) {
            name[i]=result[i].name;
            value[i]=result[i].value;
        }

        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('main'));
        // 指定图表的配置项和数据
        option = {
            xAxis: {
                type: 'category',
                data: name
            },
            yAxis: {
                type: 'value'
            },
            series: [{
                data: value,
                type: 'line'
            }]
        };
        myChart.setOption(option);
    }
</script>

</html>