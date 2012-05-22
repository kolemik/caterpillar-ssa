<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE helpset PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN" "http://java.sun.com/products/javahelp/helpset_2_0.dtd">
<helpset version="2.0">
    <title>Помощь</title>
    <maps>
        <homeID>help.about</homeID>
        <mapref location="map.jhm"/>
    </maps>
    <view mergetype="javax.help.AppendMerge">
        <name>TOC</name>
        <label>Оглавление</label>
        <type>javax.help.TOCView</type>
        <data>help-toc.xml</data>
        <image>mainIcon</image>
    </view>
    <!--<view mergetype="javax.help.AppendMerge">
        <name>Index</name>
        <label>Предметный указатель</label>
        <type>javax.help.IndexView</type>
        <data>help-index.xml</data>
        <image>indexIcon</image>
    </view>-->
    <view>
        <name>Search</name>
        <label>Поиск</label>
        <type>javax.help.SearchView</type>
        <data engine="com.sun.java.help.search.DefaultSearchEngine">JavaHelpSearch</data>
        <image>searchIcon</image>
    </view>
    <view>
        <name>Favorites</name>
        <label>Избранное</label>
        <type>javax.help.FavoritesView</type>
        <image>favoritesIcon</image>
    </view>

    <presentation default="true">
        <image>helpIcon</image>
        <toolbar>
            <helpaction image="backIcon" text="Назад">javax.help.BackAction</helpaction>
            <helpaction image="forwardIcon" label="Вперед">javax.help.ForwardAction</helpaction>
            <helpaction image="homeIcon" label="Домой">javax.help.HomeAction</helpaction>
            <helpaction image="reloadIcon" label="Обновить">javax.help.ReloadAction</helpaction>
            <helpaction image="favoritesAddIcon" label="Добавить в избранное">javax.help.FavoritesAction</helpaction>
            <helpaction image="printerIcon" label="Печать">javax.help.PrintAction</helpaction>
            <helpaction image="fileIcon" label="Параметры печати">javax.help.PrintSetupAction</helpaction>
        </toolbar>
    </presentation>

</helpset>
