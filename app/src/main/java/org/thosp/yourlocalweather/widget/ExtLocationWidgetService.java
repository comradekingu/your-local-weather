package org.thosp.yourlocalweather.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import org.thosp.yourlocalweather.R;
import org.thosp.yourlocalweather.model.Weather;
import org.thosp.yourlocalweather.utils.AppPreference;
import org.thosp.yourlocalweather.utils.Constants;
import org.thosp.yourlocalweather.utils.Utils;
import org.thosp.yourlocalweather.utils.WidgetUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.thosp.yourlocalweather.utils.LogToFile.appendLog;

public class ExtLocationWidgetService extends IntentService {

    private static final String TAG = "UpdateExtLocWidgetService";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public ExtLocationWidgetService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        appendLog(this, TAG, "updateWidgetstart");
        Weather weather = AppPreference.getWeather(this);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        ComponentName widgetComponent = new ComponentName(this, ExtLocationWidgetProvider.class);

        int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        for (int appWidgetId : widgetIds) {
            String lastUpdate = Utils.setLastUpdateTime(this, AppPreference
                    .getLastUpdateTimeMillis(this));

            RemoteViews remoteViews = new RemoteViews(this.getPackageName(),
                    R.layout.widget_ext_loc_3x3);

            ExtLocationWidgetProvider.setWidgetTheme(this, remoteViews);
            ExtLocationWidgetProvider.setWidgetIntents(this, remoteViews, ExtLocationWidgetProvider.class);

            remoteViews.setTextViewText(R.id.widget_city, Utils.getCityAndCountry(this));
            remoteViews.setTextViewText(R.id.widget_temperature, AppPreference.getTemperatureWithUnit(
                    this,
                    weather.temperature.getTemp()));
            remoteViews.setTextViewText(R.id.widget_description, Utils.getWeatherDescription(this, weather));
            WidgetUtils.setWind(getBaseContext(), remoteViews, weather.wind.getSpeed());
            WidgetUtils.setHumidity(getBaseContext(), remoteViews, weather.currentCondition.getHumidity());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(1000 * weather.sys.getSunrise());
            WidgetUtils.setSunrise(getBaseContext(), remoteViews, sdf.format(calendar.getTime()));
            calendar.setTimeInMillis(1000 * weather.sys.getSunset());
            WidgetUtils.setSunset(getBaseContext(), remoteViews, sdf.format(calendar.getTime()));
            Utils.setWeatherIcon(remoteViews, this);
            remoteViews.setTextViewText(R.id.widget_last_update, lastUpdate);

            widgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        appendLog(this, TAG, "updateWidgetend");
    }
}
