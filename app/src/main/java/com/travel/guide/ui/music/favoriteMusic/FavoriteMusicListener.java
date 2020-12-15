package com.travel.guide.ui.music.favoriteMusic;

import com.travel.guide.model.response.FavoriteMusicResponse;

import java.util.List;

public interface FavoriteMusicListener {

    void onGetFavoriteMusics(List<FavoriteMusicResponse.Favotite_musics> favoriteMusics);

    void onGetFavoriteFailed(String message);

}
