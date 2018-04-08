package in.nikhil.cinestine.Model;

import in.nikhil.cinestine.Extras.TmdbUrls;

/**
 * Created by nikhil on 27-08-2016.
 */
public class Trailer {
  private String id;
  private String key;
  private String name;
  private String site;
  private String size;
  private String type;
  private String language;
  private String country;

  public Trailer() {
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setKey(String Key) {
    key = Key;
  }

  public String getKey() {
    return TmdbUrls.YOUTUBE_THUMB + key + TmdbUrls.YOUTUBE_MEDIUM_QUALITY;
  }

  public String getTrailer() {
    return TmdbUrls.YOUTUBE_URL + key;
  }

  public void setName(String Name) {
    name = Name;
  }

  public String getName() {
    return name;
  }

  public void setSite(String Site) {
    site = Site;
  }

  public String getSite() {
    return site;
  }

  public void setSize(String Size) {
    size = Size;
  }

  public String getSize() {
    return size;
  }

  public void setType(String Type) {
    type = Type;
  }

  public String getType() {
    return type;
  }

  public void setLanguage(String Language) {
    language = Language;
  }

  public String getLanguage() {
    return language;
  }

  public void setCountry(String Country) {
    country = Country;
  }

  public String getCountry() {
    return country;
  }
}
