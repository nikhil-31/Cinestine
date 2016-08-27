package in.nikhil.cinestine.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nikhil on 20-08-2016.
 */
public class Movie implements Parcelable {
    private String mOriginalTitle;
    private String mPosterPath;
    private String mOverview;
    private Float mVoteAverage;
    private String mReleaseDate;
    private String mBackdrop;

    public Movie() {

    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;

    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;

    }

    public void setOverview(String overview) {
        mOverview = overview;

    }

    public void setVoteAverage(Float voteAverage) {
        mVoteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public void setBackdrop(String backdrop) {
        mBackdrop = backdrop;

    }

    public String getBackdrop() {
        String back = "http://image.tmdb.org/t/p/w500/" + mBackdrop;
        return back;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;

    }

    public String getPosterPath() {
        String url = "http://image.tmdb.org/t/p/w185/" + mPosterPath;
        return url;
    }

    public String getOverview() {
        return mOverview;

    }

    public String getVoteAverage() {
        String rating = mVoteAverage + "/10";
        return rating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    //Just to return data
    @Override
    public String toString() {
        return "Title" + mOriginalTitle +
                "Poster Path" + mPosterPath +
                "overview" + mOverview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeValue(mVoteAverage);
        dest.writeString(mReleaseDate);
        dest.writeString(mBackdrop);

    }

    private Movie(Parcel in) {
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        mVoteAverage = (Float) in.readValue(Double.class.getClassLoader());
        mReleaseDate = in.readString();
        mBackdrop = in.readString();
    }


    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {

            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
