package org.teiath.data.paging;

import java.util.ArrayList;
import java.util.Collection;

public class SearchResults<E> {

	private int totalPages;
	private int totalRecords;
	private Collection<E> data = new ArrayList<>();

	public SearchResults() {
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public Collection<E> getData() {
		return data;
	}

	public void setData(Collection<E> data) {
		this.data = data;
	}
}
