package com.diagoalley.android.choosepictest;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Button takePhoto;
    private ImageView picture;
    private Uri imageUri;
    private Uri imageUri1;
    private Button chooseFromAlbum;

    public static final int TAKE_PHOTO = 1;

    public static final int CROP_PHOTO = 2;

    public static final int SELECT_PHOTO =3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePhoto = (Button) findViewById(R.id.take_photo);
        picture  = (ImageView) findViewById(R.id.picture);
        chooseFromAlbum =  (Button) findViewById(R.id.choose_from_album);
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File outputImage = new File(Environment.getExternalStorageDirectory(),"tempImage.jpg");
                File outputImage1 = new File(Environment.getExternalStorageDirectory(),"tempImage1.jpg");
                try{
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    if(outputImage1.exists()){
                        outputImage1.delete();
                    }

                    outputImage.createNewFile();
                    outputImage1.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                imageUri1 = Uri.fromFile(outputImage1);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                intent.putExtra("crop", true);
//                intent.putExtra("scale", true);
//                intent.putExtra("return-data",false);
//                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //创建File对象，用于存储拍照后的图片
                File outputImage = new File(Environment.getExternalStorageDirectory(),"tempImage.jpg");
                File outputImage1 = new File(Environment.getExternalStorageDirectory(),"tempImage1.jpg");
                try{
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    if(outputImage1.exists()){
                        outputImage1.delete();
                    }

                    outputImage.createNewFile();
                    outputImage1.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                imageUri1 = Uri.fromFile(outputImage1);
                Intent intent  = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    if(data!=null)
                        imageUri = data.getData();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri,"image/*");
//                    intent.putExtra("crop",true);
//                    intent.putExtra("aspectX", 1);
//                    intent.putExtra("aspectY", 1);
//                    intent.putExtra("outputX", 400);
//                    intent.putExtra("outputY", 400);
                    intent.putExtra("scale", true);
//                    intent.putExtra("return-data",true);
//                    intent.putExtra("noFaceDetection",true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri1);
                    startActivityForResult(intent,CROP_PHOTO);
                }
                break;
            case CROP_PHOTO:
                if(resultCode == RESULT_OK){
                    try{
                        Bitmap bitmap = getBitmapFromBigImagByUri(imageUri1);
                        picture.setImageBitmap(bitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try{
                        Uri selectedImage = data.getData();
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//                        cursor.moveToFirst();
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        String picturePath = cursor.getString(columnIndex);
//                        cursor.close();
//                        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                        picture.setImageBitmap(getBitmapFromBigImagByUri(selectedImage));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    /*
     * 通过Uri得到压缩以后的图片
     */
    private Bitmap getBitmapFromBigImagByUri(Uri uri)
    {
        Bitmap result = null;
        InputStream is1 = null;
        InputStream is2 = null;
        try
        {
            // 如果图片太大，这个地方依旧会出现问题
            // Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            // 使用两个inputstream的原因
            // http://stackoverflow.com/questions/12841482/resizing-bitmap-from-inputstream
            is1 = getContentResolver().openInputStream(uri);
            is2 = getContentResolver().openInputStream(uri);
            BitmapFactory.Options opts1 = new BitmapFactory.Options();
            opts1.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is1, null, opts1);
            int bmpWidth = opts1.outWidth;
            int bmpHeight = opts1.outHeight;
            int scale = Math.max(bmpWidth / 300, bmpHeight / 300);
            BitmapFactory.Options opts2 = new BitmapFactory.Options();
            // 缩放的比例
            opts2.inSampleSize = scale;
            // 内存不足时可被回收
            opts2.inPurgeable = true;
            // 设置为false,表示不仅Bitmap的属性，也要加载bitmap
            opts2.inJustDecodeBounds = false;
            result = BitmapFactory.decodeStream(is2, null, opts2);
        }
        catch (Exception ex)
        {
        }
        finally
        {
            if (is1 != null)
            {
                try
                {
                    is1.close();
                }
                catch (IOException e1)
                {
                }
            }
            if (is2 != null)
            {
                try
                {
                    is2.close();
                }
                catch (IOException e2)
                {
                }
            }
        }
        return result;
    }
}
