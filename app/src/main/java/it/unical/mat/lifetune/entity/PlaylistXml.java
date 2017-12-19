package it.unical.mat.lifetune.entity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by beantoan on 12/19/17.
 */

@Root(name = "tracklist", strict = false)
public class PlaylistXml extends BaseObservable {
    @Bindable
    @ElementList(name = "track", entry = "track", inline = true, type = SongXml.class)
    List<SongXml> songs;


    public PlaylistXml() {
    }

    public PlaylistXml(List<SongXml> songs) {
        this.songs = songs;
    }

    public List<SongXml> getSongs() {
        return songs;
    }

    public void setSongs(List<SongXml> songs) {
        this.songs = songs;
    }
}
