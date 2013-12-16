package org.teiath.data.domain.pdf;

import org.teiath.data.domain.act.Event;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "event_pdfs")
public class EventPDF
		implements Serializable {

	@Id
	@Column(name = "event_pdf_id")
	@SequenceGenerator(name = "event_pdfs_seq", sequenceName = "event_pdfs_event_pdf_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_pdfs_seq")
	private Integer id;

	@Column(name = "file_bytes", nullable = true)
	private byte[] fileBytes;

	@Column(name = "file_name", nullable = true)
	private String fileName;

	@Column(name = "content_type", nullable = false)
	private String contentType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "event", nullable = true)
	private Event event;

	public EventPDF() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getFileBytes() {
		return fileBytes;
	}

	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == EventPDF.class && this.id
				.equals(((EventPDF) obj).getId());
	}
}
