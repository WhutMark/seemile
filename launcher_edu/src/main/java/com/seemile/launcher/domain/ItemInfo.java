package com.seemile.launcher.domain;

/**
 * Created by whuthm on 2016/1/8.
 */
public class ItemInfo {

	public static final int NO_ID = -1;

	/**
	 * The id in the settings database for this item
	 */
	public long id = NO_ID;

	public int itemType;

	/**
	 * The id of the container that holds this item
	 */
	public long container = NO_ID;

	/**
	 * Indicates the screen
	 */
	public int screen = -1;

	/**
	 * Indicates the X position of the associated cell.
	 */
	public int cellX = -1;

	/**
	 * Indicates the Y position of the associated cell.
	 */
	public int cellY = -1;

	/**
	 * Indicates the X cell span.
	 */
	public int spanX = 1;

	/**
	 * Indicates the Y cell span.
	 */
	public int spanY = 1;

	public String title;

	public ItemInfo() {
	}

	public ItemInfo(ItemInfo info) {
		id = info.id;
		cellX = info.cellX;
		cellY = info.cellY;
		spanX = info.spanX;
		spanY = info.spanY;
		screen = info.screen;
		itemType = info.itemType;
		container = info.container;
	}

	@Override
	public String toString() {
		return "Item(id=" + this.id + " type=" + this.itemType + " container="
				+ this.container + " screen=" + screen + " cellX=" + cellX
				+ " cellY=" + cellY + " spanX=" + spanX + " spanY=" + spanY
				+ ")";
	}

}
