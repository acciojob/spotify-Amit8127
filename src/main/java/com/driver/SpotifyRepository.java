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
        User newUser = new User(name,mobile);
        users.add(newUser);
        return newUser;
    }

    public Artist createArtist(String name) {
        Artist newArtist = new Artist(name);
        newArtist.setLikes(0);
        artists.add(newArtist);
        return newArtist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist newArtist = null;
        boolean isArtistPresent = false;
        for(Artist artist : artists) {
            if (Objects.equals(artist.getName(), artistName)) {
                newArtist = artist;
                isArtistPresent = true;
                break;
            }
        }
        List<Album> list = new ArrayList<>();
        if(!isArtistPresent){
            newArtist = createArtist(artistName);
        }
        Album newAlbum = new Album(title);
        newAlbum.setReleaseDate(new Date());

        albums.add(newAlbum);
        list.add(newAlbum);
        artistAlbumMap.put(newArtist, list);
        return newAlbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album newAlbum = null;
        boolean isAlbumPresent = false;
        for(Album album : albums){
            if(album.getTitle().equals(albumName)){
                newAlbum = album;
                isAlbumPresent = true;
                break;
            }
        }
        if(!isAlbumPresent) {
            throw new Exception("Album does not exist");
        } else {
            Song song = new Song(title, length);
            song.setLikes(0);
            songs.add(song);

            if(albumSongMap.containsKey(newAlbum)){
                List<Song> songList = albumSongMap.get(newAlbum);
                songList.add(song);
                albumSongMap.put(newAlbum,songList);
            }else{
                List<Song> songList = new ArrayList<>();
                songList.add(song);
                albumSongMap.put(newAlbum, songList);
            }
            return song;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User newUser = null;
        boolean isUserPresent = false;
        for(User user :users){
            if(user.getMobile().equals(mobile)){
                newUser = user;
                isUserPresent = true;
                break;
            }
        }
        if(!isUserPresent){
            throw new Exception("User does not exist");
        } else{
            Playlist playlist = new Playlist(title);
            playlists.add(playlist);

            List<Song> list = new ArrayList<>();
            for(Song song :songs){
                if(song.getLength()==length){
                    list.add(song);
                }
            }
            playlistSongMap.put(playlist,list);
            List<User> userList = new ArrayList<>();
            userList.add(newUser);
            playlistListenerMap.put(playlist,userList);
            creatorPlaylistMap.put(newUser,playlist);
            userPlaylistMap.get(newUser);

            if(userPlaylistMap.containsKey(newUser)){
                List<Playlist> userPlayList = userPlaylistMap.get(newUser);
                userPlayList.add(playlist);
                userPlaylistMap.put(newUser,userPlayList);
            }else{
                List<Playlist> plays = new ArrayList<>();
                plays.add(playlist);
                userPlaylistMap.put(newUser,plays);
            }

            return playlist;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = null;
        boolean isUserPresent = false;
        for(User user1 : users){
            if(user1.getMobile().equals(mobile)){
                user = user1;
                isUserPresent = true;
                break;
            }
        }

        if(!isUserPresent){
            throw new Exception("User does not exist");
        }
        else{
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> list = new ArrayList<>();
            for(Song song : songs){
                if(songTitles.contains(song.getTitle())){
                    list.add(song);
                }
            }
            playlistSongMap.put(playlist ,list);

            List<User> list1 = new ArrayList<>();
            list1.add(user);
            playlistListenerMap.put(playlist,list1);

            creatorPlaylistMap.put(user,playlist);


            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user,userPlayList);
            }else{
                List<Playlist> plays = new ArrayList<>();
                plays.add(playlist);
                userPlaylistMap.put(user,plays);
            }

            return playlist;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;
        boolean isUserPresent = false;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)){
                user=user1;
                isUserPresent = true;
                break;
            }
        }
        if(!isUserPresent) {
            throw new Exception("User does not exist");
        }

        Playlist playlist = null;
        boolean isPlaylistPresent = false;
        for(Playlist playlist1:playlists){
            if(playlist1.getTitle().equals(playlistTitle)){
                playlist = playlist1;
                isPlaylistPresent = true;
                break;
            }
        }
        if(!isPlaylistPresent) {
            throw new Exception("Playlist does not exist");
        }

        if(creatorPlaylistMap.containsKey(user))
            return playlist;

        List<User> listener = playlistListenerMap.get(playlist);
        for(User user1:listener){
            if(user1==user)
                return playlist;
        }

        listener.add(user);
        playlistListenerMap.put(playlist,listener);

        List<Playlist> playlists1 = userPlaylistMap.get(user);
        if(playlists1 == null){
            playlists1 = new ArrayList<>();
        }
        playlists1.add(playlist);
        userPlaylistMap.put(user,playlists1);

        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        boolean isUserPresent = false;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)){
                user=user1;
                break;
            }
        }
        if(!isUserPresent) {
            throw new Exception("User does not exist");
        }

        Song song = null;
        boolean isSongPresent = false;
        for(Song song1:songs){
            if(song1.getTitle().equals(songTitle)){
                song = song1;
                isSongPresent = true;
                break;
            }
        }
        if (!isSongPresent) {
            throw new Exception("Song does not exist");
        }

        if(songLikeMap.containsKey(song)){
            List<User> list = songLikeMap.get(song);
            if(list.contains(user)){
                return song;
            }else {
                int likes = song.getLikes() + 1;
                song.setLikes(likes);
                list.add(user);
                songLikeMap.put(song,list);

                Album album=null;
                for(Album album1:albumSongMap.keySet()){
                    List<Song> songList = albumSongMap.get(album1);
                    if(songList.contains(song)){
                        album = album1;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist artist1:artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(artist1);
                    if (albumList.contains(album)){
                        artist = artist1;
                        break;
                    }
                }
                int likes1 = artist.getLikes() +1;
                artist.setLikes(likes1);
                artists.add(artist);
                return song;
            }
        }else {
            int likes = song.getLikes() + 1;
            song.setLikes(likes);
            List<User> list = new ArrayList<>();
            list.add(user);
            songLikeMap.put(song,list);

            Album album=null;
            for(Album album1:albumSongMap.keySet()){
                List<Song> songList = albumSongMap.get(album1);
                if(songList.contains(song)){
                    album = album1;
                    break;
                }
            }
            Artist artist = null;
            for(Artist artist1:artistAlbumMap.keySet()){
                List<Album> albumList = artistAlbumMap.get(artist1);
                if (albumList.contains(album)){
                    artist = artist1;
                    break;
                }
            }
            int likes1 = artist.getLikes() +1;
            artist.setLikes(likes1);
            artists.add(artist);

            return song;
        }
    }

    public String mostPopularArtist() {
        int max = 0;
        Artist artist1=null;

        for(Artist artist:artists){
            if(artist.getLikes()>=max){
                artist1=artist;
                max = artist.getLikes();
            }
        }
        if(artist1==null)
            return null;
        else
            return artist1.getName();
    }

    public String mostPopularSong() {
        int max = 0;
        Song song = null;

        for (Song song1 : songLikeMap.keySet()) {
            if (song1.getLikes() >= max) {
                song = song1;
                max = song1.getLikes();
            }
        }
        if (song == null)
            return null;
        else
            return song.getTitle();
    }
}