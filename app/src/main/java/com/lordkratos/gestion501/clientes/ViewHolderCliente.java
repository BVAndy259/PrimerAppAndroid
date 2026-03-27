package com.lordkratos.gestion501.clientes;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lordkratos.gestion501.R;
public class ViewHolderCliente extends RecyclerView.ViewHolder {
    private View mView;
    private clickListener mClickListener;

    public interface clickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolderCliente.clickListener clicklistener) {
        mClickListener = clicklistener;
    }

    public ViewHolderCliente(@NonNull View itemView) {
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

    public void setearDatosCliente(Context context, String client_id, String client_uid,
                                   String names, String lastName, String email,
                                   String dni, String direction, String phone_number){

        TextView tvClientIdI, tvClientUidI, tvNamesI, tvLastNameI, tvEmailI, tvDniI, tvDirectionI, tvPhoneNumberI;

        tvClientIdI = mView.findViewById(R.id.tvClientIdI);
        tvClientUidI = mView.findViewById(R.id.tvClientUidI);
        tvNamesI = mView.findViewById(R.id.tvNameI);
        tvLastNameI = mView.findViewById(R.id.tvLastNameI);
        tvEmailI = mView.findViewById(R.id.tvEmailI);
        tvDniI = mView.findViewById(R.id.tvDniI);
        tvDirectionI = mView.findViewById(R.id.tvDirectionI);
        tvPhoneNumberI = mView.findViewById(R.id.tvPhoneNumberI);

        tvClientIdI.setText(client_id);
        tvClientUidI.setText(client_uid);
        tvNamesI.setText(names);
        tvLastNameI.setText(lastName);
        tvEmailI.setText(email);
        tvDniI.setText(dni);
        tvDirectionI.setText(direction);
        tvPhoneNumberI.setText(phone_number);
    }
}
