package org.metawatch.manager.widgets;

import java.util.ArrayList;
import java.util.Map;

import org.metawatch.manager.FontCache;
import org.metawatch.manager.MetaWatchService;
import org.metawatch.manager.MetaWatchService.Preferences;
import org.metawatch.manager.Monitors.LocationData;
import org.metawatch.manager.Monitors.WeatherData;
import org.metawatch.manager.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;

public class WeatherWidget implements InternalWidget {
	public final static String id_0 = "weather_24_32";
	final static String desc_0 = "Current Weather (24x32)";
	
	public final static String id_1 = "weather_96_32";
	final static String desc_1 = "Current Weather (96x32)";
	
	public final static String id_2 = "weather_fc_96_32";
	final static String desc_2 = "Weather Forecast (96x32)";
	
	public final static String id_3 = "moon_24_32";
	final static String desc_3 = "Moon Phase (24x32)";
	
	public final static String id_4 = "weather_80_16";
	final static String desc_4 = "Current Weather (80x16)";
	
	public final static String id_5 = "moon_16_16";
	final static String desc_5 = "Moon Phase (16x16)";
	
	public final static String id_6 = "weather_fc_80_16";
	final static String desc_6 = "Weather Forecast (80x16)";
	
	private Context context = null;
	private TextPaint paintSmall;
	private TextPaint paintSmallOutline;
	private TextPaint paintLarge;
	private TextPaint paintLargeOutline;
	private TextPaint paintSmallNumerals;
	private TextPaint paintSmallNumeralsOutline;
	
	public void init(Context context, ArrayList<CharSequence> widgetIds) {
		this.context = context;

		paintSmall = new TextPaint();
		paintSmall.setColor(Color.BLACK);
		paintSmall.setTextSize(FontCache.instance(context).Small.size);
		paintSmall.setTypeface(FontCache.instance(context).Small.face);
		
		paintSmallOutline = new TextPaint();
		paintSmallOutline.setColor(Color.WHITE);
		paintSmallOutline.setTextSize(FontCache.instance(context).Small.size);
		paintSmallOutline.setTypeface(FontCache.instance(context).Small.face);
		
		paintLarge = new TextPaint();
		paintLarge.setColor(Color.BLACK);
		paintLarge.setTextSize(FontCache.instance(context).Large.size);
		paintLarge.setTypeface(FontCache.instance(context).Large.face);
		
		paintLargeOutline = new TextPaint();
		paintLargeOutline.setColor(Color.WHITE);
		paintLargeOutline.setTextSize(FontCache.instance(context).Large.size);
		paintLargeOutline.setTypeface(FontCache.instance(context).Large.face);
		
		paintSmallNumerals = new TextPaint();
		paintSmallNumerals.setColor(Color.BLACK);
		paintSmallNumerals.setTextSize(FontCache.instance(context).SmallNumerals.size);
		paintSmallNumerals.setTypeface(FontCache.instance(context).SmallNumerals.face);
		
		paintSmallNumeralsOutline = new TextPaint();
		paintSmallNumeralsOutline.setColor(Color.WHITE);
		paintSmallNumeralsOutline.setTextSize(FontCache.instance(context).SmallNumerals.size);
		paintSmallNumeralsOutline.setTypeface(FontCache.instance(context).SmallNumerals.face);
	}

	public void shutdown() {
		paintSmall = null;
	}

	public void refresh(ArrayList<CharSequence> widgetIds) {
	}

