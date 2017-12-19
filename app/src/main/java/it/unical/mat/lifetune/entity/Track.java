package it.unical.mat.lifetune.entity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * Created by beantoan on 12/19/17.
 */

@Root(name = "track", strict = false)
public class Track extends BaseObservable {
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
    @Element(name = "image", data = true, required = false)
    String image;

    @Bindable
    @Element(name = "bgimage", data = true, required = false)
    String bgimage;

    @Bindable
    @Element(name = "thumb", data = true, required = false)
    String thumb;

    @Bindable
    @Element(name = "avatar", data = true, required = false)
    String avatar;

    public Track() {
    }

    public Track(String title, String singers, String url, String image, String bgimage, String thumb, String avatar) {
        this.title = title;
        this.singers = singers;
        this.url = url;
        this.image = image;
        this.bgimage = bgimage;
        this.thumb = thumb;
        this.avatar = avatar;
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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCombinedTitle() {
        return StringUtils.strip(getTitle()) + " - " + StringUtils.strip(getSingers());
    }

    public String getPlayerAvatar() {
        if (StringUtils.isNotBlank(getAvatar())) {
            return StringUtils.strip(getAvatar());
        }

        if (StringUtils.isNotBlank(getImage())) {
            return StringUtils.strip(getImage());
        }

        if (StringUtils.isNotBlank(getThumb())) {
            return StringUtils.strip(getThumb());
        }

        if (StringUtils.isNotBlank(getBgimage())) {
            return StringUtils.strip(getBgimage());
        }

        return null;
    }

    @Override
    public String toString() {
        return "Track{" +
                "title='" + title + '\'' +
                ", singers='" + singers + '\'' +
                ", url='" + url + '\'' +
                ", image='" + image + '\'' +
                ", bgimage='" + bgimage + '\'' +
                ", thumb='" + thumb + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
