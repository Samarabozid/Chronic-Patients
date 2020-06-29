package android.android.chronicpatients.Models;

public class PhotoModel {
    String photoID, imageurl, doctorComment;
    int commented;

    public PhotoModel() {
    }

    public PhotoModel(String id, String imageurl, String doctorComment, int commented) {
        this.photoID = id;
        this.imageurl = imageurl;
        this.doctorComment = doctorComment;
        this.commented = commented;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDoctorComment() {
        return doctorComment;
    }

    public void setDoctorComment(String doctorComment) {
        this.doctorComment = doctorComment;
    }

    public int getCommented() {
        return commented;
    }

    public void setCommented(int commented) {
        this.commented = commented;
    }
}
