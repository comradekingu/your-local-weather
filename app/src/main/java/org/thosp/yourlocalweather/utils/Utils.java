package org.thosp.yourlocalweather.utils;

import android.app.AlarmManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import org.thosp.yourlocalweather.R;
import org.thosp.yourlocalweather.model.Weather;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class Utils {

    public static Bitmap createWeatherIcon(Context context, String text) {
        Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Typeface weatherFont = Typeface.createFromAsset(context.getAssets(),
                                                        "fonts/weathericons-regular-webfont.ttf");

        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(weatherFont);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(AppPreference.getTextColor(context));
        paint.setTextSize(180);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, 128, 200, paint);
        return bitmap;
    }

    public static String getStrIcon(Context context) {
        SharedPreferences weatherPref = context.getSharedPreferences(Constants.PREF_WEATHER_NAME,
                Context.MODE_PRIVATE);
        Set<String> weatherDataIcons = weatherPref.getStringSet(Constants.WEATHER_DATA_ICON, new HashSet<String>());
        if (weatherDataIcons.isEmpty()) {
            return "";
        }
        return getStrIcon(context, weatherDataIcons.iterator().next());
    }

    public static String getStrIcon(Context context, Weather weather) {
        return getStrIcon(context, weather.getCurrentWeathers().iterator().next().getIdIcon());
    }

    public static String getStrIcon(Context context, String iconId) {
        if (iconId == null) {
            return "";
        }
        String icon;
        switch (iconId) {
            case "01d":
                icon = context.getString(R.string.icon_clear_sky_day);
                break;
            case "01n":
                icon = context.getString(R.string.icon_clear_sky_night);
                break;
            case "02d":
                icon = context.getString(R.string.icon_few_clouds_day);
                break;
            case "02n":
                icon = context.getString(R.string.icon_few_clouds_night);
                break;
            case "03d":
                icon = context.getString(R.string.icon_scattered_clouds);
                break;
            case "03n":
                icon = context.getString(R.string.icon_scattered_clouds);
                break;
            case "04d":
                icon = context.getString(R.string.icon_broken_clouds);
                break;
            case "04n":
                icon = context.getString(R.string.icon_broken_clouds);
                break;
            case "09d":
                icon = context.getString(R.string.icon_shower_rain);
                break;
            case "09n":
                icon = context.getString(R.string.icon_shower_rain);
                break;
            case "10d":
                icon = context.getString(R.string.icon_rain_day);
                break;
            case "10n":
                icon = context.getString(R.string.icon_rain_night);
                break;
            case "11d":
                icon = context.getString(R.string.icon_thunderstorm);
                break;
            case "11n":
                icon = context.getString(R.string.icon_thunderstorm);
                break;
            case "13d":
                icon = context.getString(R.string.icon_snow);
                break;
            case "13n":
                icon = context.getString(R.string.icon_snow);
                break;
            case "50d":
                icon = context.getString(R.string.icon_mist);
                break;
            case "50n":
                icon = context.getString(R.string.icon_mist);
                break;
            default:
                icon = context.getString(R.string.icon_weather_default);
        }

        return icon;
    }

    public static void setWeatherIcon(ImageView imageView, Context context) {
        if ("weather_icon_set_fontbased".equals(AppPreference.getIconSet(context))) {
            imageView.setImageBitmap(createWeatherIcon(context, getStrIcon(context)));
        } else {
            SharedPreferences weatherPref = context.getSharedPreferences(Constants.PREF_WEATHER_NAME,
                    Context.MODE_PRIVATE);
            imageView.setImageResource(Utils.getWeatherResourceIcon(weatherPref));
        }
    }


    public static void setWeatherIcon(RemoteViews remoteViews, Context context) {
        if ("weather_icon_set_fontbased".equals(AppPreference.getIconSet(context))) {
            remoteViews.setImageViewBitmap(R.id.widget_icon,
                    createWeatherIcon(context, getStrIcon(context)));
        } else {
            SharedPreferences weatherPref = context.getSharedPreferences(Constants.PREF_WEATHER_NAME,
                    Context.MODE_PRIVATE);
            remoteViews.setImageViewResource(R.id.widget_icon, Utils.getWeatherResourceIcon(weatherPref));
        }
    }

    public static int getWeatherResourceIcon(SharedPreferences weatherPref) {
        Set<String> weatherIds = weatherPref.getStringSet(Constants.WEATHER_DATA_WEATHER_ID, new HashSet<String>());
        if (weatherIds.isEmpty()) {
            return R.drawable.ic_weather_set_1_31;
        }
        int weatherId = Integer.parseInt(weatherIds.iterator().next());
        float temperature = weatherPref.getFloat(Constants.WEATHER_DATA_TEMPERATURE, 0);
        float wind = weatherPref.getFloat(Constants.WEATHER_DATA_WIND_SPEED, 0);
        long sunrise = weatherPref.getLong(Constants.WEATHER_DATA_SUNRISE, 0);
        long sunset = weatherPref.getLong(Constants.WEATHER_DATA_SUNSET, 0);
        boolean strongWind = wind > 5;
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long timeNow = now.getTimeInMillis() / 1000;
        boolean day = (sunrise < timeNow) && (timeNow < sunset);
        switch (weatherId) {
            case 800:
                if (day) {
                    if (temperature > 30) {
                        return R.drawable.ic_weather_set_1_36;
                    } else {
                        return R.drawable.ic_weather_set_1_32;
                    }
                } else {
                    return R.drawable.ic_weather_set_1_31;
                }
            case 801:
                if (day)
                    return R.drawable.ic_weather_set_1_34;
                else
                    return R.drawable.ic_weather_set_1_33;
            case 802:
                if (day)
                    return R.drawable.ic_weather_set_1_30;
                else
                    return R.drawable.ic_weather_set_1_29;
            case 803:
                if (day)
                    return R.drawable.ic_weather_set_1_28;
                else
                    return R.drawable.ic_weather_set_1_27;
            case 804:
                return R.drawable.ic_weather_set_1_26;
            case 300:
            case 500:
                if (day)
                    return R.drawable.ic_weather_set_1_39;
                else
                    return R.drawable.ic_weather_set_1_45;
            case 301:
            case 302:
            case 310:
            case 501:
                return R.drawable.ic_weather_set_1_11;
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:
            case 502:
            case 503:
            case 504:
            case 520:
            case 521:
            case 522:
            case 531:
                return R.drawable.ic_weather_set_1_12;
            case 511:
                if (strongWind)
                    return R.drawable.ic_weather_set_1_10;
                else
                    return R.drawable.ic_weather_set_1_08;
            case 701:
                if (day)
                    return R.drawable.ic_weather_set_1_22;
                else
                    return R.drawable.ic_weather_set_1_21;
            case 711:
            case 721:
            case 731:
            case 741:
            case 751:
            case 761:
                return R.drawable.ic_weather_set_1_20;
            case 762:
                return R.drawable.ic_weather_set_1_na;
            case 771:
            case 781:
                return R.drawable.ic_weather_set_1_24;
            case 200:
            case 210:
            case 230:
                if (day)
                    return R.drawable.ic_weather_set_1_38;
                else
                    return R.drawable.ic_weather_set_1_45;
            case 201:
            case 202:
            case 211:
            case 212:
            case 221:
            case 231:
            case 232:
                return R.drawable.ic_weather_set_1_17;
            case 600:
                return R.drawable.ic_weather_set_1_13;
            case 601:
                if (strongWind)
                    return R.drawable.ic_weather_set_1_15;
                else
                    return R.drawable.ic_weather_set_1_14;
            case 602:
                if (strongWind)
                    return R.drawable.ic_weather_set_1_15;
                else
                    return R.drawable.ic_weather_set_1_16;
            case 611:
            case 615:
            case 620:
                return R.drawable.ic_weather_set_1_05;
            case 612:
            case 616:
            case 621:
            case 622:
                return R.drawable.ic_weather_set_1_42;
            case 900:
            case 901:
            case 902:
                return R.drawable.ic_weather_set_1_24;
            case 903:
                return R.drawable.ic_weather_set_1_na;
            case 904:
                return R.drawable.ic_weather_set_1_36;
            case 905:
                return R.drawable.ic_weather_set_1_24;
            case 906:
                return R.drawable.ic_weather_set_1_18;
            case 951:
                return R.drawable.ic_weather_set_1_26;
            case 952:
            case 953:
            case 954:
            case 955:
            case 956:
            case 957:
            case 958:
            case 959:
            case 960:
            case 961:
            case 962:
            default:
                return R.drawable.ic_weather_set_1_24;
        }
    }

    public static String setLastUpdateTime(Context context, long lastUpdate) {
        Date lastUpdateTime = new Date(lastUpdate);
        return DateFormat.getTimeFormat(context).format(lastUpdateTime) + " " + AppPreference.getUpdateSource(context);
    }

    public static long intervalMillisForAlarm(String intervalMinutes) {
        int interval = Integer.parseInt(intervalMinutes);
        switch (interval) {
            case 15:
                return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            case 30:
                return AlarmManager.INTERVAL_HALF_HOUR;
            case 60:
                return AlarmManager.INTERVAL_HOUR;
            case 720:
                return AlarmManager.INTERVAL_HALF_DAY;
            case 1440:
                return AlarmManager.INTERVAL_DAY;
            default:
                return interval * 60 * 1000;
        }
    }

    public static String unixTimeToFormatTime(Context context, long unixTime) {
        long unixTimeToMillis = unixTime * 1000;
        return DateFormat.getTimeFormat(context).format(unixTimeToMillis);
    }

    public static void copyToClipboard(Context context, String string) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(
                Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(string, string);
        clipboardManager.setPrimaryClip(clipData);
    }

    public static String windDegreeToDirections(Context context, double windDegree) {
        String[] directions = context.getResources().getStringArray(R.array.wind_directions);
        String[] arrows = context.getResources().getStringArray(R.array.wind_direction_arrows);
        int index = (int) Math.abs(Math.round(windDegree % 360) / 45);

        return directions[index] + " " + arrows[index];
    }

    public static URL getWeatherForecastUrl(String endpoint, String lat, String lon, String units, String lang) throws
                                                                                         MalformedURLException {
        String url = Uri.parse(endpoint)
                        .buildUpon()
                        .appendQueryParameter("appid", ApiKeys.OPEN_WEATHER_MAP_API_KEY)
                        .appendQueryParameter("lat", lat)
                        .appendQueryParameter("lon", lon)
                        .appendQueryParameter("units", units)
                        .appendQueryParameter("lang", "cs".equalsIgnoreCase(lang)?"cz":lang)
                        .build()
                        .toString();
        return new URL(url);
    }
    
    public static void getAndWriteAddressFromGeocoder(Geocoder geocoder,
                                                      Address address,
                                                      String latitude,
                                                      String longitude,
                                                      boolean resolveAddressByOS,
                                                      Context context) {
        try {
            String latitudeEn = latitude.replace(",", ".");
            String longitudeEn = longitude.replace(",", ".");
            if (resolveAddressByOS) {
                List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitudeEn), Double.parseDouble(longitudeEn), 1);
                if((addresses != null) && (addresses.size() > 0)) {
                    address = addresses.get(0);
                }
            }
            if(address != null) {
                SharedPreferences mSharedPreferences = context.getSharedPreferences(Constants.APP_SETTINGS_NAME,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(Constants.APP_SETTINGS_ADDRESS_FOUND, true);
                if((address.getLocality() != null) && !"".equals(address.getLocality())) {
                    editor.putString(Constants.APP_SETTINGS_GEO_CITY, address.getLocality());
                } else {
                    editor.putString(Constants.APP_SETTINGS_GEO_CITY, address.getSubAdminArea());
                }
                editor.putString(Constants.APP_SETTINGS_GEO_COUNTRY_NAME, address.getCountryName());
                if(address.getAdminArea() != null) {
                    editor.putString(Constants.APP_SETTINGS_GEO_DISTRICT_OF_COUNTRY, address.getAdminArea());
                } else {
                    editor.putString(Constants.APP_SETTINGS_GEO_DISTRICT_OF_COUNTRY, null);
                }
                editor.putString(Constants.APP_SETTINGS_GEO_DISTRICT_OF_CITY, address.getSubLocality());
                editor.apply();
            } else {
                setNoLocationFound(context);
            }
        } catch (IOException | NumberFormatException ex) {
            Log.e(Utils.class.getName(), "Unable to get address from latitude and longitude", ex);
        }
    }

    public static void setNoLocationFound(Context context) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(Constants.APP_SETTINGS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Constants.APP_SETTINGS_ADDRESS_FOUND, false);
        editor.putString(Constants.APP_SETTINGS_GEO_CITY, context.getString(R.string.location_not_found));
        editor.putString(Constants.APP_SETTINGS_GEO_COUNTRY_NAME, "");
        editor.putString(Constants.APP_SETTINGS_GEO_DISTRICT_OF_COUNTRY, "");
        editor.putString(Constants.APP_SETTINGS_GEO_DISTRICT_OF_CITY, "");
        long now = System.currentTimeMillis();
        editor.putLong(Constants.LAST_UPDATE_TIME_IN_MS, now);
        //editor.putLong(Constants.LAST_LOCATION_UPDATE_TIME_IN_MS, now); don't set this time because of other resource
        editor.apply();
    }

    public static String getCityAndCountry(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.APP_SETTINGS_NAME, 0);
        if(AppPreference.isUpdateLocationEnabled(context)) {
            return getCityAndCountryFromGeolocation(preferences);
        } else {
            return getCityAndCountryFromPreference(context);
        }
    }

    public static String getWeatherDescription(Context context, Weather weather) {
        if(AppPreference.hideDescription(context)) {
            return " ";
        }
        StringBuilder currentWeatherDescription = new StringBuilder();
        boolean first = true;
        for (Weather.CurrentWeather currentWeather: weather.getCurrentWeathers()) {
            if (!first) {
                currentWeatherDescription.append(", ");
            }
            currentWeatherDescription.append(capitalizeFirstLetter(currentWeather.getDescription()));
            first = false;
        }
        return currentWeatherDescription.toString();
    }

    public static String getWeatherDescription(Context context) {
        if(AppPreference.hideDescription(context)) {
            return " ";
        }
        SharedPreferences weatherPref = context.getSharedPreferences(Constants.PREF_WEATHER_NAME,
                Context.MODE_PRIVATE);
        Set<String> weatherDescriptions = weatherPref.getStringSet(Constants.WEATHER_DATA_DESCRIPTION, new HashSet<String>());
        StringBuilder currentWeatherDescription = new StringBuilder();
        boolean first = true;
        for (Iterator<String> weatherIterator = weatherDescriptions.iterator(); weatherIterator.hasNext(); ) {
            if (!first) {
                currentWeatherDescription.append(", ");
            }
            currentWeatherDescription.append(capitalizeFirstLetter(weatherIterator.next()));
            first = false;
        }
        return currentWeatherDescription.toString();
    }

    private static String capitalizeFirstLetter(String input) {
        if ((input == null) || (input.length() < 1)) {
            return "";
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private static String getCityAndCountryFromGeolocation(SharedPreferences preferences) {
        String geoCountryName = preferences.getString(Constants.APP_SETTINGS_GEO_COUNTRY_NAME, "");
        String geoCity = preferences.getString(Constants.APP_SETTINGS_GEO_CITY, "");
        String geoDistrictOfCity = preferences.getString(Constants.APP_SETTINGS_GEO_DISTRICT_OF_CITY, "");
        if ("".equals(geoDistrictOfCity) || geoCity.equalsIgnoreCase(geoDistrictOfCity)) {
            String geoCountryDistrict = preferences.getString(Constants.APP_SETTINGS_GEO_DISTRICT_OF_COUNTRY, "");
            if ((geoCountryDistrict == null) || "".equals(geoCountryDistrict) || geoCity.equals(geoCountryDistrict)) {
                return formatLocalityToTwoLines((("".equals(geoCity))?"":(geoCity)) + (("".equals(geoCountryName))?"":(", " + geoCountryName)));
            }
            return formatLocalityToTwoLines((("".equals(geoCity))?"":(geoCity + ", ")) + geoCountryDistrict + (("".equals(geoCountryName))?"":(", " + geoCountryName)));
        }
        return formatLocalityToTwoLines((("".equals(geoCity))?"":(geoCity + " - ")) + geoDistrictOfCity + (("".equals(geoCountryName))?"":(", " + geoCountryName)));
    }
    
    private static String formatLocalityToTwoLines(String inputLocation) {
        if (inputLocation.length() < 30) {
            return inputLocation;
        }
        if (inputLocation.indexOf(",") < 30) {
            inputLocation.replaceFirst(", ", "\n");
        }
        return inputLocation;
    }
    
    private static String getCityAndCountryFromPreference(Context context) {
        String[] cityAndCountryArray = AppPreference.getCityAndCode(context);
        return cityAndCountryArray[0] + ", " + cityAndCountryArray[1];
    }
}
