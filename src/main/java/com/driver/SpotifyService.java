package com.driver;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class SpotifyService {

    //Auto-wire will not work in this case, no need to change this and add autowire

    SpotifyRepository spotifyRepository = new SpotifyRepository();

    public User createUser(String name, String mobile){
        List<User> users = spotifyRepository.getAllUser();
        for(User user : users) {
            if(user.getName().equals(name) && user.getMobile().equals(mobile)) {
                return user;
            }
        }
        User user = spotifyRepository.createUser(name, mobile);
        return user;
    }

    public Artist createArtist(String name) {
        List<Artist> artists = spotifyRepository.getAllArtists();
        for(Artist artist : artists) {
            if(artist.getName().equals(name)) {
                return artist;
            }
        }
        Artist artist = spotifyRepository.createArtist(name);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        //If the artist does not exist, first create an artist with given name
        //Create an album with given title and artist
        Artist newArtist = null;
        List<Artist> artists = spotifyRepository.getAllArtists();
        boolean isArtistPresent = false;
        for(Artist artist : artists) {
            if(artist.getName().equals(artistName)) {
                newArtist = artist;
                isArtistPresent = true;
                break;
            }
        }
        if(!isArtistPresent) {
            newArtist = spotifyRepository.createArtist(artistName);
        }
        Album album = spotifyRepository.createAlbum(title, artistName);
        List<Album> newList = new ArrayList<>();
        newList.add(album);
        spotifyRepository.addArtistAndHisAlbumsList(newArtist, newList);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        //If the album does not exist in database, throw "Album does not exist" exception
        //Create and add the song to respective album
        List<Album> albums = spotifyRepository.getAllAlbums();
        Album newAlbum = null;
        boolean isAlbumPresent = false;
        for(Album album : albums) {
            if(album.getTitle().equals(albumName)) {
                newAlbum = album;
                isAlbumPresent = true;
                break;
            }
        }
        if(!isAlbumPresent) {
            throw new Exception("Album does not exist");
        } else {
            Song song = spotifyRepository.createSong(title, albumName, length);
            List<Song> newSongsList = new ArrayList<>();
            boolean isAlbumPresentAsKey = spotifyRepository.isAlbumPresentAsKey(newAlbum);
            if (isAlbumPresentAsKey) {
                newSongsList = spotifyRepository.getListOfSongsOfAlbum(newAlbum);
            }
            newSongsList.add(song);
            spotifyRepository.addAlbumAndHisSongsList(newAlbum, newSongsList);
            return song;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        List<User> users = spotifyRepository.getAllUser();
        User newUser = null;
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
        } else {
            Playlist newPlayList = spotifyRepository.createPlaylistOnLength(mobile, title, length);
            List<Song> newSongsList = new ArrayList<>();
            List<Song> songs = spotifyRepository.getAllSongs();
            for (Song song : songs) {
                if (song.getLength() == length) {
                    newSongsList.add(song);
                }
            }
            List<User> usersList = new ArrayList<>();
            spotifyRepository.addPlayListAndHisSongsList(newPlayList, newSongsList);
            usersList.add(newUser);
            spotifyRepository.addPlaylistAndHisUser(newPlayList, usersList);
            spotifyRepository.addUserAndHisPlaylist(newUser, newPlayList);

            List<Playlist> userPlaylist = new ArrayList<>();
            boolean isUserPlaylistPresent = spotifyRepository.isUserPlaylistPresent(newUser);
            if (isUserPlaylistPresent) {
                userPlaylist = spotifyRepository.getListOfPlaylistsOfUser(newUser);
            }
            userPlaylist.add(newPlayList);
            spotifyRepository.addUserAndHisPlaylists(newUser, userPlaylist);
            return newPlayList;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        List<User> users = spotifyRepository.getAllUser();
        User newUser = null;
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
        } else {
            Playlist newPlayList = spotifyRepository.createPlaylistOnName(mobile, title, songTitles);
            List<Song> newSongsList = new ArrayList<>();
            List<Song> songs = spotifyRepository.getAllSongs();
            for(Song song : songs) {
                if(songTitles.contains(song.getTitle())) {
                    newSongsList.add(song);
                }
            }
            spotifyRepository.addPlayListAndHisSongsList(newPlayList, newSongsList);
            List<User> usersList = new ArrayList<>();
            usersList.add(newUser);
            spotifyRepository.addPlaylistAndHisUser(newPlayList, usersList);
            spotifyRepository.addUserAndHisPlaylist(newUser, newPlayList);

            List<Playlist> userPlaylist = new ArrayList<>();
            boolean isUserPlaylistPresent = spotifyRepository.isUserPlaylistPresent(newUser);
            if (isUserPlaylistPresent) {
                userPlaylist = spotifyRepository.getListOfPlaylistsOfUser(newUser);
            }
            userPlaylist.add(newPlayList);
            spotifyRepository.addUserAndHisPlaylists(newUser, userPlaylist);
            return newPlayList;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        return spotifyRepository.findPlaylist(mobile, playlistTitle);
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        return spotifyRepository.likeSong(mobile, songTitle);
    }

    public String mostPopularArtist() {
        return spotifyRepository.mostPopularArtist();
    }

    public String mostPopularSong() {
        return spotifyRepository.mostPopularSong();
    }
}