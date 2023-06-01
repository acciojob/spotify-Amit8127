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
        boolean isPlaylistPresent = false;
        Playlist playlist = new Playlist();
        for(Playlist curruntplaylist: playlists){
            if(curruntplaylist.getTitle().equals(playlistTitle)){
                playlist = curruntplaylist;
                isPlaylistPresent = true;
                break;
            }
        }
        if (!isPlaylistPresent){
            throw new Exception("Playlist does not exist");
        }
        User newUser = new User();
        boolean isUserPresent = false;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                newUser = user;
                isUserPresent = true;
                break;
            }
        }
        if (!isUserPresent){
            throw new Exception("User does not exist");
        }
        List<User> userslist = new ArrayList<>();
        if(playlistListenerMap.containsKey(playlist)){
            userslist=playlistListenerMap.get(playlist);
        }
        if(!userslist.contains(newUser))
            userslist.add(newUser);
        playlistListenerMap.put(playlist, userslist);
        if(creatorPlaylistMap.get(newUser) != playlist) {
            creatorPlaylistMap.put(newUser, playlist);
        }
        List<Playlist>userplaylists = new ArrayList<>();
        if(userPlaylistMap.containsKey(newUser)){
            userplaylists=userPlaylistMap.get(newUser);
        }
        if(!userplaylists.contains(playlist)) {
            userplaylists.add(playlist);
        }
        userPlaylistMap.put(newUser, userplaylists);
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User curUser = new User();
        boolean isUserPresent = false;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                curUser = user;
                isUserPresent = true;
                break;
            }
        }
        if (!isUserPresent){
            throw new Exception("User does not exist");
        }
        Song song = new Song();
        boolean isSongPresent = false;
        for(Song cursong : songs){
            if(cursong.getTitle().equals(songTitle)){
                song = cursong;
                isSongPresent = true;
                break;
            }
        }
        if (!isSongPresent){
            throw new Exception("Song does not exist");
        }
        List<User> users = new ArrayList<>();
        if(songLikeMap.containsKey(song)){
            users = songLikeMap.get(song);
        }
        if (!users.contains(curUser)){
            users.add(curUser);
            songLikeMap.put(song,users);
            song.setLikes(song.getLikes()+1);
            Album album = new Album();
            for(Album curAlbum : albumSongMap.keySet()){
                List<Song> temp = albumSongMap.get(curAlbum);
                if(temp.contains(song)){
                    album = curAlbum;
                    break;
                }
            }
            Artist artist = new Artist();
            for(Artist curArtist : artistAlbumMap.keySet()){
                List<Album> temp = artistAlbumMap.get(curArtist);
                if(temp.contains(album)){
                    artist=curArtist;
                    break;
                }
            }
            artist.setLikes(artist.getLikes()+1);
        }
        return song;
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
            if(maxLikes == song.getLikes())
                name = song.getTitle();
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
