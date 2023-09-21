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

    public SpotifyRepository() {
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
        for (User curUser : users) {
            if (curUser.getMobile().equals(mobile)) {
                return curUser;
            }
        }
        User user_obj = new User(name, mobile);
        users.add(user_obj);
        return user_obj;
    }

    public Artist createArtist(String name) {
        for (Artist artist : artists) {
            if (artist.getName().equals(name))
                return artist;
        }
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public boolean isArtistExist(String artistName) {
        for (Artist artist : artists) {
            if (artist.getName().equals(artistName)) {
                return true;
            }
        }
        return false;
    }

    public Artist getArtists(String artistName) {
        Artist artistObj = new Artist();
        for (Artist artist : artists) {
            if (artist.getName().equals(artistName)) {
                artistObj = artist;
            }
        }
        return artistObj;

    }

    public Album createAlbum(String title, String artistName) {
        Artist artist = createArtist(artistName);
        for (Album album : albums) {
            if (album.getTitle().equals(title))
                return album;
        }
        Album album = new Album(title);
        albums.add(album);

        List<Album> alb = new ArrayList<>();
        if (artistAlbumMap.containsKey(artist)) {
            alb = artistAlbumMap.get(artist);
        }
        alb.add(album);
        artistAlbumMap.put(artist, alb);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        if (isAlbumExist(albumName)) {
            Song song = new Song(title, length);
            songs.add(song);
            Album album = getAlbumByName(albumName);
            List<Song> songList = albumSongMap.getOrDefault(album, new ArrayList<>());
            songList.add(song);
            albumSongMap.put(album, songList);
            return song;
        }
        throw new Exception("Album does not exist");
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

        for (Playlist playlist : playlists) {
            if (playlist.getTitle().equals(title))
                return playlist;
        }

        if (!isUserExist(mobile)) {
            throw new Exception("User does not exist");
        }
        List<Song> songListOflength = getSongListOfGivenLength(length);
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        playlistSongMap.put(playlist, songListOflength);
        User user = getUserByMobileNumer(mobile);
        addPlayListListener(playlist, user);
        addCreatorPlayListMap(user, playlist);
        addUserPlayList(user, playlist);

        return playlist;

    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        for (Playlist playlist : playlists) {
            if (playlist.getTitle().equals(title))
                return playlist;
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        List<Song> temp = new ArrayList<>();
        for (Song song : songs) {
            if (songTitles.contains(song.getTitle())) {
                temp.add(song);
            }
        }
        playlistSongMap.put(playlist, temp);

        User curUser = new User();
        boolean flag = false;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                curUser = user;
                flag = true;
                break;
            }
        }
        if (flag == false) {
            throw new Exception("User does not exist");
        }

        List<User> userslist = new ArrayList<>();
        if (playlistListenerMap.containsKey(playlist)) {
            userslist = playlistListenerMap.get(playlist);
        }
        userslist.add(curUser);
        playlistListenerMap.put(playlist, userslist);

        creatorPlaylistMap.put(curUser, playlist);

        List<Playlist> userplaylists = new ArrayList<>();
        if (userPlaylistMap.containsKey(curUser)) {
            userplaylists = userPlaylistMap.get(curUser);
        }
        userplaylists.add(playlist);
        userPlaylistMap.put(curUser, userplaylists);

        return playlist;

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        boolean flag = false;
        Playlist playlist = new Playlist();
        for (Playlist curplaylist : playlists) {
            if (curplaylist.getTitle().equals(playlistTitle)) {
                playlist = curplaylist;
                flag = true;
                break;
            }
        }
        if (flag == false) {
            throw new Exception("Playlist does not exist");
        }

        User curUser = new User();
        boolean flag2 = false;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                curUser = user;
                flag2 = true;
                break;
            }
        }
        if (flag2 == false) {
            throw new Exception("User does not exist");
        }
        List<User> userslist = new ArrayList<>();
        if (playlistListenerMap.containsKey(playlist)) {
            userslist = playlistListenerMap.get(playlist);
        }
        if (!userslist.contains(curUser))
            userslist.add(curUser);
        playlistListenerMap.put(playlist, userslist);
        if (creatorPlaylistMap.get(curUser) != playlist)
            creatorPlaylistMap.put(curUser, playlist);
        List<Playlist> userplaylists = new ArrayList<>();
        if (userPlaylistMap.containsKey(curUser)) {
            userplaylists = userPlaylistMap.get(curUser);
        }
        if (!userplaylists.contains(playlist))
            userplaylists.add(playlist);
        userPlaylistMap.put(curUser, userplaylists);

        return playlist;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User curUser = new User();
        boolean flag2 = false;
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                curUser = user;
                flag2 = true;
                break;
            }
        }
        if (flag2 == false) {
            throw new Exception("User does not exist");
        }

        Song song = new Song();
        boolean flag = false;
        for (Song cursong : songs) {
            if (cursong.getTitle().equals(songTitle)) {
                song = cursong;
                flag = true;
                break;
            }
        }
        if (flag == false) {
            throw new Exception("Song does not exist");
        }

        List<User> users = new ArrayList<>();
        if (songLikeMap.containsKey(song)) {
            users = songLikeMap.get(song);
        }
        if (!users.contains(curUser)) {
            users.add(curUser);
            songLikeMap.put(song, users);
            song.setLikes(song.getLikes() + 1);

            Album album = new Album();
            for (Album curAlbum : albumSongMap.keySet()) {
                List<Song> temp = albumSongMap.get(curAlbum);
                if (temp.contains(song)) {
                    album = curAlbum;
                    break;
                }
            }

            Artist artist = new Artist();
            for (Artist curArtist : artistAlbumMap.keySet()) {
                List<Album> temp = artistAlbumMap.get(curArtist);
                if (temp.contains(album)) {
                    artist = curArtist;
                    break;
                }
            }

            artist.setLikes(artist.getLikes() + 1);
        }
        return song;
    }

    public String mostPopularArtist() {
        String name = "";
        int maxLikes = Integer.MIN_VALUE;
        for (Artist art : artists) {
            maxLikes = Math.max(maxLikes, art.getLikes());
        }
        for (Artist art : artists) {
            if (maxLikes == art.getLikes()) {
                name = art.getName();
            }
        }
        return name;
    }

    public String mostPopularSong() {
        String name = "";
        int maxLikes = Integer.MIN_VALUE;
        for (Song song : songs) {
            maxLikes = Math.max(maxLikes, song.getLikes());
        }
        for (Song song : songs) {
            if (maxLikes == song.getLikes())
                name = song.getTitle();
        }
        return name;
    }

    public boolean isAlbumExist(String albumName) {
        for (Album album : albums) {
            if (album.getTitle().equals(albumName)) {
                return true;
            }
        }
        return false;
    }

    private Album getAlbumByName(String albumName) {
        Album albumObj = null;
        for (Album album : albums) {
            if (album.getTitle().equals(albumName)) {
                albumObj = album;
            }
        }
        return albumObj;
    }

    private boolean isUserExist(String mobile) {
        boolean isExist = false;
        for (User user : users) {
            if (user.getMobile().equals(mobile))
                isExist = true;
        }
        return isExist;
    }

    private List<Song> getSongListOfGivenLength(int length) {
        List<Song> ListOfSong = new ArrayList<>();
        for (Song song : songs) {
            if (song.getLength() == length) {
                ListOfSong.add(song);
            }
        }
        return ListOfSong;
    }

    private User getUserByMobileNumer(String mobile) {
        User user = null;
        for (User userI : users) {
            if (userI.getMobile().equals(mobile)) {
                user = userI;
            }
        }
        return user;
    }

    private void addPlayListListener(Playlist playlist, User user) {
        List<User> userList = new ArrayList<>();
        if (playlistListenerMap.containsKey(playlist)) {
            userList = playlistListenerMap.get(playlist);
        }
        userList.add(user);
        playlistListenerMap.put(playlist, userList);
    }

    private void addUserPlayList(User user, Playlist playlist) {
        List<Playlist> playlistsList = new ArrayList<>();
        if (userPlaylistMap.containsKey(user)) {
            playlistsList = userPlaylistMap.get(user);
        }
        playlistsList.add(playlist);
        userPlaylistMap.put(user, playlistsList);

    }

    private void addCreatorPlayListMap(User user, Playlist playlist) {
        if (!creatorPlaylistMap.containsKey(user)) {
            creatorPlaylistMap.put(user, playlist);
        }
    }
}