package ai.cdk.justus;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.util.MediaUtil;
import com.google.appinventor.components.runtime.*;

import java.io.File;
import java.util.Date;


@DesignerComponent(version = ImageProcessor.VERSION,
    description = "A component to take do complex image processing! " +
        "That cannot be done with normal AppInventor components or blocks " +
        "This is the guinea pig for CDK Experiments done secretly at MIT " +
        "The details regarding this component is classified top secret in a google docs " +
        "Pssst... Dont let the government know!!",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/extension.png")
@SimpleObject(external = true)
public class ImageProcessor extends AndroidNonvisibleComponent implements Component {

    public static final int VERSION = 1.1;
    public static final float DEFAULT_WEIGHT = 0.5f;
    private ComponentContainer container;
    private float weight = 0;
    private int color = 0x000000FF;


    public ImageProcessor(ComponentContainer container) {
        super(container.$form());
        this.container = container;
        Weight(DEFAULT_WEIGHT);
        Log.d("CDK", "ImageProcessor Created" );
    }

    /**
     * Returns the weight to be used in ImageProcessor.ImageCombine
     * weight 's range is between 0 and 1
     * @return {@code weight} returns weight
     */
    @SimpleProperty(
        category = PropertyCategory.BEHAVIOR)
    public float Weight() {
        return weight;
    }

    /**
     * Sets the value of weight within [0,1]
     *
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_FLOAT, defaultValue = ImageProcessor.DEFAULT_WEIGHT + "")
    @SimpleProperty(description = "Weight to be used in ImageProcessor.ImageCombine"
        + "The value of weight must be in [0,1]. Out of bound values will be corrected automatically! ")
    public void Weight(float newWeight) {
        newWeight = Math.max(0, newWeight);
        newWeight = Math.min(1, newWeight);
        this.weight = newWeight;
    }


    /**
     * Takes imageA and imageB and returns imageC
     *
     * Such that RGBA of (point in imageC) = weight*(point in imageA) + (1 - weight)*(point in imageB)
     */
    @SimpleFunction
    public void ImageCombine(String imageA, String imageB) {
        imageA = imageA == null ? "" : imageA;
        imageB = imageB == null ? "" : imageB;
        // our three Bitmaps!
        Bitmap bitmapA, bitmapB;
        try { // load the input bitmaps from Assets
            bitmapA = MediaUtil.getBitmapDrawable(container.$form(), imageA).getBitmap();
            bitmapB = MediaUtil.getBitmapDrawable(container.$form(), imageB).getBitmap();
        } catch (IOException ioe) {
            Log.e("Image", "Unable to load ");
            return ;
        }
        int maxW = Math.max(bitmapA.getWidth(), bitmapB.getWidth());
        int maxH = Math.max(bitmapA.getHeight(), bitmapB.getHeight());
        Bitmap bitmapC = Bitmap.createBitmap(maxW, maxH, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < maxW; x++) {
            for (int y = 0; y < maxH; y++) {
                int colA = bitmapA.getPixel(x, y) ;
                int colB = bitmapB.getPixel(x, y) ;
                int aC = (int)(Color.alpha(colA)*weight + Color.alpha(colB)*(1.0-weight));
                int rC = (int)(Color.red(colA)*weight + Color.red(colB)*(1.0-weight));
                int gC = (int)(Color.green(colA)*weight + Color.green(colB)*(1.0-weight));
                int bC = (int)(Color.blue(colA)*weight + Color.blue(colB)*(1.0-weight));
                int colC = Color.argb(aC, rC, gC, bC);
                bitmapC.setPixel(x, y, colC);
            }
        }
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        bitmapC.compress(CompressFormat.PNG, 0 , ostream);
        Date date = new Date();
        File image = new File(Environment.getExternalStorageDirectory()+"/Cimage.png");
        try {
            FileOutputStream fostream = new FileOutputStream(image);
            fostream.write(ostream.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AfterProcess(image.getAbsolutePath());




        return;
    }

    /**
     *  This is the grey function that averages the RGB value and assigns to each color component.
     */
    @SimpleFunction
    public String ImageGrey(String imageA) {
        imageA = imageA == null ? "" : imageA;
        Bitmap bitmapA;
        try { // load the input bitmaps from Assets
            bitmapA = MediaUtil.getBitmapDrawable(container.$form(), imageA).getBitmap();
        } catch (IOException ioe) {
            Log.e("Image", "Unable to load ");
            return "";
        }
        int maxW = bitmapA.getWidth();
        int maxH = bitmapA.getHeight();
        Bitmap bitmapC = Bitmap.createBitmap(maxW, maxH, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < maxW; x++) {
            for (int y = 0; y < maxH; y++) {
                int colA = bitmapA.getPixel(x, y) ;
                int aC = Color.alpha(colA);
                int avgC = (int)((Color.red(colA) + Color.green(colA) + Color.blue(colA))/3);
                int colC = Color.argb(aC, avgC, avgC, avgC);
                bitmapC.setPixel(x, y, colC);
            }
        }
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        bitmapC.compress(CompressFormat.PNG, 0 , ostream);
        Date date = new Date();
        File image = new File(Environment.getExternalStorageDirectory()+"/Cimage.png");
        try {
            FileOutputStream fostream = new FileOutputStream(image);
            fostream.write(ostream.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image.getAbsolutePath();
    }

    /**
     * This is called after every ImageProcess that is done by this processor! To demo events ;)
     */
    @SimpleEvent
    public void AfterProcess(String image){
        EventDispatcher.dispatchEvent(this, "AfterProcess", image);
    }

    /**
     * This is called after every ImageProcess that is done by this processor! To demo events ;)
     */
    @SimpleEvent
    public static int getDominantColor(String imageA) {
        imageA = imageA == null ? "" : imageA;
        Bitmap bitmap;
        try { // load the input bitmaps from Assets
            bitmap = MediaUtil.getBitmapDrawable(container.$form(), imageA).getBitmap();
        } catch (IOException ioe) {
            Log.e("Image", "Unable to load ");
            return "";
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];
        //Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int color;
        int r = 0;
        int g = 0;
        int b = 0;
        int a;
        int count = 0;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];
            a = Color.alpha(color);
            if (a > 0) {
                r += Color.red(color);
                g += Color.green(color);
                b += Color.blue(color);
                count++;
            }
        }
        r /= count;
        g /= count;
        b /= count;
        r = (r << 16) & 0x00FF0000;
        g = (g << 8) & 0x0000FF00;
        b = b & 0x000000FF;
        color = 0xFF000000 | r | g | b;
        return color;
    }

}
