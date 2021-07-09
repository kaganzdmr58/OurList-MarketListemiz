package com.callbellapp.ourlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*    Durum;
 * 1 = normal(info)
 * 2 = sil
 * 3 = Düzenle
 */

public class NotlarAdapter extends RecyclerView.Adapter<NotlarAdapter.CardViewTasarimNesneleriTutucu> {

    private Context mContext;
    private List<Notlar> notlarList;
    private VeriTabaniYardimcisi vt;
    private Integer drum;
    private Liste nesneListe;
    private Boolean checkDurum;

    public NotlarAdapter(Context mContext, List<Notlar> notlarList, VeriTabaniYardimcisi vt, Integer drum, Liste nesneListe) {
        this.mContext = mContext;
        this.notlarList = notlarList;
        this.vt = vt;
        this.drum = drum;
        this.nesneListe = nesneListe;
    }

    public class CardViewTasarimNesneleriTutucu extends RecyclerView.ViewHolder {
        public ImageView imageViewInfo;
        public CardView cardViewNot;
        public CheckBox checkBoxNot;
        public TextView textViewNotMetin, textViewNotunDetayi;

        public CardViewTasarimNesneleriTutucu(@NonNull View itemView) {
            super(itemView);
            this.imageViewInfo = itemView.findViewById(R.id.imageViewInfo);
            this.cardViewNot = itemView.findViewById(R.id.cardViewNot);
            this.checkBoxNot = itemView.findViewById(R.id.checkBoxNot);
            this.textViewNotMetin = itemView.findViewById(R.id.textViewNotMetin);
            this.textViewNotunDetayi= itemView.findViewById(R.id.textViewNotunDetayi);
        }
    }


