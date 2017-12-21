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
public class TrackList extends BaseObservable {
    @Bindable
    @ElementList(name = "track", entry = "track", inline = true, type = Track.class)
    List<Track> tracks;


    public TrackList() {
    }

    public TrackList(List<Track> tracks) {
        this.tracks = tracks;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
