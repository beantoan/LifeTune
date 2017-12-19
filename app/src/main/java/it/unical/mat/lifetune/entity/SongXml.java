package it.unical.mat.lifetune.entity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * Created by beantoan on 12/19/17.
 */

@Root(name = "track", strict = false)
public class SongXml extends BaseObservable {
    @Bindable
    @Element(name = "title", data = true)
    String title;

    @Bindable
    @Element(name = "creator", data = true)
    String singers;

    @Bindable
    @Element(name = "location", data = true)
    String url;

    @Bindable
    @Element(name = "locationHQ", data = true, required = false)
    String locationHQ;

    @Bindable
    @Element(name = "hasHQ", data = true, required = false)
    String hasHQ;

    @Bindable
    @Element(name = "info", data = true, required = false)
    String info;

    @Bindable
    @Element(name = "image", data = true, required = false)
    String image;

    @Bindable
    @Element(name = "bgimage", data = true, required = false)
    String bgimage;

    @Bindable
    @Element(name = "lyric", data = true, required = false)
    String lyric;

    @Bindable
    @Element(name = "newtab", data = true, required = false)
    String newtab;

    @Bindable
    @Element(name = "thumb", data = true, required = false)
    String thumb;

    @Bindable
    @Element(name = "kbit", data = true, required = false)
    String kbit;

    @Bindable
    @Element(name = "key", data = true, required = false)
    String key;

    public SongXml() {
    }

    public SongXml(String title, String singers, String url, String locationHQ, String hasHQ,
                   String info, String image, String bgimage, String lyric, String newtab,
                   String thumb, String kbit, String key) {
        this.title = title;
        this.singers = singers;
        this.url = url;
        this.locationHQ = locationHQ;
        this.hasHQ = hasHQ;
        this.info = info;
        this.image = image;
        this.bgimage = bgimage;
        this.lyric = lyric;
        this.newtab = newtab;
        this.thumb = thumb;
        this.kbit = kbit;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSingers() {
        return singers;
    }

    public void setSingers(String singers) {
        this.singers = singers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocationHQ() {
        return locationHQ;
    }

    public void setLocationHQ(String locationHQ) {
        this.locationHQ = locationHQ;
    }

    public String getHasHQ() {
        return hasHQ;
    }

    public void setHasHQ(String hasHQ) {
        this.hasHQ = hasHQ;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBgimage() {
        return bgimage;
    }

    public void setBgimage(String bgimage) {
        this.bgimage = bgimage;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getNewtab() {
        return newtab;
    }

    public void setNewtab(String newtab) {
        this.newtab = newtab;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getKbit() {
        return kbit;
    }

    public void setKbit(String kbit) {
        this.kbit = kbit;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
