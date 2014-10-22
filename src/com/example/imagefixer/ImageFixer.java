package com.example.imagefixer;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ImageFixer extends Activity {
	private static final int CAMERA_REQUEST = 1888;
	private static final int ACTION_PICK_REQUEST = 1889;
	private Bitmap photo;
	private ImageButton btnCamera, btnLoad;
	private Uri picUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		addFonts();
		btnCamera = (ImageButton) this.findViewById(R.id.btnCamera);
		btnLoad = (ImageButton) this.findViewById(R.id.btnLoad);

		addEvents();
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			if (requestCode == CAMERA_REQUEST) {

				photo = (Bitmap) intent.getExtras().get("data");
				picUri = getImageUri(getApplicationContext(), photo);

			} else if (requestCode == ACTION_PICK_REQUEST) {
				picUri = intent.getData();
				if (picUri != null) {
					try {
						photo = MediaStore.Images.Media.getBitmap(
								this.getContentResolver(), picUri);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Failed to select photo, try again!", Toast.LENGTH_LONG)
						.show();
			}
			nextStep();
		}

	}
	
	public Uri getImageUri(Context inContext, Bitmap inImage) {
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	    String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
	    return Uri.parse(path);
	}

	private void addFonts() {
		// adding my fonts
		TextView title1 = (TextView) findViewById(R.id.tvTitle1);
		TextView title2 = (TextView) findViewById(R.id.tvTitle2);
		Typeface typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/ANDROID ROBOT.ttf");
		title1.setTypeface(typeFace);
		title2.setTypeface(typeFace);
	}

	// to do: create intent pass values and call second action
	private void nextStep() {
		Intent intent = new Intent(getApplicationContext(), PhotoEdit.class);
		intent.putExtra("picture", photo);
		intent.putExtra("picUri", picUri.toString());
		startActivity(intent);
	}

	private void addEvents() {
		// load images from gallery
		btnLoad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, ACTION_PICK_REQUEST);
			}
		});

		// start camera
		btnCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);

			}
		});
	}
}
