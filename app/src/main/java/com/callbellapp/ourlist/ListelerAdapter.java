package com.callbellapp.ourlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/*    Durum;
    * 1 = normal(info)
    * 2 = sil
    * 3 = Düzenle
 */

public class ListelerAdapter extends RecyclerView.Adapter<ListelerAdapter.CardViewTasarimNesneleriTutucu>{

    private Context mContext;
    private List<Liste> listeList;

    public ListelerAdapter(Context mContext, List<Liste> listeList) {
        this.mContext = mContext;
        this.listeList = listeList;
    }

    public class CardViewTasarimNesneleriTutucu extends RecyclerView.ViewHolder{
        public TextView textViewListeAdi,textViewTamamlanan,textViewPaylasilanSayisi;
        public SeekBar seekBarTamamlanan;
        public ImageView imageViewPaylasilanKisi,imageViewInfo;
        public CardView cardViewListe;

        public CardViewTasarimNesneleriTutucu(@NonNull View itemView) {
            super(itemView);
            this.textViewListeAdi = itemView.findViewById(R.id.textViewListeAdi);
            this.textViewTamamlanan = itemView.findViewById(R.id.textViewTamamlanan);
            this.seekBarTamamlanan = itemView.findViewById(R.id.seekBarTamamlanan);
            this.imageViewPaylasilanKisi = itemView.findViewById(R.id.imageViewPaylasilanKisi);
            this.imageViewInfo = itemView.findViewById(R.id.imageViewInfo);
            this.textViewPaylasilanSayisi = itemView.findViewById(R.id.textViewPaylasilanSayisi);
            this.cardViewListe = itemView.findViewById(R.id.cardViewListe);
        }
    }


