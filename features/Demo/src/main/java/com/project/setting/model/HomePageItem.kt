package com.project.setting.model

import com.project.core.model.network.Album
import com.project.core.model.network.Song

open class HomePageItem {
    class SlideHome(val listImage: List<String>) : HomePageItem()
    class SongHome(val song: Song) : HomePageItem()
    class ArtistHome : HomePageItem()
    class AlbumHome(val album: Album) : HomePageItem()
    class TitleHome(val title: String) : HomePageItem()
}
