package com.callbellapp.ourlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class RehberAdapter extends RecyclerView.Adapter<RehberAdapter.CardViewTasarimNesneleriTutucu> {
    private Context mContext;
    private List<Personel> personelList;
    private int paylasilanListeİd;
    private HashMap<String,Object> integersID ;


    public RehberAdapter(Context mContext, List<Personel> personelList, int paylasilanListeİd, HashMap<String, Object> integersID) {
        this.mContext = mContext;
        this.personelList = personelList;
        this.paylasilanListeİd = paylasilanListeİd;
        this.integersID = integersID;
    }

    public class CardViewTasarimNesneleriTutucu extends RecyclerView.ViewHolder{
        public CardView cardViewPersonel;
        public TextView textViewAdTel;
        public ImageView imageView,imageViewEkle;

        public CardViewTasarimNesneleriTutucu(@NonNull View itemView) {
            super(itemView);
            this.cardViewPersonel  = itemView.findViewById(R.id.cardViewPersonel);
            this.textViewAdTel  = itemView.findViewById(R.id.textViewAdTel);
            this.imageView  = itemView.findViewById(R.id.imageViewPerson);
            this.imageViewEkle  = itemView.findViewById(R.id.imageViewEkle);
        }
    }


    @NonNull
    @Override
    public CardViewTasarimNesneleriTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tasarim_rehber,parent,false);

        return new RehberAdapter.CardViewTasarimNesneleriTutucu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewTasarimNesneleriTutucu holder, int position) {
        final Personel personel = personelList.get(position);
        final VeriTabaniYardimcisi vt = new VeriTabaniYardimcisi(mContext);
        holder.textViewAdTel.setText(personel.getPersonel_adi()+" - "+personel.getPersonel_tel());

        holder.imageViewEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ListePaylasilanPersonelDao().listeEkle(vt, paylasilanListeİd, personel.getPersonel_uid());
                holder.imageViewEkle.setVisibility(View.INVISIBLE);
                RehberActivity.rehbereEklemeDurumu=true;
            }
        });


        if (!integersID.containsKey(String.valueOf(personel.getPersonel_uid())) ){
            holder.imageViewEkle.setVisibility(View.VISIBLE);
        }else {
            holder.imageViewEkle.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return personelList.size();
    }


}
