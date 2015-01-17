package erandoo.app.main.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.Money;
import erandoo.app.utilities.Util;

public class MoneyDetailActivity extends BaseFragActivity implements
		OnClickListener {

	private AppHeaderView appHeaderView;
	private TextView txtMnyDetailDesc;
	private TextView txtMnyDetailUserName;
	private TextView txtMnyDetailDate;
	private TextView txtMnyDetailDebit;
	private TextView txtMnyDetailCredit;
	private TextView txtMnyBalance;
	private TextView txtMnyProjLink;
	private TextView txtMnyInvoiceLink;
	private Money moneyObj;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.ed_setting_money_details);
		moneyObj = (Money) getIntent().getExtras().get("data");
		init();
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.Money), null, null, Gravity.CENTER);
		setDataToFields();

	}

	private void init() {

		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		txtMnyDetailUserName = (TextView) findViewById(R.id.txtMnyDetailUserName);
		txtMnyDetailDesc = (TextView) findViewById(R.id.txtMnyDetailDesc);
		txtMnyDetailDate = (TextView) findViewById(R.id.txtMnyDetailDate);
		txtMnyDetailDebit = (TextView) findViewById(R.id.txtMnyDetailDebit);
		txtMnyDetailCredit = (TextView) findViewById(R.id.txtMnyDetailCredit);
		txtMnyBalance= (TextView) findViewById(R.id.txtMnyBalance);
		txtMnyProjLink= (TextView) findViewById(R.id.txtMnyProjLink);
		txtMnyInvoiceLink= (TextView) findViewById(R.id.txtMnyInvoiceLink);
		
		txtMnyProjLink.setOnClickListener(this);
		txtMnyInvoiceLink.setOnClickListener(this);

	}

	private void setDataToFields() {
		txtMnyDetailUserName.setText(moneyObj.user_name);
		txtMnyDetailDesc.setText(moneyObj.reason);
		txtMnyDetailDate.setText(Util.setProjectDateWithMonth(moneyObj.created_at, false));
		txtMnyDetailDebit.setText(moneyObj.debit+" "+getResources().getString(R.string.dollorData));
		txtMnyDetailCredit.setText(moneyObj.credit+" "+getResources().getString(R.string.dollorData));
		int value = (Integer) ((moneyObj.balance == null)? 0 : moneyObj.balance);
		txtMnyBalance.setText(value +" "+getResources().getString(R.string.dollorData));		
		txtMnyProjLink.setText(Html.fromHtml("<u>"+moneyObj.project_title+"</u>"));
		txtMnyInvoiceLink.setText(Html.fromHtml("<u>"+moneyObj.invoice_id+"</u>"));
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int vId = v.getId();
		if (vId == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		}
		else if(vId==R.id.txtMnyProjLink)
		{
			goToBrowser(moneyObj.project_url);
		}
		else if(vId==R.id.txtMnyInvoiceLink)
		{
			goToBrowser(moneyObj.invoice_url);
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finishActivity();
	}
	private void finishActivity() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
	
	private void  goToBrowser(String url)
	{
//		if(url.contains("http://www."))
//		{
//			
//		}
		if(URLUtil.isValidUrl(url))
		{
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
		}
	}

}
