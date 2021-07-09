package com.callbellapp.ourlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListeInfoRehberAdapter extends RecyclerView.Adapter<ListeInfoRehberAdapter.CardViewTasarimNesneleriTutucu> {
    private Context mContext;
    private List<ListePaylasilanPersonel> personelList;
    private Liste liste;

    public ListeInfoRehberAdapter(Context mContext, List<ListePaylasilanPersonel> personelList, Liste liste) {
        this.mContext = mContext;
        this.personelList = personelList;
        this.liste = liste;
    }

    public class CardViewTasarimNesneleriTutucu extends RecyclerView.ViewHolder{
        public CardView cardViewPersonel;
        public TextView textViewAdTel,textViewYonetici;
        public ImageView imageViewPerson,imageViewEkle;

        public CardViewTasarimNesneleriTutucu(@NonNull View itemView) {
            super(itemView);
            this.cardViewPersonel  = itemView.findViewById(R.id.cardViewPersonel);
            this.textViewAdTel  = itemView.findViewById(R.id.textViewAdTel);
            this.imageViewPerson  = itemView.findViewById(R.id.imageViewPerson);
            this.imageViewEkle  = itemView.findViewById(R.id.imageViewEkle);
            this.textViewYonetici = itemView.findViewById(R.id.textViewYonetici);
        }
    }


    @NonNull
    @Override
    public CardViewTasarimNesneleriTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tasarim_paylasilan_rehber,parent,false);

        return new ListeInfoRehberAdapter.CardViewTasarimNesneleriTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewTasarimNesneleriTutucu holder, int position) {
        final ListePaylasilanPersonel personel = personelList.get(position);
        final VeriTabaniYardimcisi vt = new VeriTabaniYardimcisi(mContext);
        holder.textViewAdTel.setText(personel.getPerseonel_uid().getPersonel_adi()+" - "
                +personel.getPerseonel_uid().getPersonel_tel());

        if (personel.getPerseonel_uid().getPersonel_id() == 0) {
            holder.imageViewEkle.setVisibility(View.INVISIBLE);
            holder.textViewAdTel.setText(R.string.siz);
        }

        holder.imageViewEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext,holder.imageViewEkle);
                if (personel.getYoneticimi() == 0){
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_liste_info_kisiler_yonetici_ac,popupMenu.getMenu());
                }else {
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_liste_info_yonetici_kapat,popupMenu.getMenu());
                }
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.action_sil:
                                //Toast.makeText(mContext, "Sil'e tıklandı", Toast.LENGTH_SHORT).show();
                                ListeInfoActivity.listedenAtılanPersonel.add(personel.getPerseonel_uid());

                                new ListePaylasilanPersonelDao().listedenSil(vt,personel.getPaylasilan_personel_id());
                                // burada personel uid yerine personel iletişim id geliyor.
                                holder.imageViewEkle.setVisibility(View.INVISIBLE);
                                //firebase tekrardan veri gonderilecek. her kullanıcıda veriler siliniyor zaten.
                                return true;
                            case R.id.action_yonetici_ac:
                                //Toast.makeText(mContext, "Yonetici tıklandı", Toast.LENGTH_SHORT).show();
                                PopupMenu popupMenu = new PopupMenu(mContext,holder.imageViewEkle);
                                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_liste_info_yonetici_kapat,popupMenu.getMenu());
                                popupMenu.show();
                                holder.imageViewPerson.setVisibility(View.INVISIBLE);
                                holder.textViewYonetici.setVisibility(View.VISIBLE);

                                ListeInfoActivity.yoneticiYapilanPersonel.add(personel.getPerseonel_uid().getPersonel_uid());
                                new ListePaylasilanPersonelDao().listeYoneticimiGuncelle(vt,personel.getPaylasilan_personel_id(),1);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });


        // yonetici yazdırma
        // yonetici yazdığı zaman personel resmi kaldırılacak. cihaz oturumunu da listeden çekilecek.
        if (personel.getPerseonel_uid().getPersonel_id() == 0) {
            // burası listeden çekilecek.
            if (liste.getYoneticimi()==1){
                holder.imageViewPerson.setVisibility(View.INVISIBLE);
                holder.textViewYonetici.setVisibility(View.VISIBLE);
            }else {
                holder.imageViewPerson.setVisibility(View.VISIBLE);
                holder.textViewYonetici.setVisibility(View.INVISIBLE);
            }
        }else {
            // paylaşılan personelden alınacak.
            if (personel.getYoneticimi()==1){
                holder.imageViewPerson.setVisibility(View.INVISIBLE);
                holder.textViewYonetici.setVisibility(View.VISIBLE);
            }else {
                holder.imageViewPerson.setVisibility(View.VISIBLE);
                holder.textViewYonetici.setVisibility(View.INVISIBLE);
            }
        }


    }

    @Override
    public int getItemCount() {
        return personelList.size();
    }


}
