package com.headfirstlabs.nasadailyimage;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NasaDailyImage extends ActionBarActivity {
	// Handler handler;
	ProgressDialog dialog;
	IotdHandler2 io = null;
	Handler handler;
	Bitmap image = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		refreshFromFeed();
		setContentView(R.layout.activity_nasa_daily_image);

	}

	public void onRefresh(View view) {
		refreshFromFeed();
	}

	public void onSetWallpaper(View view) {
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				WallpaperManager wall = WallpaperManager
						.getInstance(NasaDailyImage.this);

				try {

					if (image != null) {
						wall.setBitmap(image);
					}

					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							// Siempre comunico cambios en la interfaz mediante
							// handler, porque el hilo prinicipal es el unico
							// que acutualiza la pantalla

							Toast.makeText(NasaDailyImage.this,
									"Wallpaper set", Toast.LENGTH_SHORT).show();
						}
					});

				} catch (Exception e) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(NasaDailyImage.this,
									"Error inesperado", Toast.LENGTH_SHORT)
									.show();
						}
					});

				}

			}
		});
		th.start();
	}

	public void resetDisplay(String title, String date, Bitmap imageUrl,
			StringBuffer description) {

		TextView titleView = (TextView) findViewById(R.id.imageTitle);
		titleView.setText(title);

		TextView dateView = (TextView) findViewById(R.id.imageDate);
		dateView.setText(date);

		ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
		imageView.setImageBitmap(imageUrl);

		TextView descriptionView = (TextView) findViewById(R.id.image_description);
		descriptionView.setText(description);
	}

	private void refreshFromFeed() {
		dialog = ProgressDialog.show(this, "Cargando", "Incializando");
		Hilo hilo = null;
		io=null;
		if (io == null) {
			io = new IotdHandler2();
			hilo = new Hilo("Proceso2", io);
			hilo.start();
		}

		try {
			if (hilo != null) {

				//El hilo principal no sigue hasta que termine el secundario
				hilo.join();
				
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						resetDisplay(io.getTitle(), io.getDate(), io.getImage(),
								io.getDescription());
						image = io.getImage();
						dialog.dismiss();

					}
				});
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	private void refreshFromFeed2() {
		dialog = ProgressDialog.show(this, "Cargando", "Incializando");
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (io == null) {
					io = new IotdHandler2();
				}

				io.processFeed();
			}
		});

		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					Thread.sleep(3500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				resetDisplay(io.getTitle(), io.getDate(), io.getImage(),
						io.getDescription());
				image = io.getImage();
				dialog.dismiss();

			}
		});
		th.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nasa_daily_image, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
