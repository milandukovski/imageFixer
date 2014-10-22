package com.example.imagefixer;

import java.io.File;
import java.io.FileOutputStream;

import java.util.Date;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoEdit extends Activity {
	private static final int PIC_CROP = 2;
	private ImageView mainImageView;
	private Bitmap photo;
	private ImageButton btnCrop, btnSave;
	private Uri picUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_edit);

		init();

		addEvents();
	}

	private void init() {
		Intent intent = this.getIntent();
		Bundle extras = getIntent().getExtras();
		photo = extras.getParcelable("picture");
		picUri= Uri.parse(intent.getStringExtra("picUri"));

		mainImageView = (ImageView) this.findViewById(R.id.ivPhoto);
		btnCrop = (ImageButton) this.findViewById(R.id.btnCrop);
		btnSave = (ImageButton) this.findViewById(R.id.btnSave);

		mainImageView.setImageBitmap(photo);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			if (requestCode == PIC_CROP) {
				Bundle extras = intent.getExtras();
				// get the cropped bitmap
				Bitmap thePic = extras.getParcelable("data");
				// retrieve a reference to the ImageView
				ImageView picView = (ImageView) findViewById(R.id.ivPhoto);
				// display the returned cropped image
				picView.setImageBitmap(thePic);
			}
		}

	}

	private void addEvents() {
		btnCrop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performCrop();
				Log.v(TEXT_SERVICES_MANAGER_SERVICE, "Perform good Crop");

			}
		});

		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveImage();
			}
		});
	}

	private void saveImage() {
		File filename;
		try {
			String path = Environment.getExternalStorageDirectory().toString();
			Date d = new Date();
			CharSequence s = DateFormat.format("MMddyyyy-hhmmss", d.getTime());

			new File(path + "/Images/ImageFixer").mkdirs();
			filename = new File(path + "/Images/ImageFixer/" + s.toString()
					+ ".jpg");

			FileOutputStream out = new FileOutputStream(filename);

			photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();

			MediaStore.Images.Media.insertImage(getContentResolver(),
					filename.getAbsolutePath(), filename.getName(),
					filename.getName());

			Toast.makeText(getApplicationContext(),
					"File is Saved in  " + filename, 1000).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void performCrop() {
		// take care of exceptions
		try {
			// call the standard crop action intent (the user device may not
			// support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri
			cropIntent.setDataAndType(picUri, "image/*");
			// set crop properties
			cropIntent.putExtra("crop", "true");
			// indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			// indicate output X and Y
			cropIntent.putExtra("outputX", 256);
			cropIntent.putExtra("outputY", 256);
			// retrieve data on return
			cropIntent.putExtra("return-data", true);
			// start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, PIC_CROP);
		}
		// respond to users whose devices do not support the crop action
		catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

}
