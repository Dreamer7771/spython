package com.anatoly1410.editorapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by 1 on 04.01.2017.
 */
/*Class width basic useful methods
 */
public class UtilityMethods {
    /*Gets distance between two points*/
    public static float getDistance(Point a, Point b) {
        return (float) Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }
    /*Gets file name (with extension) from path*/
    public static String getFileNameFromPath(String path) {
        int lastSlashIdx = path.lastIndexOf('/');
        if (lastSlashIdx < 0)
            return path;
        return path.substring(lastSlashIdx + 1, path.length());
    }
    /*Gets parent directory path from current path
    * If there is no parent directory in the current path, returns path without changes*/
    public static String getParentDirectoryPath(String path) {
        int lastSlashIdx = path.lastIndexOf('/');
        if (lastSlashIdx > 0) {
            path = path.substring(0, lastSlashIdx);
        }
        return path;
    }
    /*Shows OK dialog*/
    public static void showOKInfoMessage(String title, String message, Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static <T> int binarySearch(ArrayList<T> list, T keyElement, Comparator<T> comparator) {
        int lo = 0;
        int hi = list.size() - 1;
        int mid = 0;
        while (lo <= hi) {
            mid = lo + (hi - lo) / 2;
            if (comparator.compare(keyElement, list.get(mid)) < 0) {
                hi = mid - 1;
            } else if (comparator.compare(keyElement, list.get(mid)) > 0) {
                lo = mid + 1;
            } else {
                return mid;
            }
        }
        return mid;
    }
    //Checks if substr [start, end) is word of text - sequence of characters, bounded by \W (non-alphabetic symbols)
    public static boolean isTextIsWord(String text, int start, int end){
        boolean left_char_is_non_alpha = false;
        boolean right_char_is_non_alpha = false;
        if(start == 0){
            left_char_is_non_alpha = true;
        }else if(!Character.isLetter((int)text.charAt(start - 1))){
            left_char_is_non_alpha = true;
        }
        if(end == text.length()){
            right_char_is_non_alpha = true;
        }else if(!Character.isLetter((int)text.charAt(end))){
            right_char_is_non_alpha = true;
        }
        return left_char_is_non_alpha && right_char_is_non_alpha;
    }

    public static boolean isWhitespaceString(String s){
        for(int i=0;i < s.length();++i){
            char c = s.charAt(i);
            if(!Character.isWhitespace(c)){
                return false;
            }
        }
        return true;
    }
}
