package erandoo.app.cmds;

import org.json.JSONArray;
import org.json.JSONObject;

import erandoo.app.config.Config;
import erandoo.app.config.DeviceInfo;
import erandoo.app.database.Category;
import erandoo.app.database.Country;
import erandoo.app.database.NotificationSetting;
import erandoo.app.database.Project;
import erandoo.app.database.Skill;
import erandoo.app.database.WorkLocation;
import erandoo.app.main.AppGlobals;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class CmdFactory {
	
	public static Cmd createSignInCmd(String signinId, String pwd, String loginWith){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_SIGNIN;
			}
		};
		
		cmd.addData(Constants.FLD_EMAIL_ID, signinId);
		cmd.addData(Constants.FLD_PASSWORD, pwd);
		cmd.addData(Constants.FLD_LOGIN_WITH, loginWith);
		cmd.addData(DeviceInfo.FLD_SIM_SERIAL_NUMBER, Config.deviceInfo.getSimSerialNumber()); 
		cmd.addData(DeviceInfo.FLD_DEVICE_MAC_ADDRESS, Config.deviceInfo.getDeviceMacAddress()); 
		cmd.addData(DeviceInfo.FLD_EMEI_MEID_ESN, Config.deviceInfo.getEmeiMeidEsn()); 
		cmd.addData(DeviceInfo.FLD_APP_TOKEN, Config.deviceInfo.getAppToken()); 
		cmd.addData(DeviceInfo.FLD_DEVICE_ID, Config.deviceInfo.getDeviceId()); 
		cmd.addData(DeviceInfo.FLD_APP_VERSION, Config.deviceInfo.getAppVersion()); 
		cmd.addData(DeviceInfo.FLD_DEVICE_MODEL, Config.deviceInfo.getDeviceModel()); 
		cmd.addData(DeviceInfo.FLD_DEVICE_DENSITY, Config.deviceInfo.getDeviceDensity()); 
		cmd.addData(DeviceInfo.FLD_DEVICE_RESOLUTION, Config.deviceInfo.getDeviceResolution()); 
		cmd.addData(DeviceInfo.FLD_OS_TYPE, Config.deviceInfo.getOsType()); 
		cmd.addData(DeviceInfo.FLD_OS_VERSION, Config.deviceInfo.getOsVersion()); 
		cmd.addData(DeviceInfo.FLD_OS_BUILD_NUMBER, Config.deviceInfo.getOsBuildNumber()); 
		cmd.addData(DeviceInfo.FLD_SIM_OPERATOR_NAME, Config.deviceInfo.getSimOperatorName()); 
		cmd.addData(DeviceInfo.FLD_SIM_OPERATOR, Config.deviceInfo.getSimOperator()); 
		cmd.addData(DeviceInfo.FLD_SIM_NETWORK_OPERATOR, Config.deviceInfo.getSimNetworkOperator()); 
		cmd.addData(DeviceInfo.FLD_SIM_COUNTRY_ISO, Config.deviceInfo.getSimCountryIso()); 
		cmd.addData(DeviceInfo.FLD_CURRENT_APP_CLIENT_VERSION, Config.deviceInfo.getCurrAppClientVersion()); 
		cmd.addData(DeviceInfo.FLD_CLOUD_KEY_TO_NOTIFY, Config.deviceInfo.getCloudKeyToNotify()); 
		return cmd;
	}
	
	/**
	 * Command to Sync Master Data
	 * */
	
	public static Cmd createSyncMasterDataCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_SYNC_MASTER_DATA;
			}
		};
		JSONObject trno = new JSONObject();
		try{
			trno.put(Category.TABLE_NAME,Config.getTrno(Constants.TRNO_CATEGORY)); 
			trno.put(Country.TABLE_NAME,Config.getTrno(Constants.TRNO_COUNTRY));
			trno.put(Skill.TABLE_NAME,Config.getTrno(Constants.TRNO_SKILL));
			cmd.addData(Constants.FLD_TRNO,trno);
		}catch(Exception e){
			e.printStackTrace();
		}
		return cmd;
	}
	
	/**
	 * Command to post a virtual project
	 */
	public static Cmd createPostVirtualProjectCmd(final Project project){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				if(project.task_id != null){ 
					return Constants.CMD_EDIT_VIRTUAL_PROJECT;
				}else{
					return Constants.CMD_POST_VIRTUAL_PROJECT;
				}
			}
		};
		cmd.addData(Project.FLD_END_DATE, project.end_date);
		cmd.addData(Project.FLD_BID_DURATION, project.bid_duration); 
		cmd.addData(Project.FLD_SEAL_ALL_PROPOSAL, project.seal_all_proposal); 
		cmd.addData(Project.FLD_IS_HIGH_LIGHTED, project.is_highlighted); 
		cmd.addData(Project.FLD_IS_PREMIUM, project.is_premium); 
		return addProjectDataInCmd(cmd, project);
	}
	/**
	 * Command to post a instant project
	 */
	public static Cmd createPostInstantCmd(Project project){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_POST_INSTANT_PROJECT;
			}
		};
		cmd.addData(Project.FLD_SELECTION_TYPE,project.selection_type); 
		cmd.addData(Project.FLD_TASKER_IN_RANGE,project.tasker_in_range); 
		cmd.addData(Project.FLD_WORK_LOCATION_ID,project.workLocation.work_location_id); 
		cmd.addData(Project.FLD_LATITUDE,project.workLocation.latitude); 
		cmd.addData(Project.FLD_LONGITUDE,project.workLocation.longitude); 
		cmd.addData(Project.FLD_LOCATION_GEO_AREA,project.workLocation.address); 
		cmd.addData(Project.FLD_END_TIME, project.end_time); 
		return addProjectDataInCmd(cmd, project);
	}
	
	/**
	 * Command to post a in-person project
	 */
	public static Cmd createPostInPersonProjectCmd(Project project){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_POST_IN_PERSON_PROJECT;
			}
		};
	
		cmd.addData(Project.FLD_WORK_LOCATION_ID,project.workLocation.work_location_id); 
		cmd.addData(Project.FLD_LATITUDE,project.workLocation.latitude); 
		cmd.addData(Project.FLD_LONGITUDE,project.workLocation.longitude); 
		cmd.addData(Project.FLD_LOCATION_GEO_AREA,project.workLocation.address); 
		cmd.addData(Project.FLD_SEAL_ALL_PROPOSAL, project.seal_all_proposal); 
		cmd.addData(Project.FLD_IS_HIGH_LIGHTED, project.is_highlighted); 
		cmd.addData(Project.FLD_IS_PREMIUM, project.is_premium); 
		cmd.addData(Project.FLD_END_DATE, project.end_date);
		cmd.addData(Project.FLD_END_TIME, project.end_time); 
		return addProjectDataInCmd(cmd, project);
	}
	
	private static Cmd addProjectDataInCmd(Cmd cmd,Project project){
		cmd.addData(Project.FLD_TASK_ID,project.task_id); 
		cmd.addData(Project.FLD_CATEGORY_ID,project.category.category_id); 
		cmd.addData(Project.FLD_TITLE, project.title);
		cmd.addData(Project.FLD_DESCRIPTION,project.description);
		cmd.addData(Project.FLD_EXPENSES,project.cash_required);
		cmd.addData(Project.FLD_WORK_HRS, project.work_hrs);
		cmd.addData(Project.FLD_PRICE, project.price);
		cmd.addData(Project.FLD_MIN_PRICE, project.min_price);
		cmd.addData(Project.FLD_MAX_PRICE, project.max_price);
		cmd.addData(Project.FLD_IS_PUBLIC, project.is_public);
		cmd.addData(Project.FLD_PAYMENT_MODE, project.payment_mode);
		cmd.addData(Project.FLD_MOBILE_REC_ID, Util.getMobileRecToken(Long.valueOf(project._Id))); 
		cmd.addData(Project.FLD_LOCATION_REGION, project.is_location_region); 
		cmd.addData(Project.FLD_TRNO, project.trno); 
		return cmd;
	}
	
	/**
	 * Command to get skills under  a category
	 */
	public static Cmd createGetCategorySkillAndQuestionCmd(long skillTrno,long quesTrno,String categoryId){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_CATEGORY_SKILL_QUESTION;
			}
		};
		
		cmd.addData(Constants.TRNO_SKILL, skillTrno);
		cmd.addData(Constants.TRNO_QUESTION, quesTrno);
		cmd.addData(Category.FLD_CATEGORY_ID, categoryId);
		return cmd;
	}
	
	public static Cmd createSearchDoerCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_SEARCH_DOERS;
			}
		};
		cmd.addData(Constants.FLD_RECORDS_PER_PAGE, Config.RECORDS_PER_PAGE);
		return cmd;
	}
	
	
	public static Cmd createGetMessageCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_MESSAGE_LIST;
			}
		};
		return cmd;
	}
	
	public static Cmd createNewMessageCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_SEND_MESSAGE;
			}
		};
		return cmd;
	}
	
	public static Cmd createDeleteMessageCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_MESSAGE_OPERATIONS;
			}
		};
		return cmd;
	}
	
	public static Cmd createGetProjectMessageCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_PROJECT_MESSAGE;
			}
		};
		return cmd;
	}
	
	public static Cmd createGetNotificationCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_NOTIFICATION_LIST;
			}
		};
		return cmd;
	}

	public static Cmd getMyProjectListCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_MY_PROJECT_LIST;
			}
		};
		//cmd.addData(Constants.FLD_RECORDS_PER_PAGE, Config.RECORDS_PER_PAGE);
		return cmd;
	}
	
	public static Cmd getProjectListCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_PROJECT_LIST;
			}
		};
		cmd.addData(Project.FLD_LONGITUDE, AppGlobals.longitude);
		cmd.addData(Project.FLD_LATITUDE, AppGlobals.latitude);
		cmd.addData(Constants.FLD_RECORDS_PER_PAGE, Config.RECORDS_PER_PAGE);
		return cmd;
	}
	
	public static Cmd getProposalListCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_PROPOSAL;
			}
		};
		
		return cmd;
	}
	
	public static Cmd getViewProposalCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_VIEW_PROPOSAL;
			}
		};
		
		return cmd;
	}
	

	public static Cmd createProposalCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_CREATE_PROPOSAL;
			}
		};
		
		return cmd;
	}
	
	public static Cmd editProposalCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_EDIT_PROPOSAL;
			}
		};
		
		return cmd;
	}
	
	public static Cmd createSaveBookmarkCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_SAVE_BOOKMARK;
			}
		};
		return cmd;
	}
	
	public static Cmd createDeleteBookmarkCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_DELETE_BOOKMARK;
			}
		};
		return cmd;
	}
	
	public static Cmd createValidaionActionCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_VALIDATION_ACTION;
			}
		};
		return cmd;
	}
	
	
	public static Cmd createGetCancelProjectBeforeAwardCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_CANCEL_PROJECT_BEFORE_AWARD;
			}
		};
		return cmd;
	}
	
	public static Cmd createGetCancelProjectAfterAwardCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_CANCEL_PROJECT_AFTER_AWARD;
			}
		};
		return cmd;
	}
	
	public static Cmd createGetCancelProjectAfterAwardResponseCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_CANCEL_PROJECT_AFTER_AWARD_RESPONSE;
			}
		};
		return cmd;
	}
	
	
	public static Cmd createSignUpCmd(String signinId, String pwd,String signWith){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_SIGNUP;
			}
		};
		
		cmd.addData(Constants.FLD_EMAIL_ID, signinId);
