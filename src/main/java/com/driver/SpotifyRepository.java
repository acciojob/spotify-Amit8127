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
        for(User u:users)
        {
            if(u.getMobile().equals(mobile))
                return u;
        }
        User user=new User(name,mobile);
        //null pointer
        // userPlaylistMap.put(user, new ArrayList<>());
        users.add(user);
        return user;
    }

    public Artist createArtist(String name)
    {
        for(Artist a:artists)
        {
            if(a.getName().equals(name))
                return a;
        }
        Artist artist=new Artist(name);
        //album
        //  artistAlbumMap.put(artist, new ArrayList<>());
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName)
    {
        //  int k=0;
        //    for(Album album:albums)
        Artist art=null;
        Album album=new Album();
        albums.add(album);
        for(Artist a:artists)
        {
            if(a.getName().equals(artistName))
            {
                art=a;
                break;
            }
        }
        if(art==null)
        {
            Artist artist=new Artist(artistName);
            artists.add(artist);
            art=artist;

        }
        List<Album>Albumlist=artistAlbumMap.getOrDefault(art,new ArrayList<>());
        Albumlist.add(album);
        artistAlbumMap.put(art,Albumlist);

        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception
    {
        Album album=null;
        for(Album al:albums)
        {
            if(al.getTitle().equals(albumName))
            {
                album=al;
                break;
            }
        }
        if(album==null)
            throw new Exception("Album does not exist");
        else
        {
            Song song=new Song(title,length);
            List<Song>songlist;

            songlist=albumSongMap.getOrDefault(album,new ArrayList<>());
            songlist.add(song);
            songs.add(song);
            return song;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception
    {
        User user=null;

        for(User us:users)
        {
            if(us.getMobile()==mobile)
            {
                user=us;
                break;
            }
        }
        if(user==null) {
            throw new Exception("User does not exist");
        }
        else
        {
            Playlist playlist=new Playlist(title);
            playlists.add(playlist);
            List<Song>songlist=null;
            for(Song song:songs)
            {
                if(song.getLength()==length)
                {
                    songlist=playlistSongMap.getOrDefault(playlist,new ArrayList<>());
                    songlist.add(song);
                    playlistSongMap.put(playlist,songlist);
                }
            }

            List<User>userlist=new ArrayList<>();
            //userlist=playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
            userlist.add(user);
            playlistListenerMap.put(playlist,userlist);
            creatorPlaylistMap.put(user,playlist);
            List<Playlist>listofPlaylist;
            listofPlaylist=userPlaylistMap.getOrDefault(playlist,new ArrayList<>());
            listofPlaylist.add(playlist);
            userPlaylistMap.put(user,listofPlaylist);
            return playlist;
        }
    }


    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user=null;
        for(User us:users)
        {
            if(us.getMobile().equals(mobile)) {
                user = us;
                break;
            }
        }
        if(user==null)
        {
            throw new Exception("User does not exist");
        }
        else
        {
            Playlist playlist=new Playlist(title);
            List<Song>songlist;
            for(Song song:songs)
            {
                if(songTitles.contains(song.getTitle()))
                {
                    songlist=playlistSongMap.getOrDefault(playlist,new ArrayList<>());
                    songlist.add(song);
                    playlistSongMap.put(playlist,songlist);
                }

            }
            playlists.add(playlist);
            //copp
            List<User>userlist;
            userlist=playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
            userlist.add(user);
            playlistListenerMap.put(playlist,userlist);
            creatorPlaylistMap.put(user,playlist);
            List<Playlist>listofPlaylist;
            listofPlaylist=userPlaylistMap.getOrDefault(playlist,new ArrayList<>());
            listofPlaylist.add(playlist);
            userPlaylistMap.put(user,listofPlaylist);
            return playlist;
        }

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception
    {
        User user=null;
        for(User us:users)
        {
            if(us.getMobile().equals(mobile))
            {
                user=us;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");
        else
        {
            Playlist playList=null;
            for(Playlist pl:playlists)
            {
                if(pl.getTitle().equals(playlistTitle))
                {
                    playList=pl;
                }
            }
            if(playList==null)
                throw new Exception("Playlist does not exist");
            else
            {
                List<User>userlist = null;
                List<Playlist>playlistList;
                if(playlistListenerMap.containsKey(playList))
                {
                    if(!(playlistListenerMap.get(playList).contains(user)))
                    {
                        userlist=playlistListenerMap.get(playList);

                    }
                }
                else {
                    userlist=new ArrayList<>();
                    creatorPlaylistMap.put(user,playList);
                }
                userlist.add(user);
                playlistListenerMap.put(playList,userlist);
                if(userPlaylistMap.containsKey(user))
                {
                    playlistList=userPlaylistMap.get(user);
                }
                else {
                    playlistList=new ArrayList<>();
                }
                playlistList.add(playList);
                userPlaylistMap.put(user,playlistList);
                return playList;
            }
        }


    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        Song song = null;
        User user=null;
        for (Song so : songs) {
            if (so.getTitle().equals(songTitle)) {
                song = so;
                break;
            }

        }
        if (song == null)
            throw new Exception("Song does not exist");
        else {
            for (User us : users) {
                if (us.getMobile().equals(mobile)) {
                    user = us;
                    break;
                }
            }
            if (user == null) {
                throw new Exception("User does not exist");
            }
            else
            {
                if(!songLikeMap.containsKey(song))
                {
                    song.setLikes(song.getLikes()+1);

                }
                Album currAlbum = null;
                for(Album album: albumSongMap.keySet()){
                    if(albumSongMap.get(album).contains(song)){
                        currAlbum = album;
                    }
                }
                //now we have album of the artist and we can find artist
                Artist currArtist = null;
                for(Artist artist: artistAlbumMap.keySet()){
                    if(artistAlbumMap.get(artist).contains(currAlbum)){
                        currArtist = artist;
                    }
                }
                if (currArtist==null){
                    throw new Exception("");
                }

                //increase like count of an artist
                currArtist.setLikes(currArtist.getLikes()+1);
                return  song;
            }


        }
    }


    public String mostPopularArtist()
    {
        int max=0;
        String name="";
        for(Artist artist:artists)
        {
            if(artist.getLikes()>max)
            {
                max= artist.getLikes();
                name=artist.getName();
            }
        }
        return name;
    }

    public String mostPopularSong() {
        int max=0;
        String name="";
        for(Song song:songs)
            if(song.getLikes()>max)
            {
                max= song.getLikes();
                name=song.getTitle();
            }
        return name;
    }

}


