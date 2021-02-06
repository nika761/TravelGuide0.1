package travelguideapp.ge.travelguide.utility;

import android.content.Context;
import android.content.SharedPreferences;

import travelguideapp.ge.travelguide.helper.language.GlobalLanguages;
import travelguideapp.ge.travelguide.model.customModel.AppSettings;
import travelguideapp.ge.travelguide.model.response.AppSettingsResponse;
import travelguideapp.ge.travelguide.model.response.LoginResponse;
import travelguideapp.ge.travelguide.model.response.ProfileResponse;

import com.google.android.exoplayer2.C;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static travelguideapp.ge.travelguide.network.ApiEndPoint.ACCESS_TOKEN_BEARER;

public class GlobalPreferences {

    public static final String FACEBOOK = "facebook";
    public static final String GOOGLE = "google";
    public static final String TRAVEL_GUIDE = "travel_guide";

    private static final String LANGUAGE_PREFERENCES = "LANGUAGE_PREFERENCES";
    private static final String GLOBAL_LANGUAGE_KEY = "GLOBAL_LANGUAGE_KEY";

    private static final String TRAVEL_GUIDE_PREFERENCES = "travel_guide_preference";
    private static final String LANGUAGE_KEY = "language_Id";
    private static final String LANGUAGE_LOCALE_KEY = "language";
    private static final String USER_KEY = "users";
    private static final String LOGGED_USER_KEY = "logged_user";
    private static final String ACCESS_KEY = "access_token";
    private static final String PROFILE_KEY = "user_profile";
    private static final String USER_ID_KEY = "user_id";
    private static final String USER_ROLE_KEY = "user_role";
    private static final String PLATFORM_KEY = "platform_key";
    private static final String APP_VERSION_KEY = "app_version_key";
    private static final String FIRST_USE = "first_use";
    private static final String APP_SETTINGS_KEY = "app_settings_key";