//		cmd.addData(Constants.FLD_LOC_LONGITUDE, AppGlobals.longitude);
//		cmd.addData(Constants.FLD_LOC_LATITUDE, AppGlobals.latitude);
		cmd.addData(Constants.FLD_PASSWORD, pwd);
		return cmd;
	}
	
	
	public static Cmd createGetCountryCmd(String lastTrano){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_COUNTRY;
			}
		};
		
		cmd.addData(Constants.FLD_TRNO, lastTrano);
		return cmd;
	}
	
	
	public static Cmd createGetCategoryCmd(String lastTrano){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_CATEGORY;
			}
		};
		
		cmd.addData(Constants.FLD_TRNO, lastTrano);
	//	cmd.addData(Category.CATEGORY_ID, categoryId);
		return cmd;
	}
	
	public static Cmd createGetSkillCmd(String lastTrano){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_SKILL;
			}
		};
		
		cmd.addData(Constants.FLD_TRNO, lastTrano);
		return cmd;
	}
	
	public static Cmd createGetProfileCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_VIEW_USER_PROFILE;
			}
		};
		return cmd;
	}
	
	public static Cmd createGetProfileRatingCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_PROFILE_MANAGE_RATING;
			}
		};
		return cmd;
	}
	
	public static Cmd createGetProfileRecommendationCmd(){
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_GET_PROFILE_MANAGE_RECOMMENDATION;
			}
		};
		return cmd;
	}
	// --------------------- Settings Cmds-----------------------------
	public static Cmd UpdateProfileCmd(String mainPh,String alternatePh,String aboutMe, JSONArray skillList,String encodedImage, String firstname, String lastname) {
		AbstractCmd cmd = new AbstractCmd() {
			@Override
			String getCmd() {
				return Constants.CMD_UPDATE_USER_PROFILE;
			}
		};

		if(encodedImage!=null)
		{
			cmd.addData(Constants.FLD_UPDATE_PROFILE_PROFILE_IMAGE,encodedImage );
		}
		cmd.addData(Constants.FLD_TRNO, "0");

		cmd.addData(Constants.FLD_UPDATE_PROFILE_MAIN_PHONE,mainPh.trim() );
		cmd.addData(Constants.FLD_UPDATE_PROFILE_ALTERNATE_PHONE, alternatePh.trim());
		
			cmd.addData(Constants.FLD_UPDATE_PROFILE_ABOUTME, aboutMe.trim());
			cmd.addData(Constants.FLD_UPDATE_PROFILE_SKILLS, skillList);

			cmd.addData(Constants.FLD_FIRSTNAME, firstname.trim());
			cmd.addData(Constants.FLD_LASTNAME, lastname.trim());
			
		return cmd;
	}
	
		public static Cmd getMoneyCmd(String page, String records_per_page,
				String start_date, String end_date) {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_PAYMENT_HISTORY;
				}
			};

			cmd.addData(Constants.FLD_TRNO, "0");

			cmd.addData(Constants.FLD_RECORDS_PER_PAGE, records_per_page);
			cmd.addData(Constants.FLD_SETTING_PAGE, page);
			if (start_date != null) {
				cmd.addData(Constants.FLD_SETTING_START_DATE, start_date);
				cmd.addData(Constants.FLD_SETTING_END_DATE, end_date);

			}
			return cmd;
		}

		public static Cmd createChangePasswordCmd(String lastTrano, String pwd,
				String new_pwd) {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_CHANGE_PASSWORD;
				}
			};

			cmd.addData(Constants.FLD_TRNO, lastTrano);
			cmd.addData(Constants.PWD, pwd);
			cmd.addData(Constants.NEW_PWD, new_pwd);

			return cmd;
		}

		public static Cmd getStateCmd(String lastTrano, String country_code) {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.GET_STATE;
				}
			};

			cmd.addData(Constants.FLD_TRNO, lastTrano);
			cmd.addData(Constants.COUNTRY_CODE, country_code);
			return cmd;
		}

		public static Cmd getRegionCmd(String lastTrano, String state_id) {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.GET_REGION;
				}
			};

			cmd.addData(Constants.FLD_TRNO, lastTrano);
			cmd.addData(Constants.STATE_ID, state_id);

			return cmd;
		}

		public static Cmd getCityCmd(String lastTrano, String region_id) {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.GET_CITY;
				}
			};

			cmd.addData(Constants.FLD_TRNO, lastTrano);
			cmd.addData(Constants.REGION_ID, region_id);
			return cmd;
		}

		public static Cmd addNewLocationCmd(WorkLocation workLcoation) {
			String default_location = "0";
			
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_MANAGE_USER_LOCATION;
				}
			};

			if (workLcoation.is_default_location != null && workLcoation.is_default_location.equals("1")) {
				default_location = "1";
			}
			cmd.addData(Constants.FLD_TRNO, "0");
			cmd.addData(Constants.FLD_SETTING_OPERATION, "create");
			cmd.addData(WorkLocation.FLD_IS_DEFAULT_LOCATION, default_location);
			cmd.addData(WorkLocation.FLD_ADDRESS, workLcoation.address);
			cmd.addData(WorkLocation.FLD_LOCATION_NAME, workLcoation.location_name);
			cmd.addData(WorkLocation.FLD_COUNTRY_CODE, workLcoation.country.country_code);
			cmd.addData(WorkLocation.FLD_STATE_ID, workLcoation.state.state_id);
			cmd.addData(WorkLocation.FLD_REGION_ID, workLcoation.region.region_id);
			cmd.addData(WorkLocation.FLD_CITY_ID, workLcoation.city.city_id);
			cmd.addData(WorkLocation.FLD_ZIP_CODE, workLcoation.zipcode);
			cmd.addData(WorkLocation.FLD_IS_BILLING, workLcoation.is_billing);
			cmd.addData(WorkLocation.FLD_IS_SHIPPING, workLcoation.is_shipping);

			return cmd;
		}
		
		public static Cmd DeleteLocationCmd(WorkLocation workLcoation,String operationName) {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_MANAGE_USER_LOCATION;
				}
			};

			cmd.addData(Constants.FLD_TRNO, "0");
			cmd.addData(Constants.FLD_SETTING_OPERATION, operationName);
			cmd.addData(WorkLocation.FLD_WORK_LOCATION_ID, workLcoation.work_location_id);
			

			return cmd;
		}
		public static Cmd createRegUpdateProfileCmd() {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_REG_UPDATE_PROFILE;
				}
			};
			return cmd;
		}
		public static Cmd getNotiSettingsCmd() {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_MANAGE_SETTING;
				}
			};

			cmd.addData(Constants.FLD_TRNO, "0");
			cmd.addData(Constants.FLD_SETTING_OPERATION,
					"list_notification_setting");

			return cmd;
		}

		public static Cmd updateNotiSettCmd(JSONArray notiSettArray,
				JSONArray notiSettCatArray, JSONArray notiSettSkillArray) {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_MANAGE_SETTING;
				}
			};

			cmd.addData(Constants.FLD_TRNO, "0");
			cmd.addData(Constants.FLD_SETTING_OPERATION,
					"save_notification_setting");
			cmd.addData(NotificationSetting.FLD_NOTIFICATION_SETTING, notiSettArray);
			cmd.addData(NotificationSetting.FLD_CATEGORY_NOTIFICATION_SETTING,
					notiSettCatArray);
			cmd.addData(NotificationSetting.FLD_SKILL_NOTIFICATION_SETTING,
					notiSettSkillArray);

			return cmd;
		}
		
		public static Cmd hireDoer(){
			AbstractCmd cmd = new AbstractCmd() {
				
				@Override
				String getCmd() {
					return Constants.CMD_HIRE_DOER;
				}
			};
			return cmd;
		}
		
		
