package android.mangaliso_elvynn.practical5;

public class Movie {

    private String mTitle;
    private String mDate;
    private String mIMDB;
    private String mSynopsis;
    private String mImage;

    public String getImage() {
        return mImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDate() {
        return mDate;
    }

    public String getIMDB() {
        return mIMDB;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public Movie(String mTitle, String mDate, String mIMDB, String mSynopsis, String mImage) {
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mIMDB = mIMDB;
        this.mSynopsis = mSynopsis;
        this.mImage = mImage;
    }
}
