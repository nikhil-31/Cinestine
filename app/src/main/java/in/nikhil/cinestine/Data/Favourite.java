package in.nikhil.cinestine.Data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.internal.SharedGroup;

/**
 * Created by nikhil on 01-09-2016.
 */
public class Favourite extends RealmObject {

    private String mOriginalTitle;
    private String mPosterPath;
    private String mOverview;
    private String mVoteAverage;
    private String mReleaseDate;
    private String mBackdrop;
    @PrimaryKey
    private String mId;
    private String mPopularity;
    private String mVoteCount;
    private String mOriginalLanguage;
    private String mTitle;
    private String mAdult;

    public Favourite(String mOriginalTitle, String mPosterPath, String mOverview, String mVoteAverage, String mReleaseDate, String mBackdrop, String mId, String mPopularity, String mVoteCount, String mOriginalLanguage, String mTitle, String mAdult) {
        this.mOriginalTitle = mOriginalTitle;
        this.mPosterPath = mPosterPath;
        this.mOverview = mOverview;
        this.mVoteAverage = mVoteAverage;
        this.mReleaseDate = mReleaseDate;
        this.mBackdrop = mBackdrop;
        this.mId = mId;
        this.mPopularity = mPopularity;
        this.mVoteCount = mVoteCount;
        this.mOriginalLanguage = mOriginalLanguage;
        this.mTitle = mTitle;
        this.mAdult = mAdult;
    }

    public Favourite() {
    }


    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public void setmOriginalTitle(String mOriginalTitle) {
        this.mOriginalTitle = mOriginalTitle;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public String getmOverview() {
        return mOverview;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getmVoteAverage() {
        return mVoteAverage;
    }

    public void setmVoteAverage(String mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getmBackdrop() {
        return mBackdrop;
    }

    public void setmBackdrop(String mBackdrop) {
        this.mBackdrop = mBackdrop;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmPopularity() {
        return mPopularity;
    }

    public void setmPopularity(String mPopularity) {
        this.mPopularity = mPopularity;
    }

    public String getmOriginalLanguage() {
        return mOriginalLanguage;
    }

    public void setmOriginalLanguage(String mOriginalLanguage) {
        this.mOriginalLanguage = mOriginalLanguage;
    }

    public String getmVoteCount() {
        return mVoteCount;
    }

    public void setmVoteCount(String mVoteCount) {
        this.mVoteCount = mVoteCount;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmAdult() {
        return mAdult;
    }

    public void setmAdult(String mAdult) {
        this.mAdult = mAdult;
    }


}