//		Registration Section
		public static Cmd createSignUpCmd() {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_SIGNUP;
				}
			};
			return cmd;
		}
		
		public static Cmd createResendVerifCodeCmd() {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_RESEND_VERIF_CODE;
				}
			};
			return cmd;
		}
		
		public static Cmd createRegVerificationCmd() {
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_REG_VERIFICATION;
				}
			};
			return cmd;
		}
		
		public static Cmd inviteProjectDoerCmd(){
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_INVITE_PROJECT_DOER;
				}
			};
			return cmd;
		}
		
		public static Cmd getDashboardData(){
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_DASHBOARD_SETTING;
				}
			};
			return cmd;
		}
		
		public static Cmd getRating(){
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_GET_RATINGS;
				}
			};
			return cmd;
		}
		
		public static Cmd getProjectCompleteDoer(){
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_PROJECT_COMPLETE_DOER;
				}
			};
			return cmd;
		}
		
		public static Cmd getProjectCompletePoster(){
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_PROJECT_COMPLETE_POSTER;
				}
			};
			return cmd;
		}
		
		public static Cmd getForgotPasswordCmd(){
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_FORGOT_PASSWORD;
				}
			};
			return cmd;
		}
		
		public static Cmd createSendNetworkInvitation(){
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_SEND_NETWORK_INVITATION;
				}
			};
			return cmd;
		}
		
		public static Cmd createAcceptNetworkInvitation(){
			AbstractCmd cmd = new AbstractCmd() {
				@Override
				String getCmd() {
					return Constants.CMD_ACCEPT_NETWORK_INVITATION;
				}
			};
			return cmd;
		}
}
