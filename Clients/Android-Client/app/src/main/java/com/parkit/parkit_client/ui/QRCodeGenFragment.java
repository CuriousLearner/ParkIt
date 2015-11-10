package com.parkit.parkit_client.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.parkit.parkit_client.R;

public class QRCodeGenFragment extends Fragment {


    public static final String messageTag = "Message : ";


    ImageView qrCodeView;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(view == null) {
            view = inflater.inflate(R.layout.fragment_qrcode_gen, container, false);
            qrCodeView = (ImageView) view.findViewById(R.id.image_view_qrgen);
            encodeHash();
        }
        return view;
    }

    public static Bitmap bitMatrixToBitmap(BitMatrix bitMatrix) {
        int height = bitMatrix.getHeight(), width = bitMatrix.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565);
        for(int i = 0 ; i < width ; i++) {
            for(int j = 0 ; j < height ; j++) {
                bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }


    public void encodeHash() {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            int height = 350, width = 350;

            String message = QRCodeGenFragment
                    .this.getActivity().getSharedPreferences("parkit", 0)
                    .getString("hash", "");

            if (!message.equals("")) {
                // String message = messageEdit.getText().toString();

                //@TODO: Add a spinner view with small, medium and large options

                Log.d(messageTag, "Height : " + height);
                Log.d(messageTag, "Width : " + width);
                Log.d(messageTag, messageTag + message);

                BitMatrix qrCodeBitMatrix = qrCodeWriter.encode(
                        message,
                        BarcodeFormat.QR_CODE,
                        width,
                        height);
                Bitmap qrCodeBitmap = bitMatrixToBitmap(qrCodeBitMatrix);
                qrCodeView.setImageBitmap(qrCodeBitmap);
            } else {
                Toast.makeText(
                        QRCodeGenFragment.this.getActivity(),
                        "Encoding error !!!",
                        Toast.LENGTH_SHORT
                ).show();
                Log.d(messageTag, "Encoding error");
            }
        } catch (Exception e) {
            Log.d(messageTag,"An Exception was raised during writing of qr code"
                    +"\nDescription : " + e.toString());
        }

    }


}
