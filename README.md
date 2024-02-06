<div>
  <div id="bg1" class="bgbox"></div>
  <div id="bg2" class="bgbox"></div>
</div>
<center><h1>玩家背包查看</h1></center>

插件版本: 1.19 Bukkit 

构建方法: 直接Jar它

作者: <a href="http://syzygy.top">Lacrus工作室</a> ZMZZFLY(ZephyrZeno)

<center><h1>PlayerInventoryDisplay Plugin</h1></center>

Plugin Version: Bukkit 1.19

Way to jar it: Jar it Directly

Author: <a href="http://syzygy.top">LacrusStudio</a> ZMZZFLY(ZephyrZeno)
<hr>
LacrusStudio
<style>
  body,html{
    margin: 0;
}

.bgbox{
    width: 100vw;
    height: 1000vw;
    position: fixed;
    top: 0;
}

#bg1 {
    z-index: -1;
    background-image: url("http://syzygy.top/img/Background/5.jpg");
    animation-name: diybg;
    animation-timing-function: ease-in-out;
    animation-iteration-count: infinite;
    animation-duration: 15s;
    animation-direction: alternate;
}
#bg2 {
    z-index: -2;background-image: url("http://syzygy.top/img/Background/8.jpg");
    nimation-name: diybg;
}
@keyframes diybg{
    0% {
        opacity: 1;
    }
    25% {
        opacity: 0;
    }
    50% {
        opacity: 0;
    }
    75% {
        opacity: 1;
    }
}
</style>
