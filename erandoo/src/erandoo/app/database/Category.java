package erandoo.app.database;

import java.io.Serializable;

public class Category implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "category";
	public static final String FLD_CATEGORY_ID = "category_id";
	public static final String FLD_IS_VIRTUAL = "is_virtual";
	public static final String FLD_IS_INPERSON = "is_inperson";
	public static final String FLD_IS_INSTANT = "is_instant";
	public static final String FLD_PARENT_ID = "parent_id";
	public static final String FLD_SUB_CATEGORY_CNT = "subcategory_cnt";
	public static final String FLD_CATEGORY_NAME = "category_name";
	public static final String FLD_CATEGORY_IMAGE = "category_image";
	public static final String FLD_TASK_TEMPLATE = "task_templates";
	public static final String FLD_CATEGORY_STATUS = "category_status";

//	private long categoryId = -1;
//	private int subCategoryCount = 0;
//	private String categoryName = "";
//	private String categoryImage = "";
//	private String taskTemplate = "";
	
	public Long category_id;
	public String is_virtual;
	public String is_inperson;
	public String is_instant;
	public Long parent_id;
	public String subcategory_cnt;
	public String category_name;
	public String category_image;
	public String task_templates;
	public String category_status;
	public Long trno;
	public boolean isChecked;
	public Category(){
		
	}

//	public long getCategoryId(){
//		return categoryId;
//	}
//	public void setCategoryId(long categoryId) {
//		this.categoryId = categoryId;
//	}
//	
//	public int getSubCategoryCount() {
//		return subCategoryCount;
//	}
//	public void setSubCategoryCount(int subCategoryCount) {
//		this.subCategoryCount = subCategoryCount;
//	}
//	
//	public String getCategoryName() {
//		return categoryName;
//	}
//	public void setCategoryName(String categoryName) {
//		this.categoryName = categoryName;
//	}
//	
//	public String getCategoryImage() {
//		return categoryImage;
//	}
//	
//	public void setCategoryImage(String categoryImage) {
//		this.categoryImage = categoryImage;
//	}
//	
//	public String getTaskTemplate() {
//		return taskTemplate;
//	}
//
//	public void setTaskTemplate(String taskTemplate) {
//		this.taskTemplate = taskTemplate;
//	}
}
