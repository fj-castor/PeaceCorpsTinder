/**
 * AndTinder v0.1 for Android
 *
 * @Author: Enrique López Mañas <eenriquelopez@gmail.com>
 * http://www.lopez-manas.com
 *
 * TAndTinder is a native library for Android that provide a
 * Tinder card like effect. A card can be constructed using an
 * image and displayed with animation effects, dismiss-to-like
 * and dismiss-to-unlike, and use different sorting mechanisms.
 *
 * AndTinder is compatible with API Level 13 and upwards
 *
 * @copyright: Enrique López Mañas
 * @license: Apache License 2.0
 */

package com.andtinder.model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.blueoxfords.models.VolunteerOpening;

import java.util.HashMap;

public class CardModel {

	private String   title;
	private String   description;
    public  String   url;
    public int id;
    public String req_id;
    public VolunteerOpening opening;
	private Drawable cardImageDrawable;
	private Drawable cardLikeImageDrawable;
	private Drawable cardDislikeImageDrawable;

    private OnCardDimissedListener mOnCardDimissedListener = null;

    private OnClickListener mOnClickListener = null;

    public interface OnCardDimissedListener {
        void onLike(View topCard);
        void onDislike(View topCard);
    }

    public interface OnClickListener {
        void OnClickListener();
    }

	public CardModel() {
		this(null, null, (Drawable)null);
	}

	public CardModel(String title, String description, Drawable cardImage) {
		this.title = title;
		this.description = description;
		this.cardImageDrawable = cardImage;
	}

	public CardModel(String title, String description, Bitmap cardImage) {
		this.title = title;
		this.description = description;
		this.cardImageDrawable = new BitmapDrawable(null, cardImage);
	}

    public CardModel(String title, String description, String url, int id, VolunteerOpening opening) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.opening = opening;
        this.req_id = opening.req_id;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public static void swipeRight(HashMap<VolunteerOpening, Integer> scoreMap, CardModel card) {
        int curScore = scoreMap.get(card.opening) + 1;
        scoreMap.put(card.opening, curScore);
        Log.d("CARD SWIPED", card.opening.title + " " + card.opening.country);
        for (VolunteerOpening name: scoreMap.keySet()){

            String key = name.title.toString();
            String value = scoreMap.get(name).toString();
            Log.d("SCOREMAP",key + " " + value);


        }
        if (curScore == 3) {
            Log.d("OHSHIT", "YOU GOT MATCHED UP WITH "+card.opening.title);
        }
    }

	public Drawable getCardImageDrawable() {
		return cardImageDrawable;
	}

	public void setCardImageDrawable(Drawable cardImageDrawable) {
		this.cardImageDrawable = cardImageDrawable;
	}

	public Drawable getCardLikeImageDrawable() {
		return cardLikeImageDrawable;
	}

	public void setCardLikeImageDrawable(Drawable cardLikeImageDrawable) {
		this.cardLikeImageDrawable = cardLikeImageDrawable;
	}

	public Drawable getCardDislikeImageDrawable() {
		return cardDislikeImageDrawable;
	}

	public void setCardDislikeImageDrawable(Drawable cardDislikeImageDrawable) {
		this.cardDislikeImageDrawable = cardDislikeImageDrawable;
	}

    public void setOnCardDimissedListener( OnCardDimissedListener listener ) {
        this.mOnCardDimissedListener = listener;
    }

    public OnCardDimissedListener getOnCardDimissedListener() {
       return this.mOnCardDimissedListener;
    }


    public void setOnClickListener( OnClickListener listener ) {
        this.mOnClickListener = listener;
    }

    public OnClickListener getOnClickListener() {
        return this.mOnClickListener;
    }
}