    private static SharedPreferences getPref(Context context) {
        return context.getSharedPreferences(TRAVEL_GUIDE_PREFERENCES, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getLanguagePref(Context context) {
        return context.getSharedPreferences(LANGUAGE_PREFERENCES, Context.MODE_PRIVATE);
    }

    /// User Role

    public static void saveUserRole(Context context, int userRole) {
        getPref(context).edit().putInt(USER_ROLE_KEY, userRole).apply();
    }

    public static void removeUserRole(Context context) {
        getPref(context).edit().remove(USER_ROLE_KEY).apply();
    }

    public static int getUserRole(Context context) {
        int userRole = 20;
        if (getPref(context).getInt(USER_ROLE_KEY, 20) != 20) {
            userRole = getPref(context).getInt(USER_ROLE_KEY, 20);
        }
        return userRole;
    }

    /// User Id

    public static void saveUserId(Context context, int userId) {
        getPref(context).edit().putInt(USER_ID_KEY, userId).apply();
    }

    public static void removeUserId(Context context) {
        getPref(context).edit().remove(USER_ID_KEY).apply();
    }

    public static int getUserId(Context context) {
        int userId = 0;
        if (getPref(context).getInt(USER_ID_KEY, 0) != 0) {
            userId = getPref(context).getInt(USER_ID_KEY, 0);
        }
        return userId;
    }

    ///Login Type

    public static void saveLoginType(Context context, String platform) {
        getPref(context).edit().putString(PLATFORM_KEY, platform).apply();
    }

    public static void removeLoginType(Context context) {
        getPref(context).edit().remove(PLATFORM_KEY).apply();
    }

    public static String getLoginType(Context context) {
        String platform = null;
        if (getPref(context).getString(PLATFORM_KEY, null) != null) {
            platform = getPref(context).getString(PLATFORM_KEY, null);
        }
        return platform;
    }

    ///Language Id

    public static void saveLanguageId(Context context, int languageId) {
        getPref(context).edit().putInt(LANGUAGE_KEY, languageId).apply();
    }

    public static int getLanguageId(Context context) {
        int languageId = 0;
        if (getPref(context).getInt(LANGUAGE_KEY, 0) != 0) {
            languageId = getPref(context).getInt(LANGUAGE_KEY, 0);
        }
        return languageId;
    }

    public static void saveLanguage(Context context, String language) {
        getPref(context).edit().putString(LANGUAGE_LOCALE_KEY, language).apply();
    }

    public static String getLanguage(Context context) {
        String language = "en";
        if (getPref(context).getString(LANGUAGE_LOCALE_KEY, null) != null) {
            return getPref(context).getString(LANGUAGE_LOCALE_KEY, null);
        } else
            return language;
    }

    ///AccessToken

    public static void saveAccessToken(Context context, String accessToken) {
        String access = ACCESS_TOKEN_BEARER + accessToken;
        getPref(context).edit().putString(ACCESS_KEY, access).apply();
    }

    public static void removeAccessToken(Context context) {
        getPref(context).edit().remove(ACCESS_KEY).apply();
    }

    public static String getAccessToken(Context context) {
        String accessToken = null;

        if (getPref(context).getString(ACCESS_KEY, null) != null) {
            accessToken = getPref(context).getString(ACCESS_KEY, null);
        }

        return accessToken;
    }


    public static void saveIsFirstUse(Context context, boolean firsUse) {
        getPref(context).edit().putBoolean(FIRST_USE, firsUse).apply();
    }


    public static boolean getIseFirstUse(Context context) {
        return getPref(context).getBoolean(FIRST_USE, true);
    }


    public static void saveAppVersion(Context context, int appVersion) {
        getPref(context).edit().putInt(APP_VERSION_KEY, appVersion).apply();
    }

    public static void removeAppVersion(Context context) {
        getPref(context).edit().remove(APP_VERSION_KEY).apply();
    }

    public static int getAppVersion(Context context) {
        int appVersion = 0;
        if (getPref(context).getInt(APP_VERSION_KEY, 0) != 0) {
            appVersion = getPref(context).getInt(APP_VERSION_KEY, 0);
        }
        return appVersion;
    }

    public static void saveCurrentLanguage(Context context, GlobalLanguages globalLanguages) {
        getLanguagePref(context).edit().putString(GLOBAL_LANGUAGE_KEY, new Gson().toJson(globalLanguages)).apply();
    }

    public static GlobalLanguages getCurrentLanguage(Context context) {
        SharedPreferences sharedPreferences = getLanguagePref(context);
        Gson gson = new Gson();
        return gson.fromJson(sharedPreferences.getString(GLOBAL_LANGUAGE_KEY, null), GlobalLanguages.class);
    }

    ///

    public static void saveUser(Context context, LoginResponse.User user) {
        List<LoginResponse.User> users = getSavedUsers(context);
        if (!users.contains(user)) {
            users.add(user);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(TRAVEL_GUIDE_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_KEY, new Gson().toJson(users));
        editor.apply();
    }

    public static void saveCurrentUser(Context context, LoginResponse.User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TRAVEL_GUIDE_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGGED_USER_KEY, new Gson().toJson(user));
        editor.apply();
    }

    public static LoginResponse.User getCurrentUser(Context context) {
        LoginResponse.User user;
        SharedPreferences sharedPreferences = context.getSharedPreferences(TRAVEL_GUIDE_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type listType = new TypeToken<LoginResponse.User>() {
        }.getType();
        if (sharedPreferences.getString(LOGGED_USER_KEY, null) != null) {
            user = gson.fromJson(sharedPreferences.getString(LOGGED_USER_KEY, null), listType);
            return user;
        }
        return null;
    }

    public static List<LoginResponse.User> getSavedUsers(Context context) {
        List<LoginResponse.User> users = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(TRAVEL_GUIDE_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<LoginResponse.User>>() {
        }.getType();
        if (sharedPreferences.getString(USER_KEY, null) != null) {
            users = gson.fromJson(sharedPreferences.getString(USER_KEY, null), listType);
        }
        return users;
    }

    public static void deleteUser(Context context, LoginResponse.User user) {
        List<LoginResponse.User> users = getSavedUsers(context);
        users.remove(user);
        SharedPreferences sharedPreferences = context.getSharedPreferences(TRAVEL_GUIDE_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_KEY, new Gson().toJson(users));
        editor.apply();
    }

    public static void saveAppSettings(Context context, AppSettings appSettings) {
        getPref(context).edit().putString(APP_SETTINGS_KEY, new Gson().toJson(appSettings)).apply();
    }

    public static AppSettings getAppSettings(Context context) {
        SharedPreferences sharedPreferences = getPref(context);
        Gson gson = new Gson();
        return gson.fromJson(sharedPreferences.getString(APP_SETTINGS_KEY, null), AppSettings.class);
    }

    public static void saveUserProfileInfo(Context context, ProfileResponse.Userinfo userinfo) {
        getPref(context).edit().putString(PROFILE_KEY, new Gson().toJson(userinfo)).apply();
    }

    public static ProfileResponse.Userinfo getUserProfileInfo(Context context) {
        SharedPreferences sharedPreferences = getPref(context);
        Gson gson = new Gson();
        return gson.fromJson(sharedPreferences.getString(PROFILE_KEY, null), ProfileResponse.Userinfo.class);
    }

}