    @NonNull
    @Override
    public CardViewTasarimNesneleriTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tasarim_not_itemlar_2,parent,false);

        return new CardViewTasarimNesneleriTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewTasarimNesneleriTutucu holder, int position) {
        final Notlar not = notlarList.get(position);

        holder.checkBoxNot.setChecked(Boolean.valueOf(not.getCheck_box()));
        holder.textViewNotMetin.setText(not.getNot_adi());

        holder.checkBoxNot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String tamamlayan =null;

                if (b){
                    holder.textViewNotMetin.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.textViewNotMetin.setTypeface(null, Typeface.NORMAL);
                    Resources res = mContext.getResources();
                    tamamlayan = MainActivity.mAuth.getCurrentUser().getDisplayName()+res.getString(R.string.tarafindan)
                            + MainActivity.gonderTarihSaat()+res.getString(R.string.de_tamamlandi);
                }else {
                    holder.textViewNotMetin.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                    holder.textViewNotMetin.setTypeface(null, Typeface.BOLD_ITALIC);
                    tamamlayan = "";
                }
                new NotlarDao().notGuncelle(vt,not.getNot_id(),String.valueOf(b),tamamlayan
                        ,not.getNot_adi(),not.getNot_detay(),not.getNot_global_key());


                Notlar nott = new Notlar(not.getNot_id(),String.valueOf(b),not.getNot_tamamlayan(),not.getNot_adi()
                        ,nesneListe.getListe_id(),not.getNot_global_key(),not.getNot_detay());
                FirebaseVeriGonder.FBGnotIslem(vt,nesneListe,nott);

                Intent intent = new Intent(mContext,NotlarActivity.class);
                intent.putExtra("nesneListe",nesneListe);
                mContext.startActivity(intent);

            }
        });

        holder.textViewNotMetin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "not"+not.getNot_adi()+"tıklandı", Toast.LENGTH_SHORT).show();
                alertDialogOlusturucu(not);

            }
        });


        holder.textViewNotunDetayi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "not"+not.getNot_adi()+"tıklandı", Toast.LENGTH_SHORT).show();
                alertDialogOlusturucu(not);

            }
        });

        holder.cardViewNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "not"+not.getNot_adi()+"tıklandı", Toast.LENGTH_SHORT).show();
                alertDialogOlusturucu(not);

               /* holder.textViewNotMetin.setFocusableInTouchMode(true);
                holder.imageViewInfo.setImageResource(R.drawable.ic_baseline_check_24);
                checkDurum = true;*/

            }
        });
        if (Boolean.valueOf(not.getCheck_box())){
            holder.textViewNotMetin.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewNotMetin.setTypeface(null, Typeface.NORMAL);
        }

        //if (checkDurum=false) {// edittext için eklendi
        switch (drum) {
            case 1:
                holder.imageViewInfo.setImageResource(R.drawable.ic_info_outline_black_24dp);
                holder.imageViewInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // not bilgilere geçiş.
                        Intent intent = new Intent(mContext, NotInfoActivity.class);
                        intent.putExtra("nesneNot", not);
                        intent.putExtra("nesneListe", nesneListe);
                        mContext.startActivity(intent);
                    }
                });
                break;
            case 2:
                holder.imageViewInfo.setImageResource(R.drawable.ic_delete_black_24dp);
                holder.imageViewInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // silme işlemi.
                        new NotlarDao().notSil(vt, not.getNot_id());
                        FirebaseVeriGonder.FBGnotSilme(vt, nesneListe, not);
                        MainActivity.nesneListe = nesneListe;

                        Intent intent = new Intent(mContext, NotlarActivity.class);
                        intent.putExtra("nesneListe", nesneListe);
                        intent.putExtra("durumNot", 2);
                        mContext.startActivity(intent);

                        // geri dönüş için intent oluşturduğumuzda hata veriyor muhtemelen liste ID olmadığı için
                    }
                });
                break;
            default:
                break;
        }

       /* }else {
            holder.imageViewInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkDurum = false;

                    new NotlarDao().notGuncelle(vt,not.getNot_id(),not.getCheck_box(),not.getNot_tamamlayan()
                            ,holder.textViewNotMetin.getText().toString().trim(),not.getNot_detay(),not.getNot_global_key());

                    Notlar nott = new Notlar(not.getNot_id(),not.getCheck_box(),not.getNot_tamamlayan()
                            ,holder.textViewNotMetin.getText().toString().trim()
                            ,nesneListe.getListe_id(),not.getNot_global_key(),not.getNot_detay());
                    FirebaseVeriGonder.FBGnotIslem(vt,nesneListe,nott);


                    Intent intent = new Intent(mContext, NotlarActivity.class);
                    intent.putExtra("nesneListe",nesneListe);
                    mContext.startActivity(intent);

                    holder.textViewNotMetin.setFocusableInTouchMode(false);
                }
            });
        }
*/

        if (not.getNot_detay().isEmpty()){
            holder.textViewNotunDetayi.setVisibility(View.INVISIBLE);
            holder.textViewNotunDetayi.setHeight(0);
        }else {
            holder.textViewNotunDetayi.setText(not.getNot_detay());
        }




    }

    @Override
    public int getItemCount() {
        return notlarList.size();
    }

    private void alertDialogOlusturucu(final Notlar not){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View alert_tasarim = inflater.inflate(R.layout.alertview_tasarim_ikili_notlar_activity, null);

        final EditText alert_edittext = (EditText) alert_tasarim.findViewById(R.id.editTextGirisBaslik);
        final EditText alert_edittext_detay = (EditText) alert_tasarim.findViewById(R.id.editTextGirisDetay);


        AlertDialog.Builder alertDialogOlusturucu = new AlertDialog.Builder(mContext);
        alertDialogOlusturucu.setView(alert_tasarim);
        alert_edittext.setHint(R.string.notunuzu_giriniz);
        alert_edittext.setText(not.getNot_adi());
        alert_edittext_detay.setHint(R.string.not_detayini_giriniz);
        alert_edittext_detay.setText(not.getNot_detay());

        alertDialogOlusturucu.setPositiveButton(R.string.kaydet, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new NotlarDao().notGuncelle(vt,not.getNot_id(),not.getCheck_box(),not.getNot_tamamlayan()
                        ,alert_edittext.getText().toString().trim(),alert_edittext_detay.getText().toString().trim(),not.getNot_global_key());

                Notlar nott = new Notlar(not.getNot_id(),not.getCheck_box(),not.getNot_tamamlayan(),alert_edittext.getText().toString().trim()
                        ,nesneListe.getListe_id(),not.getNot_global_key(),alert_edittext_detay.getText().toString().trim());
                FirebaseVeriGonder.FBGnotIslem(vt,nesneListe,nott);


                Intent intent = new Intent(mContext, NotlarActivity.class);
                intent.putExtra("nesneListe",nesneListe);
                mContext.startActivity(intent);
            }
        });
        alertDialogOlusturucu.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Resources res = mContext.getResources();
                Toast.makeText(mContext,res.getString(R.string.not_iptal_edildi),Toast.LENGTH_SHORT).show();
            }
        });
        alertDialogOlusturucu.create().show();
    }


}
