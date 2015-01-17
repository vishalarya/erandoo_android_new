package erandoo.app.custom;

/**
 * @author Sanjay Jangid
 * this interface to handle response returned from validation check
 * task.
 */
public interface IVCheckListener {
	 public abstract void onValidationChecked(String isPercent , String planValue);
}
