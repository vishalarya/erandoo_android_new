package erandoo.app.database;
public enum TableType {
	SkillTable(1,Skill.TABLE_NAME),
	CategoryTable(2,Category.TABLE_NAME),
	CategorySkillTable(3,CategorySkill.TABLE_NAME),
	CategoryQuestionTable(4,Question.TABLE_NAME),
	CountryTable(5,Country.TABLE_NAME),
	TaskTable(6,Project.TABLE_NAME),
	TaskSkillTable(7,TaskSkill.TABLE_NAME),
	TaskQuestionTable(8,TaskQuestion.TABLE_NAME),
	TaskLocationTable(9,TaskLocation.TABLE_NAME),
	TaskDoerTable(10,TaskDoer.TABLE_NAME),
	WorkLocationTable(11,WorkLocation.TABLE_NAME),
	InboxUserTable(12,MessageDetail.TABLE_NAME),
	PaymentRequestTable(13,PaymentRequest.TABLE_NAME),
	PaymentTable(14,Payment.TABLE_NAME),
	TaskAttachmentTable(15,Attachment.TABLE_NAME);

	private int index;
	private String tableName;
	
	TableType(int index , String tableName)
	{
		this.index = index;
		this.tableName = tableName;
	}

	public int getIndex()
	{
		return index;
	}

	public String getTableName() 
	{
		return tableName;
	}
}