package com.paulzzh.yuzu.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "yuzu")
public class YuZuUIConfig {
    @Config.Comment("How shall I greet?")
    @Config.DefaultString("Ciallo～(∠ · ω < )⌒★")
    public static String greeting;

    @Config.Comment("背景音乐")
    @Config.DefaultBoolean(true)
    public static boolean bgm;

    @Config.Comment("语音")
    @Config.DefaultBoolean(true)
    public static boolean voice;

    @Config.Comment("直接退出游戏？(否则返回到原主菜单)")
    @Config.DefaultBoolean(false)
    public static boolean just_exit;
}
