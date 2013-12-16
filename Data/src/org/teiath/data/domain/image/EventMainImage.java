package org.teiath.data.domain.image;

import org.teiath.data.domain.act.Event;
import org.zkoss.image.AImage;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Entity
@Table(name = "event_main_images")
public class EventMainImage
		implements Serializable {

	@Id
	@Column(name = "event_main_image_id")
	@SequenceGenerator(name = "event_main_images_seq", sequenceName = "event_main_images_event_main_image_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_main_images_seq")
	private Integer id;

	@Column(name = "image_bytes", nullable = true)
	private byte[] imageBytes;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "eventMainImage")
	private Event event;

	private AImage image;

	public EventMainImage() {
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
		return obj != null && this.id != null && obj.getClass() == EventMainImage.class && this.id
				.equals(((EventMainImage) obj).getId());
	}
}
