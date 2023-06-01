package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album = new Album(title);
        albums.add(album);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Song song = new Song(title, length);
        song.setLikes(0);
        songs.add(song);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User newUser = null;
        boolean isUserPresent = false;
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                newUser = user;
                isUserPresent = true;
                break;
            }
        }
        if(!isUserPresent) {
            throw new Exception("User does not exist");
        }

        Playlist newPlaylist = null;
        boolean isPlayListPresent = false;
        for(Playlist playlist:playlists){
            if(playlist.getTitle().equals(playlistTitle)){
                newPlaylist = playlist;
                isPlayListPresent = true;
                break;
            }
        }
        if(!isPlayListPresent) {
            throw new Exception("Playlist does not exist");
        }

        if(creatorPlaylistMap.containsKey(newUser)) {
            return newPlaylist;
        }

        List<User> listener = playlistListenerMap.get(newPlaylist);
        for(User user : listener){
            if(user == newUser) {
                return newPlaylist;
            }
        }

        listener.add(newUser);
        playlistListenerMap.put(newPlaylist, listener);

        List<Playlist> newPlaylists = userPlaylistMap.get(newUser);
        if(newPlaylists == null){
            newPlaylists = new ArrayList<>();
        }
        newPlaylists.add(newPlaylist);
        userPlaylistMap.put(newUser, newPlaylists);

        return newPlaylist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User newUser = null;
        boolean isUserPresent = false;
        for(User user:users){
            if(user.getMobile().equals(mobile)){
                newUser = user;
                isUserPresent = true;
                break;
            }
        }
        if(!isUserPresent) {
            throw new Exception("User does not exist");
        }

        Song newSong = null;
        boolean isSongPresent = false;
        for(Song song : songs){
            if(song.getTitle().equals(songTitle)){
                newSong = song;
                break;
            }
        }
        if (!isSongPresent) {
            throw new Exception("Song does not exist");
        }

        if(songLikeMap.containsKey(newSong)){
            List<User> list = songLikeMap.get(newSong);
            if(list.contains(newUser)){
                return newSong;
            }else {
                int songLikes = newSong.getLikes();
                newSong.setLikes(songLikes + 1);
                list.add(newUser);
                songLikeMap.put(newSong,list);

                Album newAlbum = null;
                for(Album album : albumSongMap.keySet()){
                    List<Song> songList = albumSongMap.get(album);
                    if(songList.contains(newSong)){
                        newAlbum = album;
                        break;
                    }
                }
                Artist newArtist = null;
                for(Artist artist : artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(artist);
                    if (albumList.contains(newAlbum)){
                        newArtist = artist;
                        break;
                    }
                }
                int artistLikes = newArtist.getLikes();
                newArtist.setLikes(artistLikes + 1);
                artists.add(newArtist);
                return newSong;
            }
        }else {
            int songLikes = newSong.getLikes();
            newSong.setLikes(songLikes + 1);
            List<User> list = new ArrayList<>();
            list.add(newUser);
            songLikeMap.put(newSong,list);

            Album newAlbum = null;
            for(Album album1:albumSongMap.keySet()){
                List<Song> songList = albumSongMap.get(album1);
                if(songList.contains(newSong)){
                    newAlbum = album1;
                    break;
                }
            }
            Artist newArtist = null;
            for(Artist artist : artistAlbumMap.keySet()){
                List<Album> albumList = artistAlbumMap.get(artist);
                if (albumList.contains(newAlbum)){
                    newArtist = artist;
                    break;
                }
            }
            int likes1 = newArtist.getLikes() +1;
            newArtist.setLikes(likes1);
            artists.add(newArtist);

            return newSong;
        }
    }

    public String mostPopularArtist() {
        String name = "";
        int maxLikes = Integer.MIN_VALUE;
        for(Artist art : artists){
            maxLikes = Math.max(maxLikes,art.getLikes());
        }
        for(Artist art : artists){
            if(maxLikes == art.getLikes()){
                name = art.getName();
                break;
            }
        }
        return name;
    }

    public String mostPopularSong() {
        String name = "";
        int maxLikes = Integer.MIN_VALUE;
        for(Song song : songs){
            maxLikes = Math.max(maxLikes,song.getLikes());
        }
        for(Song song : songs){
            if(maxLikes == song.getLikes()) {
                name = song.getTitle();
                break;
            }
        }
        return name;
    }

    public List<User> getAllUser() {
        return users;
    }

    public List<Artist> getAllArtists() {
        return artists;
    }

    public List<Album> getAllAlbums() {
        return albums;
    }

    public List<Artist> artistAlbumMapKeys() {
        return new ArrayList<>(artistAlbumMap.keySet());
    }

    public boolean isArtistPresentAsKey(Artist newArtist) {
        return artistAlbumMap.containsKey(newArtist);
    }

    public List<Album> getListOfAlbumsOfArtist(Artist newArtist) {
        return artistAlbumMap.get(newArtist);
    }

    public void addArtistAndHisAlbumsList(Artist newArtist, List<Album> newList) {
        artistAlbumMap.put(newArtist, newList);
    }

    public boolean isAlbumPresentAsKey(Album newAlbum) {
        return albumSongMap.containsKey(newAlbum);
    }

    public List<Song> getListOfSongsOfAlbum(Album newAlbum) {
        return albumSongMap.get(newAlbum);
    }

    public void addAlbumAndHisSongsList(Album newAlbum, List<Song> newSongsList) {
        albumSongMap.put(newAlbum, newSongsList);
    }

    public List<Playlist> getAllPlayLists() {
        return playlists;
    }

    public List<Song> getAllSongs() {
        return songs;
    }

    public void addPlayListAndHisSongsList(Playlist newPlayList, List<Song> newSongsList) {
        playlistSongMap.put(newPlayList, newSongsList);
    }

    public boolean isUserListPresent(Playlist newPlayList) {
        return playlistListenerMap.containsKey(newPlayList);
    }

    public List<User> getListOfUserOfPlaylist(Playlist newPlayList) {
        return playlistListenerMap.get(newPlayList);
    }

    public void addPlaylistAndHisUser(Playlist newPlayList, List<User> usersList) {
        playlistListenerMap.put(newPlayList,usersList);
    }

    public void addUserAndHisPlaylist(User newUser, Playlist newPlayList) {
        creatorPlaylistMap.put(newUser, newPlayList);
    }

    public boolean isUserPlaylistPresent(User newUser) {
        return userPlaylistMap.containsKey(newUser);
    }

    public List<Playlist> getListOfPlaylistsOfUser(User newUser) {
        return userPlaylistMap.get(newUser);
    }

    public void addUserAndHisPlaylists(User newUser, List<Playlist> newPlayList) {
        userPlaylistMap.put(newUser, newPlayList);
    }
}