	public void get(ArrayList<CharSequence> widgetIds, Map<String,WidgetData> result) {
		
		if(context ==null)
			return;
		
		if(widgetIds == null || widgetIds.contains(id_0)) {
			InternalWidget.WidgetData widget = new InternalWidget.WidgetData();
			
			widget.id = id_0;
			widget.description = desc_0;
			widget.width = 24;
			widget.height = 32;
			
			widget.bitmap = draw0();
			widget.priority = calcPriority();
			
			result.put(widget.id, widget);
		}
		
		if(widgetIds == null || widgetIds.contains(id_1)) {
			InternalWidget.WidgetData widget = new InternalWidget.WidgetData();
			
			widget.id = id_1;
			widget.description = desc_1;
			widget.width = 96;
			widget.height = 32;
			
			widget.bitmap = draw1();
			widget.priority = calcPriority();
			
			result.put(widget.id, widget);
		}
		
		if(widgetIds == null || widgetIds.contains(id_2)) {
			InternalWidget.WidgetData widget = new InternalWidget.WidgetData();
			
			widget.id = id_2;
			widget.description = desc_2;
			widget.width = 96;
			widget.height = 32;
			
			widget.bitmap = draw2();
			widget.priority = calcPriority();
			
			result.put(widget.id, widget);
		}
		
		if(widgetIds == null || widgetIds.contains(id_3)) {
			InternalWidget.WidgetData widget = new InternalWidget.WidgetData();
			
			widget.id = id_3;
			widget.description = desc_3;
			widget.width = 24;
			widget.height = 32;
			
			widget.bitmap = draw3();
			widget.priority = WeatherData.moonPercentIlluminated !=-1 ? calcPriority() : -1;
			
			result.put(widget.id, widget);
		}
		
		if(widgetIds == null || widgetIds.contains(id_4)) {
			InternalWidget.WidgetData widget = new InternalWidget.WidgetData();
			
			widget.id = id_4;
			widget.description = desc_4;
			widget.width = 80;
			widget.height = 16;
			
			widget.bitmap = draw4();
			widget.priority = calcPriority();
			
			result.put(widget.id, widget);
		}
		
		if(widgetIds == null || widgetIds.contains(id_5)) {
			InternalWidget.WidgetData widget = new InternalWidget.WidgetData();
			
			widget.id = id_5;
			widget.description = desc_5;
			widget.width = 16;
			widget.height = 16;
			
			widget.bitmap = draw5();
			widget.priority = WeatherData.moonPercentIlluminated !=-1 ? calcPriority() : -1;
			
			result.put(widget.id, widget);
		}
		
		if(widgetIds == null || widgetIds.contains(id_6)) {
			InternalWidget.WidgetData widget = new InternalWidget.WidgetData();
			
			widget.id = id_6;
			widget.description = desc_6;
			widget.width = 80;
			widget.height = 16;
			
			widget.bitmap = draw6();
			widget.priority = calcPriority();
			
			result.put(widget.id, widget);
		}
	}
	
	private int calcPriority()
	{
		if(Preferences.weatherProvider == MetaWatchService.WeatherProvider.DISABLED)
			return -1;
		
		return WeatherData.received ? 1 : 0;
	}
		
