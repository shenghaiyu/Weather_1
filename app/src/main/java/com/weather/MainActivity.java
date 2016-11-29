package com.weather;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.weather.entity.City;
import com.weather.entity.District;
import com.weather.entity.Province;
import com.weather.json.entity.Result;
import com.weather.json.entity.Weather;
import com.weather.json.entity.Weather_data;
import com.weather.tool.HttpUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {
	private EditText editText;
	private Button button;
	//声明
	//下拉菜单
	private Spinner sp_province;//省
	private Spinner sp_city;//城市
	private Spinner sp_district;//区，地方
	private int currentPro;
	private ArrayAdapter<Province> provinceAdapter;//省得适配器
	private ArrayAdapter<City> cityAdapter;//城市的适配器
	private ArrayAdapter<District> districtAdapter;//区的适配器
	private List<Province> provinces;
//今天的天气情况
	private TextView tvCity;//城市
	private TextView tvPM25;//pm2.5
	private TextView tvDate;//日期
	private ImageView ivpic11;//图片
	private ImageView ivpic12;//图片
	private TextView tvweek1;//星期
	private TextView tvwea1;//天气
	private TextView tvwind1;//风
	private TextView tvtemper1;//温度
//明天的天气状况
	private ImageView ivpic21;
	private ImageView ivpic22;
	private TextView tvweek2;
	private TextView tvwea2;
	private TextView tvwind2;
	private TextView tvtemper2;
//后天的天气状况
	private ImageView ivpic31;
	private ImageView ivpic32;
	private TextView tvweek3;
	private TextView tvwea3;
	private TextView tvwind3;
	private TextView tvtemper3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editText=(EditText)findViewById(R.id.edittext1);
		button=(Button)findViewById(R.id.button1);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(editText.getText().toString()!="")
				{
					ContentUtil.sendSmsWithBody(MainActivity.this, "", editText.getText().toString());
				}
			}
		});

		/* 以findViewById()取得对象 */
		sp_province = (Spinner) findViewById(R.id.spinner1);
		sp_city = (Spinner) findViewById(R.id.spinner2);
		sp_district = (Spinner) findViewById(R.id.spinner3);
		tvCity = (TextView) findViewById(R.id.tvCity);
		tvPM25 = (TextView) findViewById(R.id.tvPM25);
		tvDate = (TextView) findViewById(R.id.tvDate);

		// 第一个
		ivpic11 = (ImageView) findViewById(R.id.ivpic11);
		ivpic12 = (ImageView) findViewById(R.id.ivpic12);

		tvweek1 = (TextView) findViewById(R.id.tvweek1);
		tvwea1 = (TextView) findViewById(R.id.tvwea1);
		tvwind1 = (TextView) findViewById(R.id.tvwind1);
		tvtemper1 = (TextView) findViewById(R.id.tvtemper1);

		// 第二个城市
		ivpic21 = (ImageView) findViewById(R.id.ivpic21);
		ivpic22 = (ImageView) findViewById(R.id.ivpic22);

		tvweek2 = (TextView) findViewById(R.id.tvweek2);
		tvwea2 = (TextView) findViewById(R.id.tvwea2);
		tvwind2 = (TextView) findViewById(R.id.tvwind2);
		tvtemper2 = (TextView) findViewById(R.id.tvtemper2);

		// 第三个城市
		ivpic31 = (ImageView) findViewById(R.id.ivpic31);
		ivpic32 = (ImageView) findViewById(R.id.ivpic32);

		tvweek3 = (TextView) findViewById(R.id.tvweek3);
		tvwea3 = (TextView) findViewById(R.id.tvwea3);
		tvwind3 = (TextView) findViewById(R.id.tvwind3);
		tvtemper3 = (TextView) findViewById(R.id.tvtemper3);

		// 获取到地区信息
		try {
			provinces = HttpUtils.getProvinces(this);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		// adapter，填充信息
		provinceAdapter = new ArrayAdapter<Province>(this,
				android.R.layout.simple_spinner_item, android.R.id.text1,
				provinces);
		   /* simple_spinner_dropdown_item为自定义下拉菜单样式定义在res/layout目录下 */
		provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    /* 将ArrayAdapter添加Spinner对象中 */
		sp_province.setAdapter(provinceAdapter);



		cityAdapter = new ArrayAdapter<City>(this,
				android.R.layout.simple_spinner_item, android.R.id.text1,
				provinces.get(0).getCitys());
		cityAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_city.setAdapter(cityAdapter);


		districtAdapter = new ArrayAdapter<District>(this,
				android.R.layout.simple_spinner_item, android.R.id.text1,
				provinces.get(0).getCitys().get(0).getDisList());
		districtAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_district.setAdapter(districtAdapter);


		// 当选择省份时，城市和地方列表会变化
		/* 将sp_province添加OnItemSelectedListener */
		sp_province.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				currentPro = position;
				cityAdapter = new ArrayAdapter<City>(MainActivity.this,
						android.R.layout.simple_spinner_item,
						android.R.id.text1, provinces.get(currentPro).getCitys());
				cityAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_city.setAdapter(cityAdapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		// 当选择城市时，地方列表会变化
		sp_city.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				districtAdapter = new ArrayAdapter<District>(MainActivity.this,
						android.R.layout.simple_spinner_item,
						android.R.id.text1, provinces.get(currentPro)
						.getCitys().get(position).getDisList());
				districtAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_district.setAdapter(districtAdapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		// 当选择地方时，显示具体的天气情况
		//当选择具体的地区时，创建异步类，并把地区名称作为参数。
		sp_district.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				// 选择的城市
				District dis = districtAdapter.getItem(position);
				new WeatherAsyncTask().execute(dis.getName());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	// 异步类，获取天气数据

/*一个异步任务是由参数，过程和结果这3个泛型类型定义。
它还包括四个步骤：oPostExecute，doInBackground，onProgressUpdate和onPostexecute。
使用AsyncTask必须定义一个类继承AsyncTask，然后子类中必须实现doInBackground方法，经常也会实现oPostExecute方法。*/

	class WeatherAsyncTask extends AsyncTask<String, Void, Weather> {
		/*在异步类的doInBackground方法中，从接口中获取到天气信息，
        在这儿处理图片我是这么做的，在Weather_data这个类中定义了两个Bitmap类型的属性
        （这两个属性在解析从接口获取到的天气信息的json字符串时时没有用的），
        从网络上获取后给这两个属性赋值，然后将已经封装好的Weather传递给onPostExecute方法*/
		@Override
		protected Weather doInBackground(String... params) {
			String url = HttpUtils.getURl(params[0]);
			String jsonStr = HttpUtils.getJsonStr(url);
			Weather weather = HttpUtils.fromJson(jsonStr);
			Result res = weather.getResults().get(0);
			for(int i = 0;i<3;i++){
				Weather_data w = res.getWeather_data().get(i);
				w.setDayPicture(HttpUtils.getImage(w.getDayPictureUrl()));
				w.setNightPicture(HttpUtils.getImage(w.getNightPictureUrl()));
			}
			return weather;
		}




		@Override
		protected void onPostExecute(Weather result ) {
			Result res = result.getResults().get(0);
			Weather_data wa = res.getWeather_data().get(0);
			tvCity.setText("城市:" + res.getCurrentCity());
			String pm2_5 = "".equals(res.getPm25()) ? "75" : res.getPm25();
			tvPM25.setText("PM2.5:" + pm2_5);
			tvDate.setText("日期:" + result.getDate());

			ivpic11.setImageBitmap(wa.getDayPicture());
			ivpic12.setImageBitmap(wa.getNightPicture());
			String str = wa.getDate();
			tvweek1.setText(str.substring(0, 2));
			tvwea1.setText(wa.getWeather());
			tvwind1.setText(wa.getWind());
			tvtemper1.setText(str.substring(14, str.length()-1));

			wa = res.getWeather_data().get(1);
			// System.out.println(wa2);

			tvtemper2.setText(wa.getTemperature());
			ivpic21.setImageBitmap(wa.getDayPicture());
			ivpic22.setImageBitmap(wa.getNightPicture());
			tvweek2.setText(wa.getDate());
			tvwea2.setText(wa.getWeather());
			tvwind2.setText(wa.getWind());
			tvtemper2.setText(wa.getTemperature());

			wa = res.getWeather_data().get(2);

			// System.out.println(wa4);
			ivpic31.setImageBitmap(wa.getDayPicture());
			ivpic32.setImageBitmap(wa.getNightPicture());
			tvweek3.setText(wa.getDate());
			tvwea3.setText(wa.getWeather());
			tvwind3.setText(wa.getWind());
			tvtemper3.setText(wa.getTemperature());
		}
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
