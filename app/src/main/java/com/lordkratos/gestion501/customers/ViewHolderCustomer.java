package com.lordkratos.gestion501.customers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lordkratos.gestion501.R;
public class ViewHolderCustomer extends RecyclerView.ViewHolder {
    private final View mView;
    private clickListener mClickListener;

    public interface clickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolderCustomer.clickListener clicklistener) {
        mClickListener = clicklistener;
    }

    public ViewHolderCustomer(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(view, getAdapterPosition());
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemLongClick(view, getAdapterPosition());
                    return true;
                }
                return false;
            }
        });
    }

    public void setCustomerData(Context context, String client_id, String client_uid,
                                String names, String lastName, String email,
                                String dni, String direction, String phone_number, String base64Image){

        TextView tvClientIdI, tvClientUidI, tvNamesI, tvLastNameI, tvEmailI, tvDniI, tvDirectionI, tvPhoneNumberI;
        ImageView ivClienteFotoI;

        tvClientIdI = mView.findViewById(R.id.tvClientIdI);
        tvClientUidI = mView.findViewById(R.id.tvClientUidI);
        tvNamesI = mView.findViewById(R.id.tvNameI);
        tvLastNameI = mView.findViewById(R.id.tvLastNameI);
        tvEmailI = mView.findViewById(R.id.tvEmailI);
        tvDniI = mView.findViewById(R.id.tvDniI);
        tvDirectionI = mView.findViewById(R.id.tvDirectionI);
        tvPhoneNumberI = mView.findViewById(R.id.tvPhoneNumberI);
        ivClienteFotoI = mView.findViewById(R.id.ivClienteFotoI);

        tvClientIdI.setText(client_id);
        tvClientUidI.setText(client_uid);
        tvNamesI.setText(names);
        tvLastNameI.setText(lastName);
        tvEmailI.setText(email);
        tvDniI.setText(dni);
        tvDirectionI.setText(direction);
        tvPhoneNumberI.setText(phone_number);

        if (base64Image == null || base64Image.trim().isEmpty()) {
            ivClienteFotoI.setImageResource(R.drawable.info);
            return;
        }

        try {
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            if (bitmap != null) {
                ivClienteFotoI.setImageBitmap(bitmap);
            } else {
                ivClienteFotoI.setImageResource(R.drawable.info);
            }
        } catch (IllegalArgumentException e) {
            ivClienteFotoI.setImageResource(R.drawable.info);
        }
    }
}
