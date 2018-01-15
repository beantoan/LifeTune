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

    private Boolean isPlaying = false;

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
        return StringUtils.strip(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSingers() {
        return StringUtils.strip(singers);
    }

    public void setSingers(String singers) {
        this.singers = singers;
    }

    public String getUrl() {
        return StringUtils.strip(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return StringUtils.strip(image);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBgimage() {
        return StringUtils.strip(bgimage);
    }

    public void setBgimage(String bgimage) {
        this.bgimage = bgimage;
    }

    public String getThumb() {
        return StringUtils.strip(thumb);
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getAvatar() {
        return StringUtils.strip(avatar);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Bindable
    public Boolean getPlaying() {
        return isPlaying;
    }

    public void setPlaying(Boolean playing) {
        isPlaying = playing;
        notifyChange();
    }

    public String getCombinedTitle() {

        StringBuilder combinedTitle = new StringBuilder()
                .append(getTitle())
                .append(" >> ")
                .append(getSingers());

        return combinedTitle.toString();
    }

    public String getPlayerAvatar() {
        if (StringUtils.isNotBlank(getAvatar())) {
            return getAvatar();
        }

        if (StringUtils.isNotBlank(getImage())) {
            return getImage();
        }

        if (StringUtils.isNotBlank(getThumb())) {
            return getThumb();
        }

        if (StringUtils.isNotBlank(getBgimage())) {
            return getBgimage();
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
