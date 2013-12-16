package org.teiath.data.domain.image;

import org.teiath.data.domain.act.Event;
import org.zkoss.image.AImage;

import javax.persistence.*;
import java.io.IOException;

@Entity
@Table(name = "application_images")
public class ApplicationImage {

	@Id
	@Column(name = "application_image_id")
	@SequenceGenerator(name = "application_images_seq", sequenceName = "application_images_application_image_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_images_seq")
	private Integer id;

	@Column(name = "image_bytes", nullable = true)
	private byte[] imageBytes;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "event", nullable = true)
	private Event event;

	private AImage image;

	public ApplicationImage() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getImageBytes() {
		return imageBytes;
	}

	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public AImage getImage()
			throws IOException {
		return new AImage("Image", this.getImageBytes());
	}

	public void setImage(AImage image) {
		this.image = image;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == ApplicationImage.class && this.id
				.equals(((ApplicationImage) obj).getId());
	}
}
