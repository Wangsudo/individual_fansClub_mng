package com.soccer.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 分页数据包装，包括分页信息和List数据
 * 
 */
public class PageResult<T> implements Serializable
{
	private int totalPage;
	private int totalRecord;
	private int currentPage;
	private int pageSize;
	private List<T> list;

	public PageResult() {
		this(0, 1, 10, null);
	}

	public PageResult(int totalRecord, int currentPage, int pageSize,
			List<T> list) {

		this.totalRecord = totalRecord;
		if (currentPage <= 0) {
			this.currentPage = 1;
		} else {
			this.currentPage = currentPage;
		}

		if (pageSize <= 0) {
			this.pageSize = 10;
		} else {
			this.pageSize = pageSize;
		}
		if (list == null) {
			this.list = new ArrayList();
		} else {
			this.list = list;
		}
		this.totalPage = getTotalPage(totalRecord, this.pageSize);
	}

	protected int getTotalPage(int totalRecord, int max_per_page) {
		int temp = totalRecord / max_per_page;
		if (totalRecord % max_per_page != 0)
			temp++;
		return temp;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return getTotalPage(this.totalRecord, this.pageSize);
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
}