    @NonNull
    @Override
    public CardViewTasarimNesneleriTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tasarim_listeler,parent,false);

        return new CardViewTasarimNesneleriTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewTasarimNesneleriTutucu holder, int position) {
        final Liste liste1 = listeList.get(position);

        final VeriTabaniYardimcisi vt = new VeriTabaniYardimcisi(mContext);
        ArrayList<Notlar> listeNotlari = new NotlarDao().tumNotlar(vt,liste1.getListe_id());
        final int count_seakPeak_total = listeNotlari.size();
        int count_seakPeak_tamamlanan =0;

        for (Notlar l : listeNotlari){
            if (Boolean.valueOf(l.getCheck_box())){
                count_seakPeak_tamamlanan ++;
            }
        }

        List<ListePaylasilanPersonel>listePaylasilanPersonels= new ListePaylasilanPersonelDao()
                .PerosnelGetirListesi(vt,liste1.getListe_id());


        if((listePaylasilanPersonels.size() == 0) && (liste1.getYoneticimi() == 0) ){
            // eğer paylaşılan personel yoksa ve yonetici de değilse listeye yonetici yapılıyor.
            new ListelerDao().listeYoneticimiGuncelle(vt,liste1.getListe_id(),1);
            liste1.setYoneticimi(1);
        }

        final Liste liste = new Liste(liste1.getListe_id(),liste1.getListe_global_key()
                ,liste1.getListe_adi(),liste1.getYoneticimi());

        holder.textViewListeAdi.setText(liste.getListe_adi());
        holder.textViewTamamlanan.setText(count_seakPeak_tamamlanan+" / "+count_seakPeak_total);


        holder.cardViewListe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NotlarActivity.class);
                intent.putExtra("nesneListe",liste);
                mContext.startActivity(intent);
            }
        });


        int count_paylasilan_kisi_sayisi = listePaylasilanPersonels.size();
        if (count_paylasilan_kisi_sayisi == 1){
            holder.textViewPaylasilanSayisi.setVisibility(View.INVISIBLE);
            holder.imageViewPaylasilanKisi.setVisibility(View.VISIBLE);
        }
        if (count_paylasilan_kisi_sayisi > 1){
            holder.textViewPaylasilanSayisi.setVisibility(View.VISIBLE);
            holder.imageViewPaylasilanKisi.setVisibility(View.VISIBLE);
            //resim değiştirilip çoğul olanı da konulabilir.
            holder.textViewPaylasilanSayisi.setText(String.valueOf(count_paylasilan_kisi_sayisi));
        }
        if (count_paylasilan_kisi_sayisi == 0){
            holder.textViewPaylasilanSayisi.setVisibility(View.INVISIBLE);
            holder.imageViewPaylasilanKisi.setVisibility(View.INVISIBLE);
        }


        holder.seekBarTamamlanan.setMax(count_seakPeak_total);
        holder.seekBarTamamlanan.setProgress(count_seakPeak_tamamlanan);

        if (holder.imageViewPaylasilanKisi.getVisibility() == View.VISIBLE){
            holder.imageViewPaylasilanKisi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext,ListeInfoActivity.class);
                    intent.putExtra("nesneListe",liste);
                    mContext.startActivity(intent);
                }
            });
        }

        holder.imageViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext,holder.imageViewInfo);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_liste,popupMenu.getMenu());

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.action_sil:
                                AlertDialog.Builder alertDialogOlusturucu = new AlertDialog.Builder(mContext);
                                alertDialogOlusturucu.setMessage(R.string.liste_silinsin_mi);

                                alertDialogOlusturucu.setPositiveButton(R.string.evet, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // önce firebase'e silme komutu gönder.
                                        FirebaseVeriGonder.FBGlisteSilme(vt,liste);

                                        // veri tabanından silme
                                        new ListelerDao().listeSil(vt,liste.getListe_id());

                                        // burada yenileme işlemi yapılacak.
                                        Intent intent = new Intent(mContext,ListelerActivity.class);
                                        intent.putExtra("durumListe",2);
                                        mContext.startActivity(intent);
                                    }
                                });
                                alertDialogOlusturucu.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(mContext,R.string.iptal_edildi,Toast.LENGTH_SHORT).show();
                                    }
                                });
                                alertDialogOlusturucu.create().show();

                                return true;
                            case R.id.action_duzenle:
                                //new ListelerDao().listeGuncelle(vt,liste.getListe_id(),liste.getListe_adi(),liste.getSeakPeak_total(),liste.getSeakPeak_tamamlanan(),liste.getPaylasilan_kisi_sayisi());
                                // burada isim güncellemesi için alert view açılacak. tamam ile yukarıda güncelleme liste güncelleme yapılacak.
                                // Liste güncelleme işlemi için metodlara bak olmazsa intent ile yeniden başlatırız.

                                alertDialogOlusturucu(vt,liste);
                                return true;
                            case R.id.action_info:
                                Intent intentInfo = new Intent(mContext,ListeInfoActivity.class);
                                intentInfo.putExtra("nesneListe",liste);
                                mContext.startActivity(intentInfo);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });



    }

    @Override
    public int getItemCount() {
        return listeList.size();
    }

    private void alertDialogOlusturucu(final VeriTabaniYardimcisi vt, final Liste liste){

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View alert_tasarim = inflater.inflate(R.layout.alertview_tasarim, null);

        final EditText alert_edittext = (EditText) alert_tasarim.findViewById(R.id.editTextGirisBaslik);

        AlertDialog.Builder alertDialogOlusturucu = new AlertDialog.Builder(mContext);
        alertDialogOlusturucu.setMessage(R.string.liste_adi);
        alertDialogOlusturucu.setView(alert_tasarim);
        alert_edittext.setHint(R.string.liste_adi_giriniz);
        alert_edittext.setText(liste.getListe_adi());

        alertDialogOlusturucu.setPositiveButton(R.string.kaydet, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new ListelerDao().listeGuncelle(vt,liste.getListe_id(),liste.getListe_global_key()
                        ,alert_edittext.getText().toString().trim());

                Liste liste2 = new Liste(liste.getListe_id(),liste.getListe_global_key()
                        ,alert_edittext.getText().toString().trim(),liste.getYoneticimi());
                FirebaseVeriGonder.FBGlistePaylasma(vt,liste2);

                Intent intent = new Intent(mContext,ListelerActivity.class);
                intent.putExtra("durumListe",3);
                mContext.startActivity(intent);
            }
        });
        alertDialogOlusturucu.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Resources res = mContext.getResources();
                Toast.makeText(mContext,res.getString(R.string.iptal_edildi),Toast.LENGTH_SHORT).show();
            }
        });
        alertDialogOlusturucu.create().show();
    }



}
