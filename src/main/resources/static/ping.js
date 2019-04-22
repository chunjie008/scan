$(function () {
    var maxCount = 0;
    var socket;
    var beforeNode;
    if (typeof(WebSocket) == "undefined") {
        layui.use('layer', function () {
            layer.alert('您的浏览器不支持WebSocket，请您使用chrome或者firefox');
        });
    } else {
        socket = new WebSocket("ws://localhost:8888/ping/9527");
        //打开事件
        socket.onopen = function () {

        };
        //获得消息事件
        socket.onmessage = function (msg) {
            disposeMessage(msg);
        };
        //关闭事件
        socket.onclose = function () {
            layui.use('layer', function () {
                layer.alert("websocket已关闭，请关闭此窗口");
            });
        };
        //发生了错误事件
        socket.onerror = function () {
            layui.use('layer', function () {
                layer.alert("Socket发生了错误");
            });
        }
    }

    /**
     * 处理获取的消息
     * @param msg
     */
    function disposeMessage(msg){
        var data = $.parseJSON( msg.data );
        switch (data.type){
            case "PING_PROGRESS":
                var pingNodeResult = data.pingNodeResult;
                var id = pingNodeResult.id.replace(".","_").replace(".","_").replace(".","_").replace(".","_");
                var body = pingNodeResult.currentBody;
                $("#"+id+"_current").html(body);
                var ending = pingNodeResult.ending;
                $("#"+id+"_result").html("发送"+ending.sent+"次，"+"成功"+ending.received+"次，"+"失败"+ending.lostCount+"次，"+"丢包率"+ending.lost+"，最小用时："+ending.minimum+"，最大用时"+ending.maximum+"，平均用时"+ending.average+"。");
                sortTr(id,pingNodeResult.ending.sent);
                break;
            case "SOCKET":
            var message = data.msg;
            if(ALL_END==message){
                $("#recommend_line").show();
                var id = data.pingNodeResult.id.replace(".","_").replace(".","_").replace(".","_").replace(".","_");
                var name = $("#"+id+"_name").html();
                $("#recommend").html(name);
                $("#start").click();
            }
            if(OVERLOAD==message){
                layui.use('layer', function () {
                    layer.alert('您的任务过多，可能有部分任务无法执行');
                });
            }
            break;
            // case "INIT_NODE_LIST":
            //     updateNode(data.data);
            //     break;
            case "OVERLENGTH":
                layui.use('layer', function () {
                    layer.open({
                        content: '您在其他窗口打开了此页面，此页面的任务已经全部结束，请关闭此窗口',
                        yes: function(index){
                            layer.close(index); //如果设定了yes回调，需进行手工关闭
                            window.location.href="about:blank";
                            window.close();
                        },
                        end: function(){
                            window.location.href="about:blank";
                            window.close();
                        }
                    });
                });
                break;
        }
    }

    /**
     * tr排序
     */
    function sortTr(id,msg){
        var num = parseInt(msg);
        if(num>maxCount){
            if($("#"+id+"_tr").prev()==null){
                return ;
            }
            $("#"+id+"_tr").insertBefore($("#tbodyIp tr:first"));
            beforeNode = $("#"+id+"_tr");
            maxCount = num;
        }
        if(num==maxCount){
            $("#"+id+"_tr").insertAfter(beforeNode);
            beforeNode = $("#"+id+"_tr");
        }
    }

    var runing = false;
    /**
     * 绑定开始按钮事件
     */
    $("#start").bind("click", function () {
        if (runing) {
            $(this).removeClass("layui-btn-warm");
            $(this).html("开始检测");
            var obj = {"head":PING_END};
            socket.send(JSON.stringify(obj));
            maxCount = 0;
            runing = false;
        } else {
            var count = $("#count").val();
            if(count==""||count==null){
                count=5;
                $("#count").val("5");
            }
            var timeout = $("#timeout").val();
            if(timeout==""||timeout==null){
                timeout=1000;
                $("#timeout").val("1000");
            }
            var byteInt = $("#byteInt").val();
            if(byteInt==""||byteInt==null){
                byteInt=1024;
                $("#byteInt").val("1024");
            }
            if(count<1||count>1000||!(isPositiveInteger(count))){
                layui.use('layer', function () {
                    layer.alert("次数必须是 1 至 1000的正整数");
                });
                return ;
            }
            if(byteInt<1||byteInt>1024||!(isPositiveInteger(byteInt))){
                layui.use('layer', function () {
                    layer.alert("字节数必须是 1 至 1024的正整数");
                });
                return ;
            }
            var ipList = $("#iplist").val();
            var ipArr = ipList.split(",");
            if(ipList.length<=0){
                layui.use('layer', function () {
                    layer.alert('您必须填入ip地址');
                });
                return ;
            }
            updateList(ipList);
            $(this).addClass("layui-btn-warm");
            $(this).html("结束扫描");
            $("#recommend").html("...");
            var obj = {"head":PING_START,"count":count,"timeout":timeout,"byteInt":byteInt,"nodeList":ipArr};
            socket.send(JSON.stringify(obj));
            runing = true;
        }
    });
    /**
     * 绑定结束按钮事件
     */
    $("#reset").bind("click", function () {
        $("#iplist").html("");
    });

    function updateList(ipList){
        var ipArr = ipList.split(",");
        $("#tbodyIp").empty();
        for(var ip in ipArr){
            var ipString = ipArr[ip].replace(".","_").replace(".","_").replace(".","_").replace(".","_");
            var tr = $("<tr id='"+ipString+"_tr' ></tr>");
            $("#tbodyIp").append(tr);
            var ipTd = $("<td id='"+ipString+"_name'>"+ipArr[ip]+"</td>");
            var ipCurrent = $("<td id='"+ipString+"_current'></td>");
            var ipResult = $("<td id='"+ipString+"_result'>"+"等待结果"+"</td>");
            tr.append(ipTd);
            tr.append(ipCurrent);
            tr.append(ipResult);
        }
    }

    function isPositiveInteger(s){//是否为正整数
        var re = /^[0-9]+$/ ;
        return re.test(s)
    }

})