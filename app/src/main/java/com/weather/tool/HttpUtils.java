package com.weather.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.weather.R;
import com.weather.entity.City;
import com.weather.entity.District;
import com.weather.entity.Province;
import com.weather.json.entity.Index;
import com.weather.json.entity.Result;
import com.weather.json.entity.Weather;
import com.weather.json.entity.Weather_data;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtils {
//android访问网络，获取json数据，然后解析显示
	public static String getURl(String location) {
		//请求地址
		String url = "http://api.map.baidu.com/telematics/v3/weather?location="
				+ location + "&output=json&ak=B95329fb7fdda1e32ba3e3a245193146";
		return url;
	}

	public static String getJsonStr(String url) {
		HttpGet get = new HttpGet(url);//创建httpget对象
		HttpClient client = new DefaultHttpClient();//创建httpclient对象
		HttpResponse response;
		try {
			//发送get请求
			response = client.execute(get);
			//如果服务器成功返回响应
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream in = response.getEntity().getContent();
				return getResult(in);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap getImage(String imageUrl) {
		HttpGet get = new HttpGet(imageUrl);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				//读取网络响应response的数据
				InputStream in = response.getEntity().getContent();
				Bitmap bm = BitmapFactory.decodeStream(in);
				return bm;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getJsonStr2(String url) {
		URL url2 = null;
		try {
			url2 = new URL(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) url2.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");

			if (conn.getResponseCode() == 200) {
				InputStream in = conn.getInputStream();
				return getResult(in);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getResult(InputStream in) {
		//创建一个字节数组输出流对象
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//定义了一个byte类型的数组,数组长度为1024
		byte[] b = new byte[1024];
		int len = 0;
		try {
			while ((len = in.read(b)) != -1) {//接收屏幕输入，存入b,同时读取的个数赋值给len
				bos.write(b, 0, len);//屏幕输出b
				bos.flush();//刷新流
			}
			return new String(bos.toByteArray(), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Weather fromJson2(String jsonStr) {
		return null;
	}

	public static Weather fromJson(String jsonStr) {
		try {
			JSONObject obj = new JSONObject(jsonStr);
			String error = obj.getString("error");
			String status = obj.getString("status");
			String date = obj.getString("date");
			// 根据具体的error值返回不同的信息，这儿做了统一处理，返回null;
			if (Integer.parseInt(error) != 0) {
				return null;
			} else {
				Weather wea = new Weather();
				wea.setError(error);
				wea.setStatus(status);
				wea.setDate(date);
				// results信息
				List<Result> results = new ArrayList<Result>();//实例化一个动态数组
				JSONArray rArr = obj.getJSONArray("results");
				for (int i = 0; i < rArr.length(); i++) {
					JSONObject rObj = rArr.getJSONObject(i);
					Result res = new Result();
					res.setCurrentCity(rObj.getString("currentCity"));
					res.setPm25(rObj.getString("pm25"));
					// index信息
					List<Index> index = new ArrayList<Index>();
					JSONArray iArr = rObj.getJSONArray("index");
					for (int j = 0; j < iArr.length(); j++) {
						JSONObject iObj = iArr.getJSONObject(i);
						Index ind = new Index();
						ind.setTitle(iObj.getString("title"));
						ind.setZs(iObj.getString("zs"));
						ind.setTipt(iObj.getString("tipt"));
						ind.setDes(iObj.getString("des"));
						index.add(ind);
					}
					res.setIndex(index);
					// weather_data信息
					List<Weather_data> weather_data = new ArrayList<Weather_data>();
					JSONArray wArr = rObj.getJSONArray("weather_data");
					for (int j = 0; j < wArr.length(); j++) {
						JSONObject wObj = wArr.getJSONObject(j);
						Weather_data wd = new Weather_data();
						wd.setDate(wObj.getString("date"));
						wd.setDayPictureUrl(wObj.getString("dayPictureUrl"));
						wd.setNightPictureUrl(wObj.getString("nightPictureUrl"));
						wd.setWeather(wObj.getString("weather"));
						wd.setWind(wObj.getString("wind"));
						wd.setTemperature(wObj.getString("temperature"));
						weather_data.add(wd);
					}
					res.setWeather_data(weather_data);
					res.setIndex(index);
					results.add(res);
				}
				wea.setResults(results);
				return wea;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 解析xml文件
	public static List<Province> getProvinces(Context context)
			throws XmlPullParserException, IOException {

		List<Province> provinces = null;
		Province province = null;
		List<City> citys = null;
		City city = null;
		List<District> districts = null;
		District district = null;
        //读取原始资源资源
		InputStream in = context.getResources().openRawResource(
				R.raw.citys_weather);
        //构造工厂实例
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = factory.newPullParser();//创建解析对象

		parser.setInput(in, "utf-8");
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {//尾节点
			switch (event) {
				case XmlPullParser.START_DOCUMENT://第一个节点
					provinces = new ArrayList<Province>();
					break;
				case XmlPullParser.START_TAG://其他的首部节点，p pn c cn..
					String tagName = parser.getName();
					if ("p".equals(tagName)) {
						province = new Province();
						citys = new ArrayList<City>();
						int count = parser.getAttributeCount();
						for (int i = 0; i < count; i++) {
							String attrName = parser.getAttributeName(i);
							String attrValue = parser.getAttributeValue(i);
							if ("p_id".equals(attrName))
								province.setId(attrValue);
						}
					}
					if ("pn".equals(tagName)) {
						province.setName(parser.nextText());
					}
					if ("c".equals(tagName)) {
						city = new City();
						districts = new ArrayList<District>();
						int count = parser.getAttributeCount();
						for (int i = 0; i < count; i++) {
							String attrName = parser.getAttributeName(i);
							String attrValue = parser.getAttributeValue(i);
							if ("c_id".equals(attrName))
								city.setId(attrValue);
						}
					}
					if ("cn".equals(tagName)) {
						city.setName(parser.nextText());
					}
					if ("d".equals(tagName)) {
						district = new District();
						int count = parser.getAttributeCount();
						for (int i = 0; i < count; i++) {
							String attrName = parser.getAttributeName(i);
							String attrValue = parser.getAttributeValue(i);
							if ("d_id".equals(attrName))
								district.setId(attrValue);
						}
						district.setName(parser.nextText());
						districts.add(district);
					}
					break;
				case XmlPullParser.END_TAG://其他的尾部的节点 </p> </c> .
					if ("c".equals(parser.getName())) {
						city.setDisList(districts);
						citys.add(city);
					}
					if ("p".equals(parser.getName())) {
						province.setCitys(citys);
						provinces.add(province);
					}

					break;

			}
			event = parser.next();

		}
		return provinces;
	}

	public static List<Map<String, Object>> toListMap(Result r) {
		//从result中获取到天气信息，并将image转化为bitmap存入list
		return null;
	}
}
