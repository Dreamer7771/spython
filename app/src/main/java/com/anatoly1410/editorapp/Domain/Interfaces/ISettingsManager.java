package com.anatoly1410.editorapp.Domain.Interfaces;

import android.content.SharedPreferences;

/**
 * Created by 1 on 16.05.2017.
 */

public interface ISettingsManager {

    String APP_PREFERENCES = "settings";
    String APP_PREFERENCES_LAST_OPEN_DIR = "last_open_directory";

    //search panel settings
    String APP_PREFERENCES_SEARCH_BAR_WORDS = "search_bar_words";
    String APP_PREFERENCES_SEARCH_BAR_MATCH_CASE = "search_bar_match_case";
    String APP_PREFERENCES_SEARCH_BAR_LOOP = "search_bar_loop_search";
    String APP_PREFERENCES_SEARCH_REG_EXP = "search_bar_reg_exp";

    //code highlight/block extraction settings
    String APP_PREFERENCES_HIGHLIGHT_BY_DEFAULT = "highlight_syntax_def";
    String APP_PREFERENCES_EXTRACT_BLOCKS_BY_DEFAULT = "extract_blocks_def";
    String APP_PREFERENCES_AUTOCOMP_BY_DEFAULT = "autocompletion_def";
    String APP_PREFERENCES_SHOW_CLASSES = "show_classes";
    String APP_PREFERENCES_SHOW_FUNCTIONS = "show_functions";
    String APP_PREFERENCES_SORT_ORDER = "nav_sort_order";

    //maximum commands count in history
    String APP_PREFERENCES_COMMANDS_NUM_BY_DEFAULT = "max_commands_count";

    interface OnSettingsChangedEventListener {
        void fireEvent();
    }
    void setSettingsChangedEventListener(OnSettingsChangedEventListener listener);

    void setSettings(SharedPreferences settings);
    String getLastOpenDirectory();
    void setBooleanSetting(String settingName, boolean value);
    void setIntSetting(String settingName, int value);
    boolean getBooleanSetting(String settingName);
    void setLastOpenDirectory(String path);
    int getIntSetting(String settingName);
}
