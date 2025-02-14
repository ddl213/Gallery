package com.example.pagergallery.unit.util;

import android.util.Log;

import java.util.Arrays;
import java.util.WeakHashMap;

public class LogUtil {
    private static final int MIN_STACK_DEPTH = 4; // 最低遍历深度
    private static final int MAX_SEARCH_DEPTH = 8; // 最大搜索深度
    private static volatile String baseTag = "myLogD";
    private static final WeakHashMap<Integer, String> TAG_CACHE = new WeakHashMap<>();

    private static String getCallerClassName() {
        StackTraceElement[] stack = new Throwable().getStackTrace();

        // 动态适配不同JDK版本
        for (int i = MIN_STACK_DEPTH; i < Math.min(stack.length, MAX_SEARCH_DEPTH); i++) {
            String className = stack[i].getClassName();
            if (!className.startsWith(LogUtil.class.getName())) {
                return className;
            }
        }
        return "Unknown";
    }

    private static String generateFinalTag(String customTag) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        int key = Arrays.hashCode(stack);

        return TAG_CACHE.computeIfAbsent(key, k -> {
            String className = getCallerClassName();
            String simpleName = className.substring(className.lastIndexOf('.') + 1)
                    .replaceAll("\\$\\d+", ""); // 处理内部类
            return (customTag != null ? customTag : baseTag) + ":" + simpleName;
        });
    }

    private static String getSimpleClassName(String fullClassName) {
        int start = fullClassName.lastIndexOf('.');
        int end = fullClassName.indexOf('$');
        return start == -1 ? fullClassName : fullClassName.substring(start + 1,end);
    }

    // 基础日志方法
    public static void d(String msg) {
        Log.d(generateFinalTag(null), msg);
    }

    public static void e(String msg) {
        Log.e(generateFinalTag(null), msg);
    }

    // 带自定义标签的重载方法
    public static void d(String tag, String msg) {
        Log.d(generateFinalTag(tag), msg);
    }

    public static void e(String tag, String msg) {
        Log.e(generateFinalTag(tag), msg); // 修正此处为Log.e
    }

    // 配置方法
    public static void setBaseTag(String newTag) {
        baseTag = newTag;
    }

    public static void clearCache() {
        TAG_CACHE.clear();
    }
}