	private Bitmap draw0() {
		Bitmap bitmap = Bitmap.createBitmap(24, 32, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		
		if (WeatherData.received && WeatherData.forecast!=null && WeatherData.forecast.length>0) {
			
			// icon
			Bitmap image = Utils.loadBitmapFromAssets(context, WeatherData.icon);
			canvas.drawBitmap(image, 0, 4, null);
								
			// temperatures
			if (WeatherData.celsius) {
				Utils.drawOutlinedText(WeatherData.temp+"�C", canvas, 0, 7, paintSmall, paintSmallOutline);
			}
			else {
				Utils.drawOutlinedText(WeatherData.temp+"�F", canvas, 0, 7, paintSmall, paintSmallOutline);
			}
			paintLarge.setTextAlign(Paint.Align.LEFT);
						
			Utils.drawOutlinedText("H "+WeatherData.forecast[0].tempHigh, canvas, 0, 25, paintSmall, paintSmallOutline);
			Utils.drawOutlinedText("L "+WeatherData.forecast[0].tempLow, canvas, 0, 31, paintSmall, paintSmallOutline);
									
		} else {
			paintSmall.setTextAlign(Paint.Align.CENTER);

			canvas.drawText("Wait", 12, 16, paintSmall);

			paintSmall.setTextAlign(Paint.Align.LEFT);
		}
		
		return bitmap;
	}
	
	private Bitmap draw1() {
		Bitmap bitmap = Bitmap.createBitmap(96, 32, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		
		if (WeatherData.received) {
			
			// icon
			Bitmap image = Utils.loadBitmapFromAssets(context, WeatherData.icon);
			if (Preferences.overlayWeatherText)
				canvas.drawBitmap(image, 36, 5, null);
			else
				canvas.drawBitmap(image, 34, 1, null);
			
			// condition
			if (Preferences.overlayWeatherText)
				Utils.drawWrappedOutlinedText(WeatherData.condition, canvas, 1, 2, 60, paintSmall, paintSmallOutline, Layout.Alignment.ALIGN_NORMAL);
			else
				Utils.drawWrappedOutlinedText(WeatherData.condition, canvas, 1, 2, 34, paintSmall, paintSmallOutline, Layout.Alignment.ALIGN_NORMAL);

			// temperatures
			paintLarge.setTextAlign(Paint.Align.RIGHT);
			paintLargeOutline.setTextAlign(Paint.Align.RIGHT);
			Utils.drawOutlinedText(WeatherData.temp, canvas, 82, 13, paintLarge, paintLargeOutline);
			if (WeatherData.celsius) {
				//RM: since the degree symbol draws wrong...
				canvas.drawText("O", 82, 7, paintSmall);
				canvas.drawText("C", 95, 13, paintLarge);
			}
			else {
				//RM: since the degree symbol draws wrong...
				canvas.drawText("O", 83, 7, paintSmall);
				canvas.drawText("F", 95, 13, paintLarge);
			}
			paintLarge.setTextAlign(Paint.Align.LEFT);
						
			if (WeatherData.forecast!=null && WeatherData.forecast.length>0) {
				canvas.drawText("High", 64, 23, paintSmall);
				canvas.drawText("Low", 64, 31, paintSmall);
				
				paintSmall.setTextAlign(Paint.Align.RIGHT);
				canvas.drawText(WeatherData.forecast[0].tempHigh, 95, 23, paintSmall);
				canvas.drawText(WeatherData.forecast[0].tempLow, 95, 31, paintSmall);
				paintSmall.setTextAlign(Paint.Align.LEFT);
			}

			Utils.drawOutlinedText((String) TextUtils.ellipsize(WeatherData.locationName, paintSmall, 63, TruncateAt.END), canvas, 1, 31, paintSmall, paintSmallOutline);
						
		} else {
			paintSmall.setTextAlign(Paint.Align.CENTER);
			if (Preferences.weatherGeolocation) {
				if( !LocationData.received ) {
					canvas.drawText("Awaiting location", 48, 18, paintSmall);
				}
				else {
					canvas.drawText("Awaiting weather", 48, 18, paintSmall);
				}
			}
			else {
				canvas.drawText("No data", 48, 18, paintSmall);
			}
			paintSmall.setTextAlign(Paint.Align.LEFT);
		}
		
		return bitmap;
	}

	private Bitmap draw2() {
		Bitmap bitmap = Bitmap.createBitmap(96, 32, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		
		paintSmall.setTextAlign(Align.LEFT);
		paintSmallOutline.setTextAlign(Align.LEFT);
		
		if (WeatherData.received && WeatherData.forecast!=null && WeatherData.forecast.length>3) {
			int weatherIndex = 0;
			if(WeatherData.forecast.length>4)
				weatherIndex = 1; // Start with tomorrow's weather if we've got enough entries

			for (int i=0;i<4;++i) {
				int x = i*24;
				Bitmap image = Utils.loadBitmapFromAssets(context, WeatherData.forecast[weatherIndex].icon);
				canvas.drawBitmap(image, x, 4, null);
				Utils.drawOutlinedText(WeatherData.forecast[weatherIndex].day, canvas, x, 6, paintSmall, paintSmallOutline);
				
				Utils.drawOutlinedText("H "+WeatherData.forecast[weatherIndex].tempHigh, canvas, x, 25, paintSmall, paintSmallOutline);
				Utils.drawOutlinedText("L "+WeatherData.forecast[weatherIndex].tempLow, canvas, x, 31, paintSmall, paintSmallOutline);
				
				weatherIndex++;
			}
		} else {
			paintSmall.setTextAlign(Paint.Align.CENTER);
			if (Preferences.weatherGeolocation) {
				if( !LocationData.received ) {
					canvas.drawText("Awaiting location", 48, 18, paintSmall);
				}
				else {
					canvas.drawText("Awaiting weather", 48, 18, paintSmall);
				}
			}
			else {
				canvas.drawText("No data", 48, 18, paintSmall);
			}
			paintSmall.setTextAlign(Paint.Align.LEFT);
		}
		
		return bitmap;
	}
	
	final static int[] phaseImage = {0,0,1,1,1,1,1,2,2,2,3,3,3,3,4,4,4,5,5,5,5,5,6,6,7,7,7,7,0,0,0};
	
	private Bitmap draw3() {
		Bitmap bitmap = Bitmap.createBitmap(24, 32, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		
		paintSmall.setTextAlign(Paint.Align.CENTER);
		
		final boolean shouldInvert = Preferences.invertLCD || (MetaWatchService.watchType == MetaWatchService.WatchType.ANALOG);
		
		if (WeatherData.received && WeatherData.ageOfMoon >=0 && WeatherData.ageOfMoon < phaseImage.length ) {
			int moonPhase = WeatherData.ageOfMoon;
			int moonImage = phaseImage[moonPhase];
			int x = 0-(moonImage*24);
			Bitmap image = shouldInvert ? Utils.loadBitmapFromAssets(context, "moon-inv.bmp") : Utils.loadBitmapFromAssets(context, "moon.bmp");
			canvas.drawBitmap(image, x, 0, null);
			
			canvas.drawText(Integer.toString(WeatherData.moonPercentIlluminated)+"%", 12, 30, paintSmall);
		} else {
			canvas.drawText("Wait", 12, 16, paintSmall);
		}
		
		paintSmall.setTextAlign(Paint.Align.LEFT);
		
		return bitmap;
	}

	private Bitmap draw4() {
		Bitmap bitmap = Bitmap.createBitmap(80, 16, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		
		if (WeatherData.received) {
			
			// icon
			String smallIcon = WeatherData.icon.replace(".bmp", "_12.bmp");
			Bitmap image = Utils.loadBitmapFromAssets(context, smallIcon);	
			canvas.drawBitmap(image, 46, 2, null);
			
			// condition
			Utils.drawWrappedOutlinedText(WeatherData.condition, canvas, 0, 0, 60, paintSmall, paintSmallOutline, Layout.Alignment.ALIGN_NORMAL);
			
			// temperatures
			
			paintSmall.setTextAlign(Paint.Align.RIGHT);
			paintSmallOutline.setTextAlign(Paint.Align.RIGHT);
						
			StringBuilder string = new StringBuilder();
			string.append(WeatherData.temp);
			
			if (WeatherData.celsius) {
				string.append("�C");
			}
			else {
				string.append("�F");
			}
			Utils.drawOutlinedText(string.toString(), canvas, 80, 5, paintSmall, paintSmallOutline);
			
			if (WeatherData.forecast!=null && WeatherData.forecast.length>0) {
				string = new StringBuilder();
				string.append(WeatherData.forecast[0].tempHigh);
				string.append("/");
				string.append(WeatherData.forecast[0].tempLow);
						
				Utils.drawOutlinedText(string.toString(), canvas, 80, 16, paintSmall, paintSmallOutline);
			}
			paintSmall.setTextAlign(Paint.Align.LEFT);
			paintSmallOutline.setTextAlign(Paint.Align.LEFT);
			
			Utils.drawOutlinedText((String) TextUtils.ellipsize(WeatherData.locationName, paintSmall, 47, TruncateAt.END), canvas, 0, 16, paintSmall, paintSmallOutline);
						
		} else {
			paintSmall.setTextAlign(Paint.Align.CENTER);
			if (Preferences.weatherGeolocation) {
				if( !LocationData.received ) {
					canvas.drawText("Awaiting location", 40, 9, paintSmall);
				}
				else {
					canvas.drawText("Awaiting weather", 40, 9, paintSmall);
				}
			}
			else {
				canvas.drawText("No data", 40, 9, paintSmall);
			}
			paintSmall.setTextAlign(Paint.Align.LEFT);
		}
		
		return bitmap;
	}
	
	private Bitmap draw5() {
		Bitmap bitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);

		final boolean shouldInvert = Preferences.invertLCD || (MetaWatchService.watchType == MetaWatchService.WatchType.ANALOG);
		
		paintSmall.setTextAlign(Paint.Align.CENTER);
		if (WeatherData.received && WeatherData.ageOfMoon >=0 && WeatherData.ageOfMoon < phaseImage.length) {
			int moonPhase = WeatherData.ageOfMoon;
			int moonImage = phaseImage[moonPhase];
			int x = 0-(moonImage*16);
			Bitmap image = shouldInvert ? Utils.loadBitmapFromAssets(context, "moon-inv_10.bmp") : Utils.loadBitmapFromAssets(context, "moon_10.bmp");
			canvas.drawBitmap(image, x, 0, null);
		} else {
			canvas.drawText("--", 8, 9, paintSmall);
		}
		paintSmall.setTextAlign(Paint.Align.LEFT);
		
		return bitmap;
	}

	private Bitmap draw6() {
		Bitmap bitmap = Bitmap.createBitmap(80, 16, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		
		paintSmall.setTextAlign(Align.LEFT);
		paintSmallOutline.setTextAlign(Align.LEFT);
		
		if (WeatherData.received && WeatherData.forecast!=null && WeatherData.forecast.length>3) {
			int weatherIndex = 0;
			if(WeatherData.forecast.length>3)
				weatherIndex = 1; // Start with tomorrow's weather if we've got enough entries

			for (int i=0;i<3;++i) {
				int x = i*26;
				final String smallIcon = WeatherData.forecast[weatherIndex].icon.replace(".bmp", "_12.bmp");
				Bitmap image = Utils.loadBitmapFromAssets(context, smallIcon);
				canvas.drawBitmap(image, x+12, 0, null);
				Utils.drawOutlinedText(WeatherData.forecast[weatherIndex].day.substring(0, 2), canvas, x+1, 6, paintSmall, paintSmallOutline);
				
				StringBuilder hilow = new StringBuilder();
				hilow.append(WeatherData.forecast[weatherIndex].tempHigh);
				hilow.append("/");
				hilow.append(WeatherData.forecast[weatherIndex].tempLow);
				
				Utils.drawOutlinedText(hilow.toString(), canvas, x+1, 16, paintSmallNumerals, paintSmallNumeralsOutline);
				
				weatherIndex++;
			}
		} else {
			paintSmall.setTextAlign(Paint.Align.CENTER);
			if (Preferences.weatherGeolocation) {
				if( !LocationData.received ) {
					canvas.drawText("Awaiting location", 40, 8, paintSmall);
				}
				else {
					canvas.drawText("Awaiting weather", 40, 8, paintSmall);
				}
			}
			else {
				canvas.drawText("No data", 40, 8, paintSmall);
			}
			paintSmall.setTextAlign(Paint.Align.LEFT);
		}
		
		return bitmap;
	}

}
