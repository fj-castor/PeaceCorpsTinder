package com.andtinder.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andtinder.R;
import com.andtinder.model.CardModel;
import com.blueoxfords.ImageRestClient;
import com.blueoxfords.ImageService;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class SimpleCardStackAdapter extends CardStackAdapter {

	public SimpleCardStackAdapter(Context mContext) {
		super(mContext);
	}

	@Override
	public View getCardView(int position, CardModel model, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.std_card_inner, parent, false);
			assert convertView != null;
		}

        Picasso.with(this.getContext()).load(model.url).into((ImageView) convertView.findViewById(R.id.image));

        //((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(model.getCardImageDrawable());
               // ((TextView) convertView.findViewById(R.id.title)).setText(model.getTitle());
        ((TextView) convertView.findViewById(R.id.url)).setText(model.url);
        ((TextView) convertView.findViewById(R.id.req_id)).setText(model.req_id);
        convertView.findViewById(R.id.photo_id).setTag(model.photo_id);
		((TextView) convertView.findViewById(R.id.description)).setText(model.getDescription());

		return convertView;
	}
}
