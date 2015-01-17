package erandoo.app.utilities;

import org.json.JSONObject;

import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.IVCheckListener;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import android.content.Context;
import android.os.AsyncTask;

public class LicenseValidation extends AsyncTask<Void, Void, NetworkResponse>{ 
	
	public static final String VALIDATION_ON_BID = "bid";
    public static final String VALIDATION_ON_PROJECT_POST = "project_post";
    public static final String VALIDATION_ON_HIRE_BY_POSTER = "hire_by_poster";
    public static final String VALIDATION_ON_CONFIRM_HIRING_BY_DOER = "confirm_hiring_by_doer";
    public static final String VALIDATION_ON_INVOICE_UPLOAD_BY_DOER = "invoice_upload_by_doer";
    public static final String VALIDATION_ON_INVOICE_PAY_BY_POSTER = "invoice_pay_by_poster";
    
    private final String FLD_PLAN_VALUE_IS_PERCENT = "plan_value_is_percent";
    private final String FLD_PLAN_VALUE = "plan_value";
	private final String FLD_VALIDATION_ON = "validation_on";
    private final String FLD_TASK_ID = "task_id";
    
    private Long taskId = null;
    private String vOn;
	private Context ctx;
	private String plan_value_is_percent;
	private String plan_value;
	private IVCheckListener listener;
	/**
	 * Initialize validation task
	 * @param ctx context
	 * @param vOn validation type it may be  bid ,project_post ,hire_by_poster,
	 * confirm_hiring_by_doer,invoice_upload_by_doer,invoice_pay_by_poster
	 * @param taskId Task id required field only for validation type bid and confirm_hiring_by_doer else send null 
	 * @return
	 */
	public static LicenseValidation getInstance(Context ctx, String vOn, Long taskId,IVCheckListener listener) {
		LicenseValidation validation;
		validation = new LicenseValidation();
		validation.ctx = ctx;
		validation.taskId = taskId;
		validation.vOn = vOn;
		validation.listener = listener;
		return validation;
	}
     
    @Override
    protected void onPreExecute() {
    	Util.showProDialog(ctx); 
    	Util.progressDialog.setMessage("Validating your Plan and License"); 
    }
	
	@Override
	protected NetworkResponse doInBackground(Void... params) {
		NetworkResponse response = null;
		if(Util.isDeviceOnline(ctx)){
			NetworkResponse.isDeviceOnline = true;
			Cmd cmd = CmdFactory.createValidaionActionCmd();
			try{
				cmd.addData(FLD_VALIDATION_ON, vOn);
				cmd.addData(FLD_TASK_ID, taskId); 
				response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
				if (response != null) {
					if (response.isSuccess()) {
						if(vOn.equals(VALIDATION_ON_BID)){ 
							JSONObject data = response.getJsonObject().getJSONObject(Constants.FLD_DATA);
							plan_value_is_percent = data.getString(FLD_PLAN_VALUE_IS_PERCENT);
							plan_value = data.getString(FLD_PLAN_VALUE);
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			NetworkResponse.isDeviceOnline = false;
		}
		return response;
	}
	
	@Override
	protected void onPostExecute(NetworkResponse response) {
		super.onPostExecute(response); 
		Util.dimissProDialog();
		if(NetworkResponse.isDeviceOnline){
			if (response != null) {
				if (response.isSuccess()) {
					listener.onValidationChecked(plan_value_is_percent,plan_value);
				} else {
					Util.showCenteredToast(ctx,response.getErrMsg());
				}
			}
		}else{
			Util.showCenteredToast(ctx, ctx.getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	public static String calculateMyPay(String pPay, double palnValue,boolean isPercent){
		double totalMyPay = 0;
		try{
			double mPay = Util.toDouble(pPay);
			if(mPay > 0){
				if(isPercent){
					totalMyPay = ( mPay * 100 ) / (100 + palnValue);	
				}else{
					totalMyPay =  mPay + palnValue ;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return String.valueOf(String.format("%.2f", totalMyPay)); 
	}
	
	public static String calculatePosterPay(String myPay, double palnValue,boolean isPercent){
		double totalApprovedCost = 0; 
		try{
			double mPay = Util.toDouble(myPay);
			if(mPay > 0){
				if(isPercent){
					double servicefees = ( palnValue / 100 ) * mPay;
					totalApprovedCost = mPay + servicefees; 
				}else{
					totalApprovedCost = mPay + palnValue; 
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return String.valueOf(String.format("%.3f", totalApprovedCost)); 
	}
	
	public static String calculateProjectCost(String taskKind,String mExpenses,String mPrice,String mMinPrice,String mMaxPrice,String mWHours) {
		double estimatedCost = 0;
		try{
			double expenses = Util.toDouble(mExpenses);
			if (taskKind.equals(Constants.INSTANT)) {
				double price = Util.toDouble(mPrice);
				estimatedCost = expenses + price;
			} else {
				double minPrice = Util.toDouble(mMinPrice);
				double maxPrice = Util.toDouble(mMaxPrice);
				int wHours = Util.toInt(mWHours);

				if (maxPrice > 0 && minPrice > 0) {
					estimatedCost = (maxPrice + minPrice) / 2;
				}

				if (wHours > 0) {
					estimatedCost =  wHours * estimatedCost;
				}

				if (expenses > 0) {
					estimatedCost = estimatedCost + expenses;
				}

				estimatedCost = Math.round(estimatedCost);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return String.valueOf((long)estimatedCost);
	}

	
}
