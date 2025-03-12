package appevent.model;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity representing an image uploaded for an activity.
 */
@Entity
@Table(name = "activity_images")
public class ActivityImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User uploadedBy;

    @Lob
    @Column(name = "image_data", nullable = false)
    private byte[] imageData;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    protected ActivityImage() { }

    /**
     * Constructs a new ActivityImage.
     *
     * @param activity the activity this image belongs to
     * @param uploadedBy the user who uploaded this image
     * @param imageData the binary data of the image
     * @param fileName the original file name of the image
     */
    public ActivityImage(final Activity activity, final User uploadedBy, final byte[] imageData, final String fileName) {
        this.activity = activity;
        this.uploadedBy = uploadedBy;
        this.imageData = imageData;
        this.fileName = fileName;
    }

    /**
     * Gets the unique identifier of the image.
     *
     * @return the UUID of the image
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the activity this image belongs to.
     *
     * @return the activity
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the activity this image belongs to.
     *
     * @param activity the activity to set
     */
    public void setActivity(final Activity activity) {
        this.activity = activity;
    }

    /**
     * Gets the user who uploaded this image.
     *
     * @return the user
     */
    public User getUploadedBy() {
        return uploadedBy;
    }

    /**
     * Sets the user who uploaded this image.
     *
     * @param uploadedBy the user to set
     */
    public void setUploadedBy(final User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    /**
     * Gets the binary data of the image.
     *
     * @return the image data
     */
    public byte[] getImageData() {
        return imageData;
    }

    /**
     * Sets the binary data of the image.
     *
     * @param imageData the image data to set
     */
    public void setImageData(final byte[] imageData) {
        this.imageData = imageData;
    }

    /**
     * Gets the original file name of the image.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the original file name of the image.
     *
     * @param fileName the file name to set
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

